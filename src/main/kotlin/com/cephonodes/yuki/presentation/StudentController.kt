package com.cephonodes.yuki.presentation

import com.cephonodes.yuki.application.SearchStudentsUseCase
import com.cephonodes.yuki.domain.FilterBy
import com.cephonodes.yuki.domain.IStudentRepository
import com.cephonodes.yuki.domain.SortBy
import com.cephonodes.yuki.domain.SortOrder
import io.ktor.http.*

class StudentController(private val studentRepository: IStudentRepository) {
    private val searchStudentsUseCase = SearchStudentsUseCase(studentRepository)

    fun searchStudents(queryParameters: Parameters): Pair<HttpStatusCode, SearchStudentsResponseBody> {
        val sortBy = (queryParameters["sort"]).let {
            when (it) {
                "name" -> {
                    SortBy.NAME
                }
                "loginId" -> {
                    SortBy.LOGIN_ID
                }
                else -> {
                    SortBy.NAME
                }
            }
        }
        val sortOrder = (queryParameters["order"]).let {
            when (it) {
                "asc" -> {
                    SortOrder.ASC
                }
                "desc" -> {
                    SortOrder.DESC
                }
                else -> {
                    SortOrder.ASC
                }
            }
        }
        val nameFilter = queryParameters["name_like"]
        val loginIdFilter = queryParameters["loginId_like"]
        val (filterBy, filterQuery) =
            if (nameFilter != null && loginIdFilter != null) {
                Pair(FilterBy.NAME, nameFilter)
            } else if (nameFilter != null) {
                Pair(FilterBy.NAME, nameFilter)
            } else if (loginIdFilter != null) {
                Pair(FilterBy.LOGIN_ID, loginIdFilter)
            } else {
                Pair(FilterBy.NAME, "")
            }

        val students = this.searchStudentsUseCase.execute(
            queryParameters["facilitator_id"]?.toInt() ?: 1,
            sortBy,
            sortOrder,
            filterBy,
            filterQuery,
            queryParameters["page"]?.toInt() ?: 1,
            queryParameters["limit"]?.toInt() ?:1
        )
        val body = SearchStudentsResponseBody(
            students,
            students.size
        )
        return Pair(HttpStatusCode.OK, body)
    }
}