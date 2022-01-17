package br.com.landi.todolist.dialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import br.com.landi.books.utils.Action
import br.com.landi.books.utils.Utils
import java.time.LocalDate
import java.util.*

class CustomDialog(private val context: Context) {

    var title = ""
    var message = ""
    var cancelable = false
    var textNegativeButton = "Não"
    var textPositiveButton = "Sim"

    fun showDialog(action: Action) {
        with(AlertDialog.Builder(context)) {
            setTitle(title)
            setMessage(message)
            setCancelable(cancelable)
            setNegativeButton(textNegativeButton) { dialog, id -> dialog.cancel() }
            setPositiveButton(textPositiveButton) { dialog, id -> action.execute() }
            create().show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePickerDialog(date: LocalDate, action: Action) {
        DatePickerDialog(
            context,
            { view, selectedYear, selectedMonth, selectedDay ->
                action.execute(selectedYear, selectedMonth, selectedDay)
            },
            date.year,
            date.monthValue-1,
            date.dayOfMonth
        ).show()
    }

    fun showDatePickerDialog(cal: Calendar, action: Action, minDate : String = "", maxDate : String = "") {
        val datePickerDialog = DatePickerDialog(
            context,
            { view, selectedYear, selectedMonth, selectedDay ->
                action.execute(selectedYear, selectedMonth, selectedDay)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        if (minDate.isNotEmpty()) {
            datePickerDialog.datePicker.minDate = Utils.getCalendarByDate(minDate).timeInMillis
        }
        if (maxDate.isNotEmpty()) {
            datePickerDialog.datePicker.maxDate = Utils.getCalendarByDate(maxDate).timeInMillis
        }
        datePickerDialog.show()
    }

}
