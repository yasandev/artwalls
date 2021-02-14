package dev.yasan.wallpapers.artists.links

import dev.yasan.wallpapers.ArtistLinksTable
import dev.yasan.wallpapers.ArtistsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface ArtistsLinksService {

    suspend fun create(artist: Int, name: String, url: String): Int

    suspend fun all(): List<ArtistLink>

    suspend fun findById(id: Int): ArtistLink?

    suspend fun findByArtistId(id: Int): List<ArtistLink>

    suspend fun delete(id: Int): Int

}

class ArtistsLinksServiceDB : ArtistsLinksService {

    override suspend fun create(artist: Int, name: String, url: String): Int {
        val id = transaction {
            ArtistLinksTable.insertAndGetId { artistLink ->
                artistLink[ArtistLinksTable.artist] = artist
                artistLink[ArtistLinksTable.name] = name
                artistLink[ArtistLinksTable.url] = url
            }
        }
        return id.value
    }

    override suspend fun all(): List<ArtistLink> {
        return transaction {
            ArtistsTable.selectAll().map { row ->
                row.asArtistLink()
            }
        }
    }

    override suspend fun findById(id: Int): ArtistLink? {
        val row = transaction {
            addLogger(StdOutSqlLogger)
            ArtistsTable.select {
                ArtistsTable.id eq id
            }.firstOrNull()
        }
        return row?.asArtistLink()
    }

    override suspend fun findByArtistId(id: Int): List<ArtistLink> {
        val results = transaction {
            addLogger(StdOutSqlLogger)
            ArtistsTable.select {
                ArtistsTable.id eq id
            }
        }

        val list = ArrayList<ArtistLink>()
        transaction { for (row in results) list.add(row.asArtistLink()) }
        return list
    }

    override suspend fun delete(id: Int): Int {
        return transaction {
            ArtistsTable.deleteWhere {
                ArtistsTable.id eq id
            }
        }
    }

}

private fun ResultRow.asArtistLink() = ArtistLink(
    this[ArtistsTable.id].value,
    this[ArtistsTable.name],
    this[ArtistsTable.fileName],
)

data class ArtistLink(
    val id: Int,
    val name: String,
    val url: String,
)