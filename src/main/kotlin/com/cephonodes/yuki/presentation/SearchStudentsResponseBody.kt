package com.cephonodes.yuki.presentation

import com.cephonodes.yuki.domain.Student
import kotlinx.serialization.Serializable

@Serializable
data class SearchStudentsResponseBody(
    val students: List<Student>,
    val totalCount: Int
)
