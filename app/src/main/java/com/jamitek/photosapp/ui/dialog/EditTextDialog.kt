package com.jamitek.photosapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.jamitek.photosapp.R
import kotlinx.android.synthetic.main.dialog_edittext.view.*

class EditTextDialog(
    context: Context,
    title: String,

    /** Input type of the [EditText], use constants in [InputType]. */
    inputTypeFlags: Int = InputType.TYPE_CLASS_TEXT,

    /** Validates the input. Return true when valid. */
    validator: (String) -> Boolean = { true },

    negativeAction: () -> Unit = {},

    /** Handler for positive button. */
    positiveAction: (String) -> Unit
) {

    private val dialog: Dialog

    init {

        val contentView =
            LayoutInflater.from(context).inflate(R.layout.dialog_edittext, null, false).apply {
                input.inputType = inputTypeFlags
            }


        val input = { contentView.input.text.toString() }

        dialog = AlertDialog.Builder(context)
            .setView(contentView)
            .setTitle(title)
            .setPositiveButton(R.string.commonOk) { dialog, _ ->
                val isValid = validator(input())
                if (isValid) {
                    positiveAction(input())
                    dialog.dismiss()
                }
            }
            .setNegativeButton(R.string.commonCancel) { dialog, _ ->
                negativeAction()
                dialog.dismiss()
            }
            .create()

    }

    fun show() = dialog.show()

}