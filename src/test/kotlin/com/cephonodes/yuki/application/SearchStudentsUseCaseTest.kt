package com.cephonodes.yuki.application

import com.cephonodes.yuki.domain.*
import com.cephonodes.yuki.infrastructure.repository.StudentRepositoryInMemory
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchStudentsUseCaseTest {
    @ParameterizedTest
    @MethodSource("dataProvider")
    fun 正常系(
        page: Int,
        limit: Int,
        expected: List<Student>
    ) {
        val parameters = SearchStudentsUseCaseParameters(
            facilitatorID = 1,
            sortBy = SortBy.NAME,
            sortOrder = SortOrder.ASC,
            filterBy = FilterBy.NAME,
            filterQuery = "abc",
            page = page,
            limit = limit
        )

        // テスト対象を実行する
        val useCase = SearchStudentsUseCase(StudentRepositoryInMemory())
        val result = useCase.execute(parameters)

        // 検証する
        assertEquals(expected, result)
    }

    private fun dataProvider() = listOf(
        arguments(
            1,
            1,
            listOf(
                Student(1, "田辺 忠明", "tanabe123", Classroom(1, "特進クラスA"))
            )
        ),
        arguments(
            2,
            1,
            listOf(
                Student(2, "亀岡 正悟", "kameoka123", Classroom(1, "特進クラスA"))
            )
        ),
        arguments(
            1,
            2,
            listOf(
                Student(1, "田辺 忠明", "tanabe123", Classroom(1, "特進クラスA")),
                Student(2, "亀岡 正悟", "kameoka123", Classroom(1, "特進クラスA"))
            )
        ),
        arguments(
            2,
            2,
            listOf(
                Student(3, "品川 文佳", "shiagawa123", Classroom(1, "特進クラスA")),
                Student(4, "笹尾 幸恵", "sasao123", Classroom(2, "特進クラスB"))
            )
        ),
        arguments(
            3,
            2,
            listOf(
                Student(5, "山田 安夫", "kameoka123", Classroom(2, "特進クラスB")),
            )
        )
    )

    @Test
    fun 正常系_取得できたデータが0件の時() {
        val facilitatorID = 1
        val sortBy = SortBy.NAME
        val sortOrder = SortOrder.ASC
        val page = 4
        val limit = 2
        val filterBy = FilterBy.NAME
        val filterQuery = "abc"
        val parameters = SearchStudentsUseCaseParameters(
            facilitatorID = facilitatorID,
            sortBy = sortBy,
            sortOrder = sortOrder,
            filterBy = filterBy,
            filterQuery = filterQuery,
            page = page,
            limit = limit
        )

        // モックを作成する
        val repository = mockkClass(StudentRepositoryInMemory::class)
        every { repository.search(
            facilitatorID = facilitatorID,
            sortBy = sortBy,
            sortOrder = sortOrder,
            filterBy = filterBy,
            filterQuery = filterQuery
        ) } returns listOf()

        // テスト対象を実行する
        val useCase = SearchStudentsUseCase(repository)
        val result = useCase.execute(parameters)

        // 検証する
        val expected: List<Student> = listOf()
        assertEquals(expected, result)
    }

    @Test
    fun 異常系_存在しないページが指定された時() {
        val parameters = SearchStudentsUseCaseParameters(
            facilitatorID = 1,
            sortBy = SortBy.NAME,
            sortOrder = SortOrder.ASC,
            filterBy = FilterBy.NAME,
            filterQuery = "abc",
            page = 4,
            limit = 2
        )

        val useCase = SearchStudentsUseCase(StudentRepositoryInMemory())
        assertThrows<IllegalArgumentException> {
            useCase.execute(parameters)
        }
    }
}