package com.lordxarus.subtitler.view

import com.lordxarus.subtitler.app.MyStyles
import com.lordxarus.subtitler.app.Status
import com.lordxarus.subtitler.app.SubtitleGrabber
import com.lordxarus.subtitler.app.SubtitleItem
import com.lordxarus.subtitler.controller.MainController
import javafx.collections.ListChangeListener
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.stage.FileChooser
import tornadofx.*

class MainView : View("Subtitler!") {

    val controller: MainController by inject()

    private var model = controller.model
    private var listView: ListView<SubtitleItem> by singleAssign()
    private var lastSelected = SortedFilteredList<SubtitleItem>()

    override val root = vbox {

        listView = listview(controller.subs) {

            isEditable = false
            selectionModel.selectionMode = SelectionMode.MULTIPLE

            bindSelected(model)

            selectionModel.selectedItems.addListener { listener: ListChangeListener.Change<out SubtitleItem>->
                lastSelected.clear()
                listener.list.forEach {
                    if (it != null) {
                        println("SELECTED ${it.name}")
                        lastSelected.add(it)
                    }
                }
            }

            cellFormat {
                text = item.name
                if (this.item.complete == Status.COMPLETE) {
                    if (!hasClass(MyStyles.completedCell)) {
                        removeClass(MyStyles.failedCell)
                        addClass(MyStyles.completedCell)
                    }
                } else if (this.item.complete == Status.FAILED) {
                    if (!hasClass(MyStyles.failedCell)) {
                        addClass(MyStyles.failedCell)
                    }
                }
            }


        }


        hbox(20) {
            button("+") {
                enableWhen { model.title.isNotBlank() }
                action {
                    val files = chooseFile(mode = FileChooserMode.Multi, title = "Select Target Directory / Files", filters = arrayOf(FileChooser.ExtensionFilter("Video files", "*.mkv")))
                    if (files.isNotEmpty()) {
                        files.forEach {
                            controller.addSub(SubtitleItem(SubtitleGrabber.parseTitle(model.title.value, it), it))
                        }
                    }
                }

            }

            button("-") {
                enableWhen(lastSelected.sizeProperty.isNotEqualTo(0))
                action {
                    controller.removeAllSubs(lastSelected)
                }
            }

            button("Download") {
                enableWhen { controller.subs.sizeProperty.greaterThan(0) }
                action {
                    model.commit()
                    runAsync {
                        listView.items.forEach {
                            controller.download(it)
                            listView.refresh()
                        }
                    }

                }

            }
        }

        textfield(model.title) {
            required()
        }

        paddingAll = 10.0
        spacing = 10.0
    }
}





