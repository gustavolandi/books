package br.com.landi.books.utils

import android.content.Context
import android.os.Build
import android.widget.Toast
import br.com.landi.books.model.Book
import br.com.landi.books.model.Read
import br.com.landi.books.types.StatusRead
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
        const val DATE_FORMAT = "dd/MM/yyyy"
        const val NO_FILTER = "Sem filtro"
        const val FILTER_AUTHOR = "Autor(a)"
        const val FILTER_GENRE = "Gênero"

        fun toastMessage(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
            Toast.makeText(context,message,duration).show()
        }

        fun validateBuildSdk() : Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

        fun getCalendarByDate(date : String) : Calendar {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            calendar.time = sdf.parse(date)
            return calendar
        }

        fun getDateNow() : String {
           return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        }

        fun comparatorReadStatus() : Comparator<Read> {
            return Comparator<Read>{ a, b ->
                when {
                    (a.status == StatusRead.STATUS_FINISHED && b.status == StatusRead.STATUS_NOT_INITIALIZED) -> 1
                    (a.status == StatusRead.STATUS_FINISHED && b.status == StatusRead.STATUS_READING) -> 1
                    (a.status == StatusRead.STATUS_NOT_INITIALIZED && b.status == StatusRead.STATUS_READING) -> 1
                    else -> -1
                }
            }
        }

        fun comparatorDate() : Comparator<Book> {
            return Comparator<Book>{ a, b ->
               return@Comparator(a.registerDate!!.compareTo(b.registerDate!!))
            }
        }

    }

}