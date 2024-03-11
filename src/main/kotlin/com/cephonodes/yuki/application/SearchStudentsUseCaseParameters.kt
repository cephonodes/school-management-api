package com.cephonodes.yuki.application

import com.cephonodes.yuki.domain.FilterBy
import com.cephonodes.yuki.domain.SortBy
import com.cephonodes.yuki.domain.SortOrder
import kotlinx.serialization.Serializable

/**
 * SearchStudentsUseCaseの入力パラメーター
 *
 * @property facilitatorID 先生のID
 * @property sortBy ソートキー
 * @property sortOrder ソート順
 * @property filterBy 検索対象の属性
 * @property filterQuery 検索文字列（部分一致）
 * @property page 返すページ
 * @property limit 1ページ当たりの件数
 * @return 生徒の情報の配列
 */
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