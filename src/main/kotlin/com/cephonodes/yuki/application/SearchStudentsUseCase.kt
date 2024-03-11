package com.cephonodes.yuki.application

import com.cephonodes.yuki.domain.*

/**
 * ユースケース 生徒の情報を検索する
 */
class SearchStudentsUseCase(private val studentRepository: IStudentRepository) {
    /**
     * ユースケースを実行する
     *
     * @param parameters
     * @return 生徒の情報の配列
     */
    fun execute(
        parameters: SearchStudentsUseCaseParameters
    ): List<Student> {
        val students = this.studentRepository.search(
            parameters.facilitatorID,
            parameters.sortBy,
            parameters.sortOrder,
            parameters.filterBy,
            parameters.filterQuery
        )

        if (students.isEmpty()) {
            return students
        }

        // ページネーション
        val fromIndex = parameters.limit * (parameters.page - 1)
        if (fromIndex >= students.size) {
            throw IllegalArgumentException("fromIndex is invalid: $fromIndex")
        }

        val toIndex = (fromIndex + parameters.limit).let {
            if (it > students.size) {
                students.size
            } else {
                it
            }
        }
        return students.subList(fromIndex, toIndex)
    }
}