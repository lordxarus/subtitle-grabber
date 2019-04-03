package com.lordxarus.subtitler.view

import com.lordxarus.subtitler.app.*
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.stage.FileChooser
import tornadofx.*

class MainView : View("Subtitler!") {

    val controller: MainController by inject()

    private var model = controller.model
    private var listView: ListView<SubtitleItem> by singleAssign()

    override val root = vbox {

        listView = listview(controller.subs) {

            isEditable = false
            selectionModel.selectionMode = SelectionMode.MULTIPLE

            bindSelected(model)

            cellFormat {
                text = item.name
                if (this.item.complete == Status.COMPLETE) {
                    if (!hasClass(MyStyles.completedCell)) {
                        removeClass(MyStyles.failedCell)
                        addClass(MyStyles.completedCell)
                    }
                } else if (this.item.complete == Status.FAILED){
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
                    val files = chooseFile(mode = FileChooserMode.Multi, title = "Select Target Directory / Files", filters =  arrayOf(FileChooser.ExtensionFilter("Video files", "*.mkv")))
                    if (files.isNotEmpty()) {
                        files.forEach {
                            controller.addSub(SubtitleItem(SubtitleGrabber.parseTitle(model.title.value, it), it))
                        }
                    }
                }

            }

            button("-") {
                enableWhen(listView.selectionModel?.selectedItemProperty()?.isNotNull!!)
                action {
                    controller.removeSub(listView.selectionModel?.selectedItemProperty()?.value!!)
                }
            }

            button("Download") {
                enableWhen { controller.subs.sizeProperty.greaterThan(0) }
                action {
                    model.commit()
                    listView.items.forEach {
                        controller.setComplete(it)
                    }
                    listView.refresh()
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





