package br.com.landi.books.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RelativeLayout
import br.com.landi.books.R
import br.com.landi.books.utils.Utils.Companion.BOOK_AUTHOR_NAME
import br.com.landi.books.utils.Utils.Companion.BOOK_DATE_END
import br.com.landi.books.utils.Utils.Companion.BOOK_DATE_STARTED
import br.com.landi.books.utils.Utils.Companion.BOOK_GENRE
import br.com.landi.books.utils.Utils.Companion.BOOK_READ_LIST
import br.com.landi.books.utils.Utils.Companion.BOOK_TITLE

class AddBookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        init()
    }

    fun init() {
        val cb = findViewById<CheckBox>(R.id.cbAddToReadList)
        cb.setOnCheckedChangeListener { buttonView, isChecked ->
            val layoutReadList = findViewById<RelativeLayout>(R.id.rl_add_read_list)
            if (isChecked) {
                layoutReadList.visibility = View.VISIBLE
            } else {
                layoutReadList.visibility = View.GONE
            }
        }
        validateFieldsAndSave()
    }

    fun validateFieldsAndSave() {
        val btn = findViewById<RelativeLayout>(R.id.btnAddBook)
        val edtTitle = findViewById<EditText>(R.id.edtTitle)
        val edtAuthor = findViewById<EditText>(R.id.edtAuthorName)
        val edtGenre = findViewById<EditText>(R.id.edtGenre)
        val cbReadList = findViewById<CheckBox>(R.id.cbAddToReadList)
        val edtDateStart = findViewById<EditText>(R.id.edtDateStartReading1)
        val edtDateFinish = findViewById<EditText>(R.id.edtDateFinishReading1)
        btn.setOnClickListener {
            if (edtTitle.text.toString().isEmpty()) {
                edtTitle.error  = "Preencha o campo"
            } else if (edtAuthor.text.toString().isEmpty()) {
                edtAuthor.error  = "Preencha o campo"
            } else {
                with(Intent()) {
                    putExtra(BOOK_TITLE, edtTitle.text.toString())
                    putExtra(BOOK_AUTHOR_NAME, edtAuthor.text.toString())
                    putExtra(BOOK_GENRE, edtGenre.text.toString())
                    putExtra(BOOK_READ_LIST, cbReadList.isChecked)
                    putExtra(BOOK_DATE_STARTED, edtDateStart.text.toString())
                    putExtra(BOOK_DATE_END, edtDateFinish.text.toString())
                    setResult(Activity.RESULT_OK, this)
                }
                finish()
            }

        }
    }
}