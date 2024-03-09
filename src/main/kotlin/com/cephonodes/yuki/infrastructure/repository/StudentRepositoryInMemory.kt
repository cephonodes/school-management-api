package com.cephonodes.yuki.infrastructure.repository

import com.cephonodes.yuki.domain.*

class StudentRepositoryInMemory : IStudentRepository {
    override fun search(
        facilitatorID: Int,
        sortBy: SortBy,
        sortOrder: SortOrder,
        filterBy: FilterBy,
        filterQuery: String
    ): List<Student> {
        val classA = Classroom(1, "特進クラスA")
        val classB = Classroom(2, "特進クラスB")
        return listOf(
            Student(1, "田辺 忠明", "tanabe123", classA),
            Student(2, "亀岡 正悟", "kameoka123", classA),
            Student(3, "品川 文佳", "shiagawa123", classA),
            Student(4, "笹尾 幸恵", "sasao123", classB),
            Student(5, "山田 安夫", "kameoka123", classB),
        )
    }
}