package com.cephonodes.yuki.domain

import kotlinx.serialization.Serializable

@Serializable
data class Student(val id: Int, val name: String, val loginId: String, val classroom: Classroom)