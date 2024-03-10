package com.cephonodes.yuki.presentation

import com.cephonodes.yuki.application.SearchStudentsUseCase
import com.cephonodes.yuki.domain.*
import com.cephonodes.yuki.infrastructure.repository.StudentRepositoryInMemory
import io.ktor.http.*
import io.mockk.*
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class ExpectedUseCaseInput(
    val facilitatorID: Int,
    val sortOrder: SortOrder?,
    val page: Int,
    val limit: Int,
    val sortBy: SortBy?,
    val filterBy: FilterBy?,
    val filterQuery: String?
)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentControllerTest {
    @ParameterizedTest
    @MethodSource("dataProvider")
    fun 正常系(
        queryParameters: Parameters,
        expectedUseCaseInput: ExpectedUseCaseInput,
        useCaseResult: List<Student>,
        expectedBody: SearchStudentsResponseBody
    ) {
        // モックを作成する
        mockkConstructor(SearchStudentsUseCase::class)
        every { anyConstructed<SearchStudentsUseCase>().execute(
            facilitatorID = expectedUseCaseInput.facilitatorID,
            sortOrder = expectedUseCaseInput.sortOrder,
            page = expectedUseCaseInput.page,
            limit = expectedUseCaseInput.limit,
            sortBy = expectedUseCaseInput.sortBy,
            filterBy = expectedUseCaseInput.filterBy,
            filterQuery = expectedUseCaseInput.filterQuery
        ) } returns useCaseResult

        // テスト対象を実行する
        val controller = StudentController(StudentRepositoryInMemory())
        val (statusCode, body) = controller.searchStudents(queryParameters)

        // 検証する
        assertEquals(HttpStatusCode.OK, statusCode)
        assertEquals(expectedBody, body)

        unmockkConstructor(SearchStudentsUseCase::class)
    }

    private fun dataProvider() = listOf(
        arguments(
            Parameters.build {
                append("facilitator_id", "1")
                append("page", "1")
                append("limit", "2")
                append("sort", "name")
                append("order", "asc")
                append("name_like", "田辺")
            },
            ExpectedUseCaseInput(
                1,
                SortOrder.ASC,
                1,
                2,
                SortBy.NAME,
                FilterBy.NAME,
                "田辺"
            ),
            listOf(
                Student(1, "田辺 忠明", "tanabe123", Classroom(1, "特進クラス1"))
            ),
            SearchStudentsResponseBody(
                listOf(
                    Student(
                        1,
                        "田辺 忠明",
                        "tanabe123",
                        Classroom(1, "特進クラス1")
                    )
                ),
                1
            )
        ),
        arguments(
            Parameters.build {
                append("facilitator_id", "2")
                append("page", "2")
                append("limit", "1")
                append("sort", "loginId")
                append("order", "desc")
                append("loginId_like", "123")
            },
            ExpectedUseCaseInput(
                2,
                SortOrder.DESC,
                2,
                1,
                SortBy.LOGIN_ID,
                FilterBy.LOGIN_ID,
                "123"
            ),
            listOf(
                Student(1, "田辺 忠明", "tanabe123", Classroom(1, "特進クラス1")),
                Student(2, "亀岡 正悟", "kameoka123", Classroom(2, "特進クラス2"))
            ),
            SearchStudentsResponseBody(
                listOf(
                    Student(
                        1,
                        "田辺 忠明",
                        "tanabe123",
                        Classroom(1, "特進クラス1")
                    ),
                    Student(
                    2,
                    "亀岡 正悟",
                    "kameoka123",
                    Classroom(2, "特進クラス2")
                    )
                ),
                2
            )
        ),
        arguments(
            Parameters.build {
                append("facilitator_id", "2")
                append("page", "2")
                append("limit", "1")
                append("sort", "loginId")
                append("order", "desc")
                append("loginId_like", "123")
            },
            ExpectedUseCaseInput(
                2,
                SortOrder.DESC,
                2,
                1,
                SortBy.LOGIN_ID,
                FilterBy.LOGIN_ID,
                "123"
            ),
            listOf<Student>(),
            SearchStudentsResponseBody(
                listOf(),
                0
            )
        )
    )

    @ParameterizedTest
    @MethodSource("validationTestDataProvider")
    fun バリデーションチェックのテスト(
        queryParameters: Parameters,
        expectedUseCaseInput: ExpectedUseCaseInput?,
        expectedHttpStatusCode: HttpStatusCode
    ) {
        // モックを作成する
        // このテストでは戻り値のうちHTTPステータスコードが確認できればよいので、引数に関わらず適当な結果を返すようにしている
        mockkConstructor(SearchStudentsUseCase::class)
        every { anyConstructed<SearchStudentsUseCase>().execute(
            facilitatorID = any(),
            sortOrder = any(),
            page = any(),
            limit = any(),
            sortBy = any(),
            filterBy = any(),
            filterQuery = any()
        ) } returns listOf(Student(1, "生徒1", "student_1", Classroom(1, "クラス1")))

        // テスト対象を実行する
        val controller = StudentController(StudentRepositoryInMemory())
        val (httpStatusCode, _) = controller.searchStudents(queryParameters)

        // 検証する
        assertEquals(expectedHttpStatusCode, httpStatusCode)

        if (expectedUseCaseInput != null) {
            verify(exactly = 1) {
                anyConstructed<SearchStudentsUseCase>().execute(
                    facilitatorID = expectedUseCaseInput.facilitatorID,
                    sortOrder = expectedUseCaseInput.sortOrder,
                    page = expectedUseCaseInput.page,
                    limit = expectedUseCaseInput.limit,
                    sortBy = expectedUseCaseInput.sortBy,
                    filterBy = expectedUseCaseInput.filterBy,
                    filterQuery = expectedUseCaseInput.filterQuery
                )
            }
        } else {
            verify(exactly = 0) {
                anyConstructed<SearchStudentsUseCase>().execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
        }

        unmockkConstructor(SearchStudentsUseCase::class)
    }

    private fun validationTestDataProvider() = listOf(
        arguments(
            Named.of(
                "facilitator_idがない",
                Parameters.build {
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "asc")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "facilitator_idが数字でない",
                Parameters.build {
                    append("facilitator_id", "abc")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "asc")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "facilitator_idが空文字",
                Parameters.build {
                    append("facilitator_id", "")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "asc")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "pageが指定されていない",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "asc")
                    append("name_like", "123")
                }
            ),
            ExpectedUseCaseInput(
                1,
                SortOrder.ASC,
                1,  // デフォルト値
                1,
                SortBy.NAME,
                FilterBy.NAME,
                "123"
            ),
            HttpStatusCode.OK
        ),
        arguments(
            Named.of(
                "pageが数字でない",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "abc")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "asc")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "limitが指定されていない",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("sort", "name")
                    append("order", "asc")
                    append("name_like", "123")
                }
            ),
            ExpectedUseCaseInput(
                1,
                SortOrder.ASC,
                1,
                1,  // デフォルト値
                SortBy.NAME,
                FilterBy.NAME,
                "123"
            ),
            HttpStatusCode.OK
        ),
        arguments(
            Named.of(
                "limitが数字でない",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "abc")
                    append("sort", "name")
                    append("order", "asc")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "sortもorderも指定されていない",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("name_like", "123")
                }
            ),
            ExpectedUseCaseInput(
                1,
                null,
                1,
                1,
                null,
                FilterBy.NAME,
                "123"
            ),
            HttpStatusCode.OK
        ),
        arguments(
            Named.of(
                "sortが指定されていてorderが指定されていない",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("name_like", "123")
                }
            ),
            ExpectedUseCaseInput(
                1,
                SortOrder.ASC,  // デフォルト値
                1,
                1,
                SortBy.NAME,
                FilterBy.NAME,
                "123"
            ),
            HttpStatusCode.OK
        ),
        arguments(
            Named.of(
                "sortが規定の文字列でない",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "address")
                    append("order", "asc")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "orderが規定の文字列でない",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "none")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "sortが空文字列",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "")
                    append("order", "asc")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "sortが指定されていないのにorderが指定されている",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("order", "asc")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "name_likeが指定されていなくてloginId_likeが指定されている",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "asc")
                    append("loginId_like", "456")
                }
            ),
            ExpectedUseCaseInput(
                1,
                SortOrder.ASC,
                1,
                1,
                SortBy.NAME,
                FilterBy.LOGIN_ID,
                "456"
            ),
            HttpStatusCode.OK
        ),
        arguments(
            Named.of(
                "loginId_likeもname_likeも指定されていない",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "asc")
                }
            ),
            ExpectedUseCaseInput(
                1,
                SortOrder.ASC,  // デフォルト値
                1,
                1,
                SortBy.NAME,
                null,
                null
            ),
            HttpStatusCode.OK
        ),
        arguments(
            Named.of(
                "name_likeとloginId_likeの両方が指定されている",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "asc")
                    append("name_like", "123")
                    append("loginId_like", "123")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        ),
        arguments(
            Named.of(
                "name_likeとloginId_likeの両方が指定されている（空文字列）",
                Parameters.build {
                    append("facilitator_id", "1")
                    append("page", "1")
                    append("limit", "1")
                    append("sort", "name")
                    append("order", "asc")
                    append("name_like", "")
                    append("loginId_like", "")
                }
            ),
            null,
            HttpStatusCode.BadRequest
        )
    )
}