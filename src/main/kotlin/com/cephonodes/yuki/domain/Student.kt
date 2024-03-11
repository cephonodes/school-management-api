package com.cephonodes.yuki.domain

import kotlinx.serialization.Serializable

/**
 * ドメイン 生徒
 *
 * @property id ID
 * @property name 名前
 * @property loginId ログインID
 * @property classroom クラスの情報
 */
@Serializable
data class Student(val id: Int, val name: String, val loginId: String, val classroom: Classroom)