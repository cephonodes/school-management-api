package com.cephonodes.yuki.presentation

import com.cephonodes.yuki.domain.FilterBy
import com.cephonodes.yuki.domain.SortBy
import com.cephonodes.yuki.domain.SortOrder
import kotlinx.serialization.Serializable

@Serializable
data class SearchStudentsUseCaseParameters (
    val facilitatorID: Int,
    val page: Int,
    val limit: Int,
    val sortBy: SortBy?,
    val sortOrder: SortOrder?,
    val filterBy: FilterBy?,
    val filterQuery: String?
)