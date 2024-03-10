package com.cephonodes.yuki.infrastructure.repository

import com.cephonodes.yuki.domain.*
import com.cephonodes.yuki.domain.SortOrder
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction

object Facilitators : IntIdTable() {
}

object students : IntIdTable() {
    val name = varchar("name", 50)
    val loginID = varchar("login_id", 50)
    val classroomID = integer("classroom_id").references(classrooms.id)
}

object classrooms : IntIdTable() {
    val name = varchar("name", 50)
}

object facilitator_classroom_relation : IntIdTable() {
    val facilitatorID = integer("facilitator_id").references(Facilitators.id)
    val classroomID = integer("classroom_id").references(classrooms.id)
}

class StudentRepositoryPostgreSql : IStudentRepository {
    override fun search(
        facilitatorID: Int,
        sortBy: SortBy?,
        sortOrder: SortOrder?,
        filterBy: FilterBy?,
        filterQuery: String?
    ): List<Student> {
        val studentFilter =
            when (filterBy) {
                null -> booleanLiteral(true)
                FilterBy.NAME -> students.name.like("%$filterQuery%")
                FilterBy.LOGIN_ID -> students.loginID.like("%$filterQuery%")
            }
        val sortCondition =
            when (sortBy) {
                null -> students.name
                SortBy.NAME -> students.name
                SortBy.LOGIN_ID -> students.loginID
            }
        val sqlSortOrder =
            when (sortOrder) {
                null -> org.jetbrains.exposed.sql.SortOrder.ASC
                SortOrder.ASC -> org.jetbrains.exposed.sql.SortOrder.ASC
                SortOrder.DESC -> org.jetbrains.exposed.sql.SortOrder.DESC
            }

        val result = mutableListOf<Student>()
        Database.connect(url = "jdbc:postgresql://db:5432/school", driver = "org.postgresql.Driver", user = "postgres")
        transaction {
            (facilitator_classroom_relation innerJoin classrooms innerJoin students)
                .select(students.id, students.name, students.loginID, classrooms.id, classrooms.name)
                .where {
                    facilitator_classroom_relation.facilitatorID.eq(facilitatorID) and studentFilter
                }
                .orderBy(sortCondition, sqlSortOrder)
                .forEach { row ->
                    result.add(Student(
                        row[students.id].value,
                        row[students.name].toString(),
                        row[students.loginID].toString(),
                        Classroom(
                            row[classrooms.id].value,
                            row[classrooms.name].toString()
                        )
                    ))
                }
        }
        return result
    }
}
