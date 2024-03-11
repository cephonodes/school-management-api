package com.cephonodes.yuki.infrastructure.repository

import com.cephonodes.yuki.domain.*
import com.cephonodes.yuki.domain.SortOrder
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 先生テーブルのスキーマ
 */
object facilitators : IntIdTable() {
}

/**
 * 生徒テーブルのスキーマ
 */
object students : IntIdTable() {
    val name = varchar("name", 50)
    val loginID = varchar("login_id", 50)
    val classroomID = integer("classroom_id").references(classrooms.id)
}

/**
 * クラステーブルのスキーマ
 */
object classrooms : IntIdTable() {
    val name = varchar("name", 50)
}

/**
 * 先生とクラスの対応関係のテーブルのスキーマ
 */
object facilitator_classroom_relation : IntIdTable() {
    val facilitatorID = integer("facilitator_id").references(facilitators.id)
    val classroomID = integer("classroom_id").references(classrooms.id)
}

/**
 * 生徒のリポジトリの実装 SQL版
 */
class StudentRepositorySql(
    private val dbUrl: String,
    private val dbDriver: String,
    private val dbUser: String,
    private val dbPassword: String?) : IStudentRepository {

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
                null -> null
                SortBy.NAME -> students.name
                SortBy.LOGIN_ID -> students.loginID
            }
        val sqlSortOrder =
            when (sortOrder) {
                null -> null
                SortOrder.ASC -> org.jetbrains.exposed.sql.SortOrder.ASC
                SortOrder.DESC -> org.jetbrains.exposed.sql.SortOrder.DESC
            }

        val result = mutableListOf<Student>()
        if (this.dbPassword != null) {
            Database.connect(url = this.dbUrl, driver = this.dbDriver, user = this.dbUser, password = this.dbPassword)
        } else {
            Database.connect(url = this.dbUrl, driver = this.dbDriver, user = this.dbUser)
        }
        transaction {
            (facilitator_classroom_relation innerJoin classrooms innerJoin students)
                .select(students.id, students.name, students.loginID, classrooms.id, classrooms.name)
                .where {
                    facilitator_classroom_relation.facilitatorID.eq(facilitatorID) and studentFilter
                }.apply {
                    if (sortCondition != null && sqlSortOrder != null) {
                        orderBy(sortCondition, sqlSortOrder)
                    }
                }
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
