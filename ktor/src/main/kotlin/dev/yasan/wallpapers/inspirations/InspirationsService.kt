package dev.yasan.wallpapers.inspirations

import dev.yasan.wallpapers.InspirationsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface InspirationsService {

    suspend fun create(name: String, picture: Boolean, link: String?): Int

    suspend fun all(): List<Inspiration>

    suspend fun findById(id: Int): Inspiration?

    suspend fun delete(id: Int): Int

}

class InspirationsServiceDB : InspirationsService {

    override suspend fun create(name: String, picture: Boolean, link: String?): Int {
        val id = transaction {
            InspirationsTable.insertAndGetId { inspiration ->
                inspiration[InspirationsTable.name] = name
                inspiration[InspirationsTable.picture] = picture
                inspiration[InspirationsTable.url] = link
            }
        }
        return id.value
    }

    override suspend fun all(): List<Inspiration> {
        return transaction {
            InspirationsTable.selectAll().map { row ->
                row.asInspiration()
            }
        }
    }

    override suspend fun findById(id: Int): Inspiration? {
        val row = transaction {
            addLogger(StdOutSqlLogger)
            InspirationsTable.select {
                InspirationsTable.id eq id
            }.firstOrNull()
        }
        return row?.asInspiration()
    }

    override suspend fun delete(id: Int): Int {
        return transaction {
             InspirationsTable.deleteWhere {
                InspirationsTable.id eq id
            }
        }
    }

}

private fun ResultRow.asInspiration() = Inspiration(
    this[InspirationsTable.id].value,
    this[InspirationsTable.name],
    this[InspirationsTable.picture],
    this[InspirationsTable.url],
)

data class Inspiration(val id: Int, val name: String, val picture: Boolean, val link: String?)