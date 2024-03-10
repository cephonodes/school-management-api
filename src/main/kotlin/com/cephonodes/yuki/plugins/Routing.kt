package com.cephonodes.yuki.plugins

import com.cephonodes.yuki.infrastructure.repository.StudentRepositoryInMemory
import com.cephonodes.yuki.infrastructure.repository.StudentRepositoryPostgreSql
import com.cephonodes.yuki.presentation.StudentController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/students") {
            val studentController = StudentController(StudentRepositoryInMemory())
            val (statusCode, body) = studentController.searchStudents(call.request.queryParameters)
            call.respond(statusCode, body)
        }
    }
}
