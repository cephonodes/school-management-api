package com.cephonodes.yuki.presentation

import com.cephonodes.yuki.application.SearchStudentsUseCase
import com.cephonodes.yuki.domain.FilterBy
import com.cephonodes.yuki.domain.IStudentRepository
import com.cephonodes.yuki.domain.SortBy
import com.cephonodes.yuki.domain.SortOrder
import io.ktor.http.*

class StudentController(private val studentRepository: IStudentRepository) {
    private val searchStudentsUseCase = SearchStudentsUseCase(studentRepository)

    fun searchStudents(queryParameters: Parameters): Pair<HttpStatusCode, SearchStudentsResponseBody?> {
        // バリデーションチェック
        val useCaseParameters: SearchStudentsUseCaseParameters = try {
            queryParameters.run {
                val facilitatorID = requireNotNull(get("facilitator_id")) { "" }.toInt()
                val page = get("page")?.toInt() ?: 1
                val limit = get("limit")?.toInt() ?: 1
                val (order, sort) = this@StudentController.validateSortParameter(get("order"), get("sort"))
                val (filterBy, filterQuery) = this@StudentController.validateFilterParameter(
                    get("name_like"),
                    get("loginId_like")
                )
                SearchStudentsUseCaseParameters(facilitatorID, page, limit, sort, order, filterBy, filterQuery)
            }
        } catch (e: Exception) {
            return Pair(HttpStatusCode.BadRequest, null)
        }

        // ユースケースの実行
        val students = this.searchStudentsUseCase.execute(
            useCaseParameters.facilitatorID,
            useCaseParameters.sortBy,
            useCaseParameters.sortOrder,
            useCaseParameters.filterBy,
            useCaseParameters.filterQuery,
            useCaseParameters.page,
            useCaseParameters.limit
        )
        val body = SearchStudentsResponseBody(
            students,
            students.size
        )
        return Pair(HttpStatusCode.OK, body)
    }

    private fun validateSortParameter(order: String?, sort: String?): Pair<SortOrder?, SortBy?> {
        if (sort == null && order != null) {
            throw IllegalArgumentException()
        }
        return if (sort == null) {
            Pair(null, null)
        } else {
            Pair(
                when (order) {
                    "asc" -> SortOrder.ASC
                    "desc" -> SortOrder.DESC
                    null -> SortOrder.ASC
                    else -> throw IllegalArgumentException()
                },
                when (sort) {
                    "name" -> SortBy.NAME
                    "loginId" -> SortBy.LOGIN_ID
                    else -> throw IllegalArgumentException()
                }
            )
        }
    }

    private fun validateFilterParameter(nameLike: String?, loginIDLike: String?): Pair<FilterBy?, String?> {
        if (nameLike != null && loginIDLike != null) {
            throw IllegalArgumentException()
        }

        return if (nameLike != null) {
            Pair(FilterBy.NAME, nameLike)
        } else if (loginIDLike != null) {
            Pair(FilterBy.LOGIN_ID, loginIDLike)
        } else {
            Pair(null, null)
        }
    }
}