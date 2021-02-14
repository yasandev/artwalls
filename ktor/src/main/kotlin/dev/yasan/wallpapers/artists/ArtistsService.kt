package dev.yasan.wallpapers.artists

import dev.yasan.wallpapers.ArtistsTable
import dev.yasan.wallpapers.artists.links.ArtistLink
import dev.yasan.wallpapers.artists.links.ArtistsLinksServiceDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface ArtistsService {

    suspend fun create(name: String, fileName: String, hasDarkPicture: Boolean, commissions: Boolean): Int

    suspend fun all(linksServiceDB: ArtistsLinksServiceDB): List<Artist>

    suspend fun findById(id: Int, linksServiceDB: ArtistsLinksServiceDB): Artist?

    suspend fun delete(id: Int): Int

}

class ArtistsServiceDB : ArtistsService {

    override suspend fun create(name: String, fileName: String, hasDarkPicture: Boolean, commissions: Boolean): Int {
        val id = transaction {
            ArtistsTable.insertAndGetId { artist ->
                artist[ArtistsTable.name] = name
                artist[ArtistsTable.fileName] = fileName
                artist[ArtistsTable.hasDarkPicture] = hasDarkPicture
                artist[ArtistsTable.commissions] = commissions
            }
        }
        return id.value
    }

    override suspend fun all(linksServiceDB: ArtistsLinksServiceDB): List<Artist> {
        val dJob = GlobalScope.async {
            transaction {
                ArtistsTable.selectAll().map { row ->
                    async {
                        row.asArtist(linksServiceDB)
                    }
                }
            }
        }
        return dJob.await().awaitAll()
    }

    override suspend fun findById(id: Int, linksServiceDB: ArtistsLinksServiceDB): Artist? {
        val row = transaction {
            addLogger(StdOutSqlLogger)
            ArtistsTable.select {
                ArtistsTable.id eq id
            }.firstOrNull()
        }
        return row?.asArtist(linksServiceDB)
    }

    override suspend fun delete(id: Int): Int {
        return transaction {
            ArtistsTable.deleteWhere {
                ArtistsTable.id eq id
            }
        }
    }

}

suspend fun ResultRow.asArtist(linksServiceDB: ArtistsLinksServiceDB): Artist {
    val artistId = this[ArtistsTable.id].value

    val jobDeferred = GlobalScope.async {
        linksServiceDB.findByArtistId(artistId)
    }

    val links = jobDeferred.await()

    return Artist(
        artistId,
        this[ArtistsTable.name],
        this[ArtistsTable.fileName],
        this[ArtistsTable.hasDarkPicture],
        this[ArtistsTable.commissions],
        links
    )
}


data class Artist(
    val id: Int,
    val name: String,
    val fileName: String,
    val hasDarkPicture: Boolean,
    val commissions: Boolean,
    val links: List<ArtistLink>
)