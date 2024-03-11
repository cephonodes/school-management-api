package com.cephonodes.yuki.plugins

import com.cephonodes.yuki.infrastructure.repository.StudentRepositorySql
import com.cephonodes.yuki.presentation.StudentController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/students") {
            val studentController = StudentController(StudentRepositorySql(
                // TODO: ハードコーディングではなく外部のファイル等から読み込むようにしたい
                dbUrl = "jdbc:postgresql://db:5432/school",
                dbDriver = "org.postgresql.Driver",
                dbUser = "postgres",
                dbPassword = null
            ))
            val (statusCode, body) = studentController.searchStudents(call.request.queryParameters)
            if (statusCode != HttpStatusCode.OK || body == null) {
                call.respond(statusCode)
            } else {
                call.respond(statusCode, body)
            }
        }
    }
}
