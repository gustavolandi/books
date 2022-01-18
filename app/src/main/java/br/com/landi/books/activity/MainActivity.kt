package br.com.landi.books.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.widget.*
import br.com.landi.books.utils.Action
import br.com.landi.books.utils.Utils.Companion.BOOK_AUTHOR_NAME
import br.com.landi.books.utils.Utils.Companion.BOOK_DATE_END
import br.com.landi.books.utils.Utils.Companion.BOOK_DATE_STARTED
import br.com.landi.books.utils.Utils.Companion.BOOK_READ_LIST
import br.com.landi.books.utils.Utils.Companion.BOOK_TITLE
import br.com.landi.books.utils.Utils.Companion.BOOK_GENRE
import br.com.landi.books.utils.Utils.Companion.BOOK_READ_UPDATE
import br.com.landi.books.utils.Utils.Companion.FILTER_AUTHOR
import br.com.landi.books.utils.Utils.Companion.FILTER_GENRE
import br.com.landi.books.utils.Utils.Companion.NO_FILTER
import br.com.landi.books.utils.Utils.Companion.getDateNow


class MainActivity : AppCompatActivity() {

    private lateinit var bookList : MutableList<Book>
    private lateinit var readList : MutableList<Read>
    private lateinit var listViewBook : ListView
    private lateinit var listViewRead : ListView
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>
    private var filterSelected = 0
    private var authorSelected = 0
    private var genreSelected = 0

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
                        authorName = result.data?.getStringExtra(BOOK_AUTHOR_NAME),
                        genreList = result.data?.getStringArrayListExtra(BOOK_GENRE)!!
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
        registerReceiver(uiUpdated, IntentFilter(BOOK_READ_UPDATE))
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
        val currentDate: String = getDateNow()
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
        val listFilter = listOf(NO_FILTER, FILTER_AUTHOR, FILTER_GENRE)
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.spinner_layout, listFilter
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(Dialog(this)) {
            setContentView(R.layout.dialog_filter)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            val spinner = findViewById<Spinner>(R.id.spinnerFilterBooks)
            spinner.adapter = dataAdapter
            spinner.setSelection(filterSelected)
            val btnOk = findViewById<RelativeLayout>(R.id.btnSubmitFilterBooks)
            btnOk.setOnClickListener {
                filterSelected = spinner.selectedItemPosition
                when(spinner.selectedItem.toString()) {
                    NO_FILTER -> noFilter()
                    FILTER_AUTHOR -> filterByAuthor()
                    FILTER_GENRE -> filterByGenre()
                }
                dismiss()
            }
            show()
        }
    }

    fun filterLayout(backAction: Action, nextAction: Action, txvAction: Action) {
        val linearLayoutFilter : LinearLayout = findViewById(R.id.linearLayoutFilter)
        linearLayoutFilter.visibility = View.VISIBLE
        val txv : TextView = findViewById(R.id.txvBookFilter)
        val imgBack : ImageView = findViewById(R.id.imgBackFilter)
        val imgNext : ImageView = findViewById(R.id.imgNextFilter)
        imgBack.setOnClickListener { backAction.execute() }
        imgNext.setOnClickListener { nextAction.execute() }
        txv.setOnClickListener { txvAction.execute() }
    }

    private fun noFilter() {
        val linearLayoutFilter : LinearLayout = findViewById(R.id.linearLayoutFilter)
        linearLayoutFilter.visibility = View.GONE
        listViewBook()
        listViewReadList()
    }

    private fun filterByAuthor() {
        val db = SQLiteHelper(this)
        var authorList = db.getAuthors()
        if (authorList.isNotEmpty()) {
            val authorsSortedBy: List<String> = authorList.sortedWith( compareBy(String.CASE_INSENSITIVE_ORDER) { it })
            filterAuthor(authorsSortedBy)
            filterLayout(
                nextAction = object : Action {
                    override fun execute() {
                        if (++authorSelected >= authorsSortedBy.size) {
                            authorSelected = 0
                        }
                        filterAuthor(authorsSortedBy)
                    }
                },
                backAction = object : Action {
                    override fun execute() {
                        if (--authorSelected < 0) {
                            authorSelected = authorsSortedBy.size - 1
                        }
                        filterAuthor(authorsSortedBy)
                    }
                },
                txvAction = object : Action {
                    override fun execute() {
                        dialogFilterAuthor(authorsSortedBy,authorSelected)
                    }
                }
            )
        }
    }

    private fun dialogFilterAuthor(list: List<String>, optionSelected : Int) {
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.spinner_layout, list
        )
        val context = this
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(Dialog(context)) {
            setContentView(R.layout.dialog_filter)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            val spinner = findViewById<Spinner>(R.id.spinnerFilterBooks)
            spinner.adapter = dataAdapter
            spinner.setSelection(optionSelected)
            val btnOk = findViewById<RelativeLayout>(R.id.btnSubmitFilterBooks)
            btnOk.setOnClickListener {
                if (filterSelected == 1) {
                    btnOkFilterAuthor(this,list)
                } else if (filterSelected == 2) {
                    btnOkFilterGenre(this,list)
                }
                dismiss()
            }
            show()
        }
    }

    private fun btnOkFilterAuthor(dialog: Dialog,authorList: List<String>) {
        val spinner = dialog.findViewById<Spinner>(R.id.spinnerFilterBooks)
        authorSelected = spinner.selectedItemPosition
        filterAuthor(authorList)
    }

    private fun btnOkFilterGenre(dialog: Dialog,genreList: List<String>) {
        val spinner = dialog.findViewById<Spinner>(R.id.spinnerFilterBooks)
        genreSelected = spinner.selectedItemPosition
        filterGenre(genreList)
    }

    fun filterAuthor(authorList: List<String>) {
        val authorsSortedBy: List<String> = authorList.sortedWith( compareBy(String.CASE_INSENSITIVE_ORDER) { it })
        val bookListAuthors = this.bookList.filter { it.authorName == authorsSortedBy[authorSelected] }.toMutableList()
        val readListAuthors = this.readList.filter { it.authorName == authorsSortedBy[authorSelected] }.toMutableList()
        val txv : TextView = findViewById(R.id.txvBookFilter)
        txv.text = authorList[authorSelected]
        listViewBook(bookListAuthors)
        listViewReadList(readListAuthors)
    }

    private fun filterByGenre()  {
        val db = SQLiteHelper(this)
        var genreList = db.getListGenre()
        if (genreList.isNotEmpty()) {
            val genresSortedBy = genreList.sortedWith( compareBy(String.CASE_INSENSITIVE_ORDER) { it })
            filterGenre(genresSortedBy)
            filterLayout(
                nextAction = object : Action {
                    override fun execute() {
                        if (++genreSelected >= genresSortedBy.size) {
                            genreSelected = 0
                        }
                        filterGenre(genresSortedBy)
                    }
                },
                backAction = object : Action {
                    override fun execute() {
                        if (--genreSelected < 0) {
                            genreSelected = genresSortedBy.size - 1
                        }
                        filterGenre(genresSortedBy)
                    }
                },
                txvAction = object : Action {
                    override fun execute() {
                        dialogFilterAuthor(genresSortedBy,genreSelected)
                    }
                }
            )
        }
    }

    fun filterGenre(genreList: List<String>) {
        val genreListSorted: List<String> = genreList.sortedWith( compareBy(String.CASE_INSENSITIVE_ORDER) { it })
        val txv : TextView = findViewById(R.id.txvBookFilter)
        txv.text = genreListSorted[genreSelected]
        listViewBook(this.bookList.filter { it.genreList.contains(genreListSorted[genreSelected]) }.toMutableList())
        listViewReadList(this.readList.filter { it.genreList.contains(genreListSorted[genreSelected]) }.toMutableList())
    }


}