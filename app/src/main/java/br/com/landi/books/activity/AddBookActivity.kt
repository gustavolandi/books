package br.com.landi.books.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
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
import android.widget.ArrayAdapter
import br.com.landi.books.repository.SQLiteHelper


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
        val edtAuthor = findViewById<AutoCompleteTextView>(R.id.edtAuthorName)
        val edtGenre = findViewById<EditText>(R.id.edtGenre)
        val cbReadList = findViewById<CheckBox>(R.id.cbAddToReadList)
        val edtDateStart = findViewById<EditText>(R.id.edtDateStartReading1)
        val edtDateFinish = findViewById<EditText>(R.id.edtDateFinishReading1)
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    edtAuthor.setAdapter(getArrayAdapter(s.toString()))
                }
            }
        }
        edtAuthor.addTextChangedListener(textWatcher)

        btn.setOnClickListener {
            if (edtTitle.text.toString().isEmpty()) {
                edtTitle.error  = "Preencha o campo"
            } else if (edtAuthor.text.toString().isEmpty()) {
                edtAuthor.error  = "Preencha o campo"
            } else {
                var genres = if (edtGenre.text.toString().trim().isEmpty()) {
                    ArrayList()
                } else {
                    ArrayList(edtGenre.text.toString().split(" ").filter { it.trim().isNotEmpty() })
                }
                with(Intent()) {
                    putExtra(BOOK_TITLE, edtTitle.text.toString())
                    putExtra(BOOK_AUTHOR_NAME, edtAuthor.text.toString())
                    putExtra(BOOK_READ_LIST, cbReadList.isChecked)
                    putExtra(BOOK_DATE_STARTED, edtDateStart.text.toString())
                    putExtra(BOOK_DATE_END, edtDateFinish.text.toString())
                    putStringArrayListExtra(BOOK_GENRE, genres)
                    setResult(Activity.RESULT_OK, this)
                }
                finish()
            }

        }
    }

    private fun getArrayAdapter(text: String) : ArrayAdapter<String> {
        return ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line, SQLiteHelper(this).getAuthors(text)
        )
    }
}