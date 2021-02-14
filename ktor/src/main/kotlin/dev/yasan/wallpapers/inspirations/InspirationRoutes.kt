package dev.yasan.wallpapers.inspirations

import dev.yasan.wallpapers.helper.getIdOrRespond
import dev.yasan.wallpapers.helper.getStringParamOrRespond
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

fun Application.inspirationRoutes(inspirationsService: InspirationsService) {

    routing {

        route("/inspiration") {

            get {
                call.respond(inspirationsService.all())
            }

            get("/{id}") {
                call.respond(inspirationsService.findById(call.getIdOrRespond()) ?: HttpStatusCode.NotFound)
            }

            post {
                with(call) {
                    val parameters: Parameters = receiveParameters()
                    val name = getStringParamOrRespond("name", parameters)
                    val picture = parameters["picture"]?.toBoolean() ?: false
                    val link = parameters["link"]
                    val createdId = inspirationsService.create(name, picture, link)
                    respond(HttpStatusCode.Created, createdId)
                }
            }

            delete("/{id}") {
                with(call) {
                    val deleted = inspirationsService.delete(getIdOrRespond())
                    if (deleted > 0) respond(HttpStatusCode.OK, "Inspiration deleted")
                    else respond(HttpStatusCode.NotFound, "Could not find inspiration to delete")
                }
            }

        }
    }
}



