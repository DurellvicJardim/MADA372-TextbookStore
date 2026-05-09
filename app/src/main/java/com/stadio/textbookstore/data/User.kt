package com.stadio.textbookstore.data

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val password: String,
    val institution: String,
    val course: String,
    val studentStatus: String
)