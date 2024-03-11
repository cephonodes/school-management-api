package com.cephonodes.yuki.domain

import kotlinx.serialization.Serializable

/**
 * ドメイン クラス
 *
 * @property id ID
 * @property name 名前
 */
@Serializable
data class Classroom(val id: Int, val name: String)