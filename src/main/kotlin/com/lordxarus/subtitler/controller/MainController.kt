package com.lordxarus.subtitler.controller

import com.lordxarus.subtitler.app.*
import tornadofx.*
import java.io.File

class MainController : Controller() {

    val subs = SortedFilteredList<SubtitleItem>()

    val model: SubtitleItemModel by inject()

    init {

        // Dummy data
        addSub(SubtitleItem(SubID("King of the Hill", "01", "01"), File("/run/media/jeremy/Torrents/tv/King.of.the.Hill.S01-S13.COMPLETE.WEB-DL.H.264-MiXED/King.of.the.Hill.S01.480p.WEB-DL.AAC2.0.H.264-SA89/King.of.the.Hill.S01E01.Pilot.480p.WEB-DL.AAC2.0.H.264-SA89.mkv")))
        addSub(SubtitleItem(SubID("King of the Hill", "01", "02"), File("/run/media/jeremy/Torrents/tv/King.of.the.Hill.S01-S13.COMPLETE.WEB-DL.H.264-MiXED/King.of.the.Hill.S01.480p.WEB-DL.AAC2.0.H.264-SA89/King.of.the.Hill.S01E02.Square.Pig.480p.WEB-DL.AAC2.0.H.264-SA89.mkv")))
        addSub(SubtitleItem(SubID("King of the Hill", "01", "03"), File("/run/media/jeremy/Torrents/tv/King.of.the.Hill.S01-S13.COMPLETE.WEB-DL.H.264-MiXED/King.of.the.Hill.S01.480p.WEB-DL.AAC2.0.H.264-SA89/King.of.the.Hill.S01E03.The.Order.of.the.Straight.Arrow.480p.WEB-DL.AAC2.0.H.264-SA89.mkv")))

        runAsync {
            SubtitleGrabber
        }
    }

    fun addSub(sub: SubtitleItem){
        subs.add(sub)
    }

    fun removeAllSubs(sub: SubtitleItem) {
        subs.remove(sub)
    }

    fun removeAllSubs(sub: SortedFilteredList<SubtitleItem>) {
        subs.removeAll(sub)
    }

    fun download(sub: SubtitleItem) {
        model.itemProperty.set(sub)
        val result = SubtitleGrabber.getSubtitle(model.title.value, model.file.value)
        if (result) {
            model.complete.value = Status.COMPLETE
        } else {
            model.complete.value = Status.FAILED
        }
        model.commit()
    }



}