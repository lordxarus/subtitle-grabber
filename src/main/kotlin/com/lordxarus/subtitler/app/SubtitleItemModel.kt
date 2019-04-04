package com.lordxarus.subtitler.app

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import tornadofx.*
import java.io.File
import java.util.*

class SubtitleItem(subID: SubID, file: File) {
    val id = UUID.randomUUID()

    val titleProperty = SimpleStringProperty(subID.title)
    var title by titleProperty

    val fileProperty = SimpleObjectProperty(file)
    var file by fileProperty

    val episodeProperty = SimpleObjectProperty(subID.episode)
    var episode by episodeProperty

    val seasonProperty = SimpleObjectProperty(subID.season)
    var season by seasonProperty

    val nameProperty = SimpleStringProperty("$title S${season}E$episode")
    val name by nameProperty

    val completeProperty = SimpleObjectProperty(Status.UNKNOWN)
    var complete by completeProperty


    override fun toString() : String {
        return title
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as SubtitleItem

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}

class SubtitleItemModel: ItemViewModel<SubtitleItem>() {
    val file = bind(SubtitleItem::fileProperty)
    val title = bind(SubtitleItem::titleProperty)
    val episode = bind(SubtitleItem::episodeProperty)
    val season = bind(SubtitleItem::seasonProperty)
    val name = bind(SubtitleItem::nameProperty)
    val complete = bind(SubtitleItem::completeProperty)

}

data class SubID(val title: String, val season: String, val episode: String)

enum class Status(color: Color) {
    UNKNOWN(Color.WHITE), FAILED(Color.RED), COMPLETE(Color.GREEN)
}