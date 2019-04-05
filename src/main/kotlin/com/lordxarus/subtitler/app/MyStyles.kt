package com.lordxarus.subtitler.app

import javafx.scene.paint.Color
import tornadofx.*

class MyStyles : Stylesheet() {

    companion object {
        val completedCell by cssclass()
        val failedCell by cssclass()
        val button by cssclass()
    }


    init {

        completedCell {
            backgroundColor += Color.GREEN
            wrapText = true
        }

        failedCell {
            backgroundColor += Color.RED
            wrapText = true
        }

    }
}