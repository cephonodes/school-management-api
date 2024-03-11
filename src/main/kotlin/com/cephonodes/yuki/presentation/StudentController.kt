package com.cephonodes.yuki.presentation

import com.cephonodes.yuki.application.SearchStudentsUseCase
import com.cephonodes.yuki.application.SearchStudentsUseCaseParameters
import com.cephonodes.yuki.domain.FilterBy
import com.cephonodes.yuki.domain.IStudentRepository
import com.cephonodes.yuki.domain.SortBy
import com.cephonodes.yuki.domain.SortOrder
import io.ktor.http.*

/**
 * コントローラー 生徒の情報
 */
class StudentController(private val studentRepository: IStudentRepository) {
    private val searchStudentsUseCase = SearchStudentsUseCase(studentRepository)

    /**
     * 生徒の情報を検索する
     *
     * @param queryParameters クエリパラメータ
     * @return HTTPステータスコードとレスポンスボディの組
     */
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
        val students = this.searchStudentsUseCase.execute(useCaseParameters)
        val body = SearchStudentsResponseBody(
            students,
            students.size
        )
        return Pair(HttpStatusCode.OK, body)
    }

    /**
     * ソートに関するパラメーターのバリデーションチェック
     *
     * @param order ソート順
     * @param sort ソートキー
     * @return 正規化されたソート順とソートキーの組
     * @throws IllegalArgumentException 不正なパラメーターの組み合わせだった場合
     */
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

    /**
     * ソートに関するパラメーターのバリデーションチェック
     *
     * @param nameLike 生徒の名前に対する検索文字列
     * @param loginIDLike 生徒のログインIDに対する検索文字列
     * @return 正規化された検索対象と検索文字列の組
     * @throws IllegalArgumentException 不正なパラメーターの組み合わせだった場合
     */
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