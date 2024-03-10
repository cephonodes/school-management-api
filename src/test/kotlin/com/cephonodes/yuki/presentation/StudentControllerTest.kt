package com.cephonodes.yuki.presentation

import com.cephonodes.yuki.application.SearchStudentsUseCase
import com.cephonodes.yuki.domain.*
import com.cephonodes.yuki.infrastructure.repository.StudentRepositoryInMemory
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class ExpectedUseCaseInput(
    val facilitatorID: Int,
    val sortOrder: SortOrder,
    val page: Int,
    val limit: Int,
    val sortBy: SortBy,
    val filterBy: FilterBy,
    val filterQuery: String
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

        val controller = StudentController(StudentRepositoryInMemory())
        val (statusCode, body) = controller.searchStudents(queryParameters)
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
}