package br.com.landi.books.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.landi.books.R
import br.com.landi.books.adapter.BookAdapter
import br.com.landi.books.adapter.ReadAdapter
import br.com.landi.books.model.Book
import br.com.landi.books.model.Read
import br.com.landi.books.repository.SQLiteHelper
import br.com.landi.books.types.StatusRead
import br.com.landi.books.utils.Utils.Companion.BOOK_AUTHOR_NAME
import br.com.landi.books.utils.Utils.Companion.BOOK_DATE_END
import br.com.landi.books.utils.Utils.Companion.BOOK_DATE_STARTED
import br.com.landi.books.utils.Utils.Companion.BOOK_READ_LIST
import br.com.landi.books.utils.Utils.Companion.BOOK_TITLE
import java.text.SimpleDateFormat
import java.util.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter


class MainActivity : AppCompatActivity() {

    private lateinit var bookList : MutableList<Book>
    private lateinit var readList : MutableList<Read>
    private lateinit var listViewBook : ListView
    private lateinit var listViewRead : ListView
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_adiciona, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        } else if (id == R.id.addItem) {
            activityAddBook()
            return true
        } else if (id == R.id.filterItem) {
            dialogFilter()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun init(){
        val btnBooks = findViewById<RelativeLayout>(R.id.btnOptionBooks)
        val btnReadList = findViewById<RelativeLayout>(R.id.btnOptionReadList)
        val txvBooks = findViewById<TextView>(R.id.txvOptionBooks)
        val txvReadList = findViewById<TextView>(R.id.txvOptionReadList)
        listViewBook = findViewById(R.id.ltwBooks)
        listViewRead = findViewById(R.id.ltwRead)
        btnBooks.setOnClickListener {
            showList(txvBooks, txvReadList, listViewBook, listViewRead)
        }
        btnReadList.setOnClickListener {
            showList(txvReadList, txvBooks, listViewRead, listViewBook)
        }
        newBook()
        updateReadList()
        getBookList()
        getReadList()
    }

    fun newBook(){
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val book = buildBook(
                        id = 1,
                        title = result.data?.getStringExtra(BOOK_TITLE),
                        authorName = result.data?.getStringExtra(BOOK_AUTHOR_NAME)
                    )
                    val idBook = saveBook(book)
                    if (result.data?.getBooleanExtra(BOOK_READ_LIST, false) == true) {
                        val statusRead =
                            if (result.data?.getStringExtra(BOOK_DATE_STARTED)!!.isNotEmpty()) {
                                if (result.data?.getStringExtra(BOOK_DATE_END)!!.isNotEmpty()) {
                                    StatusRead.STATUS_FINISHED
                                } else {
                                    StatusRead.STATUS_READING
                                }
                            } else {
                                StatusRead.STATUS_NOT_INITIALIZED
                            }
                        val read =  buildRead(
                            id = 1,
                            idBook = idBook,
                            title = result.data?.getStringExtra(BOOK_TITLE),
                            authorName = result.data?.getStringExtra(BOOK_AUTHOR_NAME),
                            startDate = result.data?.getStringExtra(BOOK_DATE_STARTED),
                            finishDate = result.data?.getStringExtra(BOOK_DATE_END),
                            status = statusRead
                        )
                        saveReadList(read)

                    }
                }
            }
    }

    fun updateReadList() {
        val uiUpdated: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                getReadList()
            }
        }
        registerReceiver(uiUpdated, IntentFilter("BOOK_READ"))
    }

    fun getBookList(){
        val db = SQLiteHelper(this)
        this.bookList = db.getBookList()
        listViewBook()
    }

    fun getReadList(){
        val db = SQLiteHelper(this)
        this.readList = db.getReadList()
        listViewReadList()
    }

    fun saveBook(book: Book): Long {
        val db = SQLiteHelper(this)
        val id = db.saveBook(book)
        getBookList()
        return id
    }

    fun saveReadList(read: Read){
        val db = SQLiteHelper(this)
        db.saveReadBook(read)
        getReadList()
    }

    fun showList(
        txvSelected: TextView,
        txvNotSelected: TextView,
        listViewSelected: ListView,
        listViewNotSelected: ListView
    ) {
        txvSelected.setTextColor(resources.getColor(R.color.white, null))
        txvNotSelected.setTextColor(resources.getColor(R.color.colorButtonNotSelected, null))
        listViewNotSelected.visibility = View.GONE
        listViewSelected.visibility = View.VISIBLE
    }

    fun initExamples(){
        bookList = mutableListOf()
        readList = mutableListOf()
        bookList.add(
            buildBook(
                id = 1,
                title = "Harry Potter e a Pedra Filosofal",
                authorName = "J. K. Rowling"
            )
        )
        bookList.add(
            buildBook(
                id = 2,
                title = "Harry Potter e a Câmara Secreta",
                authorName = "J. K. Rowling"
            )
        )
        readList.add(
            buildRead(
                id = 1,
                idBook = 1,
                title = "Harry Potter e a Pedra Filosofal",
                authorName = "J. K. Rowling",
                startDate = "12/01/2022",
                finishDate = "13/01/2022",
                status = StatusRead.STATUS_FINISHED
            )
        )
        readList.add(
            buildRead(
                id = 1,
                idBook = 2,
                title = "Harry Potter e a Câmara Secreta",
                authorName = "J. K. Rowling",
                startDate = "14/01/2022",
                status = StatusRead.STATUS_READING
            )
        )
    }

    fun buildRead(
        id: Long,
        idBook: Long,
        title: String?,
        authorName: String?,
        startDate: String? = "",
        finishDate: String? = "",
        status: StatusRead = StatusRead.STATUS_NOT_INITIALIZED
    ) : Read {
        val read = Read(id, title, authorName, idBook, startDate, finishDate, status)
        return read
    }

    fun buildBook(
        id: Long,
        title: String?,
        authorName: String?,
        genreList: MutableList<String> = mutableListOf(),
        readList: MutableList<Read> = mutableListOf()
    ) : Book {
        val currentDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val book = Book(id, title, authorName, currentDate, genreList, readList)
        return book
    }

    fun listViewBook(bookList: MutableList<Book> = this.bookList) {
        if (listViewBook.adapter != null) {
            (listViewBook.adapter as BookAdapter).refresh(bookList)
        } else {
            listViewBook.adapter = BookAdapter(this, bookList)
        }
    }

    fun listViewReadList(readList: MutableList<Read> = this.readList) {
        if (listViewRead.adapter != null) {
            (listViewRead.adapter as ReadAdapter).refresh(readList)
        } else {
            listViewRead.adapter = ReadAdapter(this, readList)
        }
    }

    fun activityAddBook(){
        intentLauncher.launch(Intent(this, AddBookActivity::class.java))
    }

    fun dialogFilter(){

    }
}