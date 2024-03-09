package com.cephonodes.yuki.application

import com.cephonodes.yuki.domain.*

class SearchStudentsUseCase(private val studentRepository: IStudentRepository) {
    fun execute(
        facilitatorID: Int,
        sortBy: SortBy,
        sortOrder: SortOrder,
        filterBy: FilterBy,
        filterQuery: String,
        page: Int,
        limit: Int
    ): List<Student> {
        val students = this.studentRepository.search(facilitatorID, sortBy, sortOrder, filterBy, filterQuery)

        // ページネーション
        val fromIndex = limit * (page - 1)
        if (fromIndex >= students.size) {
            throw IllegalArgumentException("fromIndex is invalid: $fromIndex")
        }

        val toIndex = (fromIndex + limit).let {
            if (it > students.size) {
                students.size
            } else {
                it
            }
        }
        return students.subList(fromIndex, toIndex)
    }
}