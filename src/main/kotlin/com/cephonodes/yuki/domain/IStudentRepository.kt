package com.cephonodes.yuki.domain

/**
 * 生徒のリポジトリのインターフェース定義
 */
interface IStudentRepository {
    /**
     * 生徒の情報を検索する
     * @param facilitatorID 先生のID
     * @param sortBy ソートキー
     * @param sortOrder ソート順
     * @param filterBy 検索対象の属性
     * @param filterQuery 検索文字列（部分一致）
     * @return 生徒の情報の配列
     */
    fun search(
        facilitatorID: Int,
        sortBy: SortBy?,
        sortOrder: SortOrder?,
        filterBy: FilterBy?,
        filterQuery: String?
    ): List<Student>
}