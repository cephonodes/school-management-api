package com.cephonodes.yuki.infrastructure.repository

import com.cephonodes.yuki.domain.*
import com.cephonodes.yuki.domain.SortOrder
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction

object Facilitators : IntIdTable() {
}

object Students : IntIdTable() {
    val name = varchar("name", 50)
    val loginID = varchar("login_id", 50)
    val classroomID = integer("classroom_id").references(Classrooms.id)
}

object Classrooms : IntIdTable() {
    val name = varchar("name", 50)
}

object FacilitatorClassroomRelation : IntIdTable() {
    val facilitatorID = integer("facilitator_id").references(Facilitators.id)
    val classroomID = integer("classroom_id").references(Classrooms.id)
}

class StudentRepositoryPostgreSql : IStudentRepository {
    override fun search(facilitatorID: Int, sortBy: SortBy, sortOrder: SortOrder, filterBy: FilterBy, filterQuery: String): List<Student> {
        val studentFilter =
            when (filterBy) {
                FilterBy.NAME -> Students.name.like("%$filterQuery%")
                FilterBy.LOGIN_ID -> Students.loginID.like("%$filterQuery%")
            }
        val sortCondition =
            when (sortBy) {
                SortBy.NAME -> Students.name
                SortBy.LOGIN_ID -> Students.loginID
            }
        val sqlSortOrder =
            when (sortOrder) {
                SortOrder.ASC -> org.jetbrains.exposed.sql.SortOrder.ASC
                SortOrder.DESC -> org.jetbrains.exposed.sql.SortOrder.DESC
            }

        val students = mutableListOf<Student>()
        Database.connect(url = "", driver = "org.postgresql.Driver", user = "", password = "")
        transaction {
            (FacilitatorClassroomRelation innerJoin Students innerJoin Classrooms)
                .select(Students.id, Students.name, Students.loginID, Classrooms.id, Classrooms.name)
                .where {
                    FacilitatorClassroomRelation.facilitatorID.eq(facilitatorID) and studentFilter
                }
                .orderBy(sortCondition, sqlSortOrder)
                .forEach { row ->
                    students.add(Student(
                        row[Students.id].value,
                        row[Students.name].toString(),
                        row[Students.loginID].toString(),
                        Classroom(
                            row[Classrooms.id].value,
                            row[Classrooms.name].toString()
                        )
                    ))
                }
        }
        return students
    }
}
