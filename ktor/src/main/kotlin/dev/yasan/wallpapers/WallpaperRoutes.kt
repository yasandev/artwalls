package dev.yasan.wallpapers

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.wallpaperRoutes() {
    routing {
        route("/wallpaper") {

            wallpapersListRoute()

            wallpaperByIdRoute()

        }
    }
}

fun Route.wallpapersListRoute() {
    get("/") {
        call.respondText("wallpaper")
    }
}

fun Route.wallpaperByIdRoute() {
    get("/{id}") {
        val id = call.parameters["id"]
        call.respondText("wallpaper/$id")
    }
}

