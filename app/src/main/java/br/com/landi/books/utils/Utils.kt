package br.com.landi.books.utils

import android.content.Context
import android.os.Build
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        const val BOOK_TITLE = "bookTitle"
        const val BOOK_AUTHOR_NAME = "bookAuthorName"
        const val BOOK_GENRE = "bookGenre"
        const val BOOK_READ_LIST = "bookReadList"
        const val BOOK_DATE_STARTED = "bookDateStarted"
        const val BOOK_DATE_END = "bookDateEnd"
        const val BOOK_READ_UPDATE = "BOOK_READ_UPDATE"

        fun toastMessage(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
            Toast.makeText(context,message,duration).show()
        }

        fun validateBuildSdk() : Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

        fun getCalendarByDate(date : String) : Calendar {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            calendar.time = sdf.parse(date)
            return calendar
        }

    }

}