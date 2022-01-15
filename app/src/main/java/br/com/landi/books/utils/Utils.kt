package br.com.landi.books.utils

import android.content.Context
import android.os.Build
import android.widget.Toast

class Utils {

    companion object {

        fun toastMessage(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
            Toast.makeText(context,message,duration).show()
        }


        fun validateBuildSdk() : Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

    }

}