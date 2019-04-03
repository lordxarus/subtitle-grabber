package com.lordxarus.subtitler.app

import tornadofx.*

class MainController : Controller() {

    val subs = SortedFilteredList<SubtitleItem>()

    val model: SubtitleItemModel by inject()

    init {

        // Dummy data
        //addSub(SubtitleItem(SubID("King of the Hill", "01", "01"), File("/run/media/jeremy/Torrents/tv/King.of.the.Hill.S01-S13.COMPLETE.WEB-DL.H.264-MiXED/King.of.the.Hill.S01.480p.WEB-DL.AAC2.0.H.264-SA89/King.of.the.Hill.S01E01.Pilot.480p.WEB-DL.AAC2.0.H.264-SA89.mkv")))

        SubtitleGrabber
    }

    fun addSub(sub: SubtitleItem){
        subs.add(sub)
    }

    fun removeSub(sub: SubtitleItem) {
        subs.remove(sub)
    }

    fun setComplete(sub: SubtitleItem) {
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