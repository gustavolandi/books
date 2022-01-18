package br.com.landi.books.adapter

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import br.com.landi.books.R
import br.com.landi.books.types.ErrorMessage
import br.com.landi.books.types.GetStatus
import br.com.landi.books.types.StatusRead
import br.com.landi.books.utils.Action
import br.com.landi.books.utils.Utils
import br.com.landi.todolist.dialog.CustomDialog
import java.util.*

class UtilsAdapter(val context: Context) {

    companion object {

        private fun adapterSpinnerDialogUpdate(
            context: Context,
            status: StatusRead
        ): ArrayAdapter<String> {
            val listFilter = GetStatus.getStatus().toMutableList()
            if (status == StatusRead.STATUS_READING) {
                listFilter.remove(StatusRead.STATUS_NOT_INITIALIZED.status)
            }
            val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                context,
                R.layout.spinner_layout, listFilter
            )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            return dataAdapter
        }

        fun spinnerDialogStatus(
            context: Context,
            dialog: Dialog,
            startedDate: String = "",
            status: StatusRead
        ) {
            val dataAdapter: ArrayAdapter<String> = adapterSpinnerDialogUpdate(context, status)
            val spinner = dialog.findViewById<Spinner>(R.id.spinnerStatus)
            val edtStartDate = dialog.findViewById<EditText>(R.id.edtStatusDataStarted)
            val edtFinishDate = dialog.findViewById<EditText>(R.id.edtStatusDataFinished)
            spinner.adapter = dataAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?, selectedItemView: View,
                    position: Int, id: Long
                ) {
                    if (spinner.selectedItem.toString() == StatusRead.STATUS_READING.status) {
                        edtStartDate.visibility = View.VISIBLE
                        edtFinishDate.visibility = View.GONE
                        edtFinishDate.setText("")
                        validateDataStartedNotEmpty(startedDate, edtStartDate)
                    } else if (spinner.selectedItem.toString() == StatusRead.STATUS_FINISHED.status) {
                        validateDataStartedNotEmpty(startedDate, edtStartDate)
                        edtStartDate.visibility = View.VISIBLE
                        edtFinishDate.visibility = View.VISIBLE
                    }
                    edtStartDate.setOnClickListener {
                        selectDate(
                            context,
                            edtStartDate,
                            edtFinishDate,
                            maxDate = edtFinishDate.text.toString()
                        )
                    }
                    edtStartDate.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            selectDate(
                                context,
                                edtStartDate,
                                edtFinishDate,
                                maxDate = edtFinishDate.text.toString()
                            )
                        }
                    }
                    edtFinishDate.setOnClickListener {
                        selectDate(
                            context,
                            edtFinishDate,
                            null,
                            minDate = edtStartDate.text.toString()
                        )
                    }
                    edtFinishDate.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            selectDate(
                                context,
                                edtFinishDate,
                                null,
                                minDate = edtStartDate.text.toString()
                            )
                        }
                    }

                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {

                }
            }
        }

        private fun validateDataStartedNotEmpty(startedDate: String, edtStartDate: EditText) {
            if (startedDate.isNotEmpty()) {
                edtStartDate.setText(startedDate)
                edtStartDate.isEnabled = false
            }
        }


        fun validateDialogStatusFields(dialog: Dialog): Boolean {
            val spinner = dialog.findViewById<Spinner>(R.id.spinnerStatus)
            val edtStartDate = dialog.findViewById<EditText>(R.id.edtStatusDataStarted)
            val edtFinishDate = dialog.findViewById<EditText>(R.id.edtStatusDataFinished)
            var validateFields = true
            if (spinner.selectedItem.toString() == StatusRead.STATUS_READING.status) {
                if (edtStartDate.text.toString().isEmpty()) {
                    edtStartDate.error = ErrorMessage.FIELD_NECESSARY.errorMessage
                    validateFields = false
                }
            } else if (spinner.selectedItem.toString() == StatusRead.STATUS_FINISHED.status) {
                if (edtStartDate.text.toString().isEmpty()) {
                    edtStartDate.error = ErrorMessage.FIELD_NECESSARY.errorMessage
                    validateFields = false
                } else if (edtFinishDate.text.toString().isEmpty()) {
                    edtFinishDate.error = ErrorMessage.FIELD_NECESSARY.errorMessage
                    validateFields = false
                } else {
                    val startDate = Utils.getCalendarByDate(edtStartDate.text.toString())
                    val finishDate = Utils.getCalendarByDate(edtFinishDate.text.toString())
                    if (finishDate.timeInMillis < startDate.timeInMillis) {
                        edtFinishDate.error = ErrorMessage.FINISH_DATE_WRONG.errorMessage
                        validateFields = false
                    }
                }
            }
            return validateFields
        }

        fun selectDate(
            context: Context,
            edtSelected: EditText,
            edtNext: EditText?,
            minDate: String = "",
            maxDate: String = ""
        ) {
            val cal = Calendar.getInstance()
            CustomDialog(context).showDatePickerDialog(
                cal,
                object : Action {
                    override fun execute(selectedYear: Int, selectedMonth: Int, selectedDay: Int) {
                        val day = validateFieldDataZeros(selectedDay)
                        val month = validateFieldDataZeros(selectedMonth + 1)
                        edtSelected.setText("$day/$month/$selectedYear")
                        edtNext?.requestFocus()
                    }
                },
                minDate,
                maxDate
            )
        }

        fun validateFieldDataZeros(field: Int): String {
            var fieldReturn = field.toString()
            if (field < 10) {
                fieldReturn = "0$fieldReturn"
            }
            return fieldReturn
        }

    }
}