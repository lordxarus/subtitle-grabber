package com.lordxarus.subtitler.app

import com.lordxarus.subtitler.view.MainView
import tornadofx.*

class Subtitler: App(MainView::class, MyStyles::class) {

    init {
      //reloadStylesheetsOnFocus()
      //reloadViewsOnFocus()
    }
}