package com.cephonodes.yuki.domain

interface IStudentRepository {
    fun search(facilitatorID: Int, sortBy: SortBy, sortOrder: SortOrder, filterBy: FilterBy, filterQuery: String): List<Student>
}