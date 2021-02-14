package dev.yasan.wallpapers

import org.jetbrains.exposed.dao.id.IntIdTable

object InspirationsTable : IntIdTable() {
    val name = varchar("name", 20)
    val picture = bool("picture").default(false)
    val url = varchar("url", 150).nullable()
}

object ArtistsTable : IntIdTable() {
    val name = varchar("name", 20)
    val fileName = varchar("file_name", 150)
    val hasDarkPicture = bool("has_dark_picture").default(false)
    val commissions = bool("commissions").default(false)
}

object ArtistLinksTable : IntIdTable() {
    val artist = integer("artist").references(ArtistsTable.id)
    val name = varchar("name", 20).uniqueIndex()
    val url = varchar("url", 150).nullable()
}

object CollectionsTable : IntIdTable() {
    val name = varchar("name", 20)
    val picture = varchar("picture", 150)
    val hasDarkPicture = bool("has_dark_picture").default(false)
    val description = varchar("description", 150)
    val artist = integer("artist").references(ArtistsTable.id)
    val hiddenName = bool("hidden_name").default(false)
}

object WallpapersTable : IntIdTable() {
    val name = varchar("name", 20)
    val artist = integer("artist").references(ArtistsTable.id)
    val collection = integer("collection").references(CollectionsTable.id)
    val inspiration = integer("inspiration").references(InspirationsTable.id).nullable()
    val variants = integer("variants").nullable()
    val width = integer("width")
    val height = integer("height")
    val fileName = varchar("file_name", 150)
    val description = varchar("description", 150).nullable()
    val makingOfUrl = varchar("making_url", 150).nullable()
    val shopUrl = varchar("shop_url", 150).nullable()
}