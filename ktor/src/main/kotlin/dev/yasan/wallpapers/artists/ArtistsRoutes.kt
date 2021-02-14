package dev.yasan.wallpapers.artists

import dev.yasan.wallpapers.artists.links.ArtistsLinksServiceDB
import dev.yasan.wallpapers.helper.getIdOrRespond
import dev.yasan.wallpapers.helper.getStringParamOrRespond
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun Application.artistsRoutes(artistsService: ArtistsServiceDB, linksService: ArtistsLinksServiceDB) = runBlocking {

    routing {

        route("/artist") {

            get {
                call.respond(artistsService.all(linksService))
            }

            get("/{id}") {
                call.respond(artistsService.findById(call.getIdOrRespond(),linksService) ?: HttpStatusCode.NotFound)
            }

            post {
                with(call) {
                    val parameters: Parameters = receiveParameters()

                    val name = getStringParamOrRespond("name", parameters)
                    val fileName = getStringParamOrRespond("file_name", parameters)
                    val hasDarkPicture = parameters["picture"]?.toBoolean() ?: false
                    val commissions = parameters["commissions"]?.toBoolean() ?: false

                    val createdId = artistsService.create(name, fileName, hasDarkPicture, commissions)
                    respond(HttpStatusCode.Created, createdId)
                }
            }

            delete("/{id}") {
                with(call) {
                    val deleted = artistsService.delete(getIdOrRespond())
                    if (deleted > 0) respond(HttpStatusCode.OK, "Artist deleted")
                    else respond(HttpStatusCode.NotFound, "Could not find inspiration to delete")
                }
            }

        }
    }
}



