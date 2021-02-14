package dev.yasan.wallpapers

import com.fasterxml.jackson.databind.SerializationFeature
import dev.yasan.wallpapers.artists.ArtistsServiceDB
import dev.yasan.wallpapers.artists.artistsRoutes
import dev.yasan.wallpapers.artists.links.ArtistsLinksServiceDB
import dev.yasan.wallpapers.inspirations.InspirationsServiceDB
import dev.yasan.wallpapers.inspirations.inspirationRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.DateFormat

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

/**
 * Please note that you can use any other name instead of *module*.
 * Also note that you can have more then one modules in your application.
 * */
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = true) {

    val host = "localhost"
    val port = 5555
    val dbName = "artwalls_db"
    val dbUser = "artwalls_user"
    val dbPassword = "artwallspass123"
    val db = Database.connect(
        "jdbc:postgresql://$host:$port/$dbName",
        user = dbUser,
        password = dbPassword
    )

    transaction {
        SchemaUtils.create(InspirationsTable, ArtistsTable, ArtistLinksTable, CollectionsTable, WallpapersTable)
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateInstance()
        }
    }

    wallpaperRoutes()
    inspirationRoutes(InspirationsServiceDB())
    artistsRoutes(ArtistsServiceDB(), ArtistsLinksServiceDB())

}

