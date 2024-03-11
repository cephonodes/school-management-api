package com.cephonodes.yuki.presentation

import com.cephonodes.yuki.domain.Student
import kotlinx.serialization.Serializable

/**
 * 生徒の検索のレスポンスボディの定義
 */
@Serializable
data class SearchStudentsResponseBody(
    val students: List<Student>,
    val totalCount: Int
)
