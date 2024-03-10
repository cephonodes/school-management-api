package com.cephonodes.yuki.plugins

import com.cephonodes.yuki.infrastructure.repository.StudentRepositoryPostgreSql
import com.cephonodes.yuki.presentation.StudentController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/students") {
            val studentController = StudentController(StudentRepositoryPostgreSql())
            val (statusCode, body) = studentController.searchStudents(call.request.queryParameters)
            if (statusCode != HttpStatusCode.OK || body == null) {
                call.respond(statusCode)
            } else {
                call.respond(statusCode, body)
            }
        }
    }
}
