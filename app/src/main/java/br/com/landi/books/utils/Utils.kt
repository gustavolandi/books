package br.com.landi.books.utils

import android.content.Context
import android.os.Build
import android.widget.Toast

class Utils {

    companion object {

        const val BOOK_TITLE = "bookTitle"
        const val BOOK_AUTHOR_NAME = "bookAuthorName"
        const val BOOK_GENRE = "bookGenre"
        const val BOOK_READ_LIST = "bookReadList"
        const val BOOK_DATE_STARTED = "bookDateStarted"
        const val BOOK_DATE_END = "bookDateEnd"

        fun toastMessage(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
            Toast.makeText(context,message,duration).show()
        }


        fun validateBuildSdk() : Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

    }

}