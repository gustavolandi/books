package br.com.landi.books.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.com.landi.books.model.Book
import br.com.landi.books.model.Read
import br.com.landi.books.types.GetStatus
import br.com.landi.books.types.StatusRead
import java.lang.Exception

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, NAME_DB, null, version_db) {

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    private fun createTables(db: SQLiteDatabase) {
        val sqlAuthors = "CREATE TABLE IF NOT EXISTS $TBX_AUTHORS ($ID_PK, $AUTHOR_NAME VARCHAR(255))"
        val sqlBooks = "CREATE TABLE IF NOT EXISTS $TBX_BOOKS ($ID_PK, $TITLE VARCHAR(255), $ID_AUTHOR INTEGER(10), $DATE_REGISTER VARCHAR(10), $STATUS INTEGER(1)," +
                "CONSTRAINT fk_author_book FOREIGN KEY ($ID_AUTHOR) REFERENCES $TBX_AUTHORS($ID))"
        val sqlGenres = "CREATE TABLE IF NOT EXISTS $TBX_GENRES ($ID_PK, $GENRE)"

        val sqlGenresBooks = "CREATE TABLE IF NOT EXISTS $TBX_GENRES_BOOKS ($ID_PK, $ID_BOOK INTEGER(10), $ID_GENRE INTEGER(10), " +
                "CONSTRAINT fk_book FOREIGN KEY ($ID_BOOK) REFERENCES $TBX_BOOKS($ID)," +
                "CONSTRAINT fk_genre FOREIGN KEY ($ID_GENRE) REFERENCES $TBX_GENRES($ID))"

        val sqlBooksRead = "CREATE TABLE IF NOT EXISTS $TBX_BOOKS_READ ($ID_PK, $ID_BOOK INTEGER(10), $STATUS VARCHAR(10),$DATE_STARTED VARCHAR(10),$DATE_FINISHED VARCHAR(10),"  +
                "CONSTRAINT fk_book_read FOREIGN KEY ($ID_BOOK) REFERENCES $TBX_BOOKS($ID))"


        db.createTable(sqlAuthors)
        db.createTable(sqlBooks)
        db.createTable(sqlGenres)
        db.createTable(sqlGenresBooks)
        db.createTable(sqlBooksRead)

    }

    private fun SQLiteDatabase.createTable(query: String) {
        try {
            this.execSQL(query)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getBookList() : MutableList<Book> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $TBX_AUTHORS.$AUTHOR_NAME, $TBX_BOOKS.* FROM $TBX_BOOKS " +
                "INNER JOIN $TBX_AUTHORS ON $TBX_AUTHORS.$ID = $TBX_BOOKS.$ID_AUTHOR",
            null)
        val bookList: MutableList<Book> = mutableListOf()
        while (cursor.moveToNext()) {
            bookList.add(
                Book(id = cursor.getLong(cursor.getColumnIndex("$TBX_BOOKS.$ID")),
                    title = cursor.getString(cursor.getColumnIndex("$TBX_BOOKS.$TITLE")),
                    authorName = cursor.getString(cursor.getColumnIndex("$TBX_AUTHORS.$AUTHOR_NAME")),
                    registerDate = cursor.getString(cursor.getColumnIndex("$TBX_BOOKS.$DATE_REGISTER")),
                    genreList = getGenresBooksById(cursor.getLong(cursor.getColumnIndex("$TBX_BOOKS.$ID")))
                )
            )
        }
        return bookList
    }

    fun getReadList() : MutableList<Read> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $TBX_BOOKS.$TITLE, $TBX_AUTHORS.$AUTHOR_NAME, $TBX_BOOKS_READ.* " +
                "FROM $TBX_BOOKS_READ INNER JOIN $TBX_BOOKS ON $TBX_BOOKS.$ID = $TBX_BOOKS_READ.$ID_BOOK " +
                "INNER JOIN $TBX_AUTHORS ON $TBX_AUTHORS.$ID = $TBX_BOOKS.$ID_AUTHOR",
            null)
        val readList: MutableList<Read> = mutableListOf()
        while (cursor.moveToNext()) {
            readList.add(
                Read(id = cursor.getLong(cursor.getColumnIndex("$TBX_BOOKS_READ.$ID")),
                    idBook = cursor.getLong(cursor.getColumnIndex("$TBX_BOOKS.$ID")),
                    titleBook = cursor.getString(cursor.getColumnIndex("$TBX_BOOKS.$TITLE")),
                    authorName = cursor.getString(cursor.getColumnIndex("$TBX_AUTHORS.$AUTHOR_NAME")),
                    status = GetStatus.getStatus(cursor.getString(cursor.getColumnIndex("$TBX_BOOKS_READ.$STATUS"))),
                    startedDate = cursor.getString(cursor.getColumnIndex("$TBX_BOOKS_READ.$DATE_STARTED")),
                    finishedDate = cursor.getString(cursor.getColumnIndex("$TBX_BOOKS_READ.$DATE_FINISHED"))
                )
            )
        }
        return readList
    }

    fun saveBook(book: Book) : Long {
        var idAuthor: Long? = getAuthorByName(book.authorName!!)
        if (idAuthor == null) {
            idAuthor = saveAuthor(book.authorName!!)
        }
        val db = this.writableDatabase
        val ctv = ContentValues()
        ctv.put(TITLE, book.title)
        ctv.put(ID_AUTHOR, idAuthor)
        ctv.put(DATE_REGISTER, book.registerDate)
        val id = db.insert(TBX_BOOKS, ID, ctv)
        saveGenresBooks(book.genreList,id)
        return id
    }

    fun updateReadBook(read: Read) : MutableList<Read> {
        val db = this.writableDatabase
        val ctv = ContentValues()
        ctv.put(STATUS, read.status.status)
        ctv.put(DATE_STARTED, read.startedDate)
        ctv.put(DATE_FINISHED, read.finishedDate)
        db.update(TBX_BOOKS_READ,ctv,"$ID = ?",arrayOf(read.id.toString()))
        return getReadList()
    }

    fun saveReadBook(read: Read) {
        val db = this.writableDatabase
        val ctv = ContentValues()
        ctv.put(ID_BOOK, read.idBook)
        ctv.put(STATUS, read.status.status)
        ctv.put(DATE_STARTED, read.startedDate)
        ctv.put(DATE_FINISHED, read.finishedDate)
        db.insert(TBX_BOOKS_READ, ID, ctv)
    }

    fun saveAuthor(authorName: String) : Long {
        val db = this.writableDatabase
        val ctv = ContentValues()
        ctv.put(AUTHOR_NAME, authorName)
        return db.insert(TBX_AUTHORS, ID, ctv)
    }

    fun getAuthors(){

    }

    fun saveGenresBooks(genreList: MutableList<String>, idBook: Long) {
        genreList
            .map {
                getGenreByName(it)
            }
            .forEach {
                val db = this.writableDatabase
                val ctv = ContentValues()
                ctv.put(ID_GENRE, it)
                ctv.put(ID_BOOK, idBook)
                db.insert(TBX_GENRES_BOOKS, ID, ctv)
        }
    }

    fun getAuthorByName(authorName: String) : Long? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TBX_AUTHORS where $AUTHOR_NAME = '$authorName'", null)
        while (cursor.moveToNext()) {
           return cursor.getLong(cursor.getColumnIndex(ID))
        }
        return null
    }


    fun getAuthorById(id: Long) : String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TBX_AUTHORS where $ID = $id", null)
        while (cursor.moveToNext()) {
            return cursor.getString(cursor.getColumnIndex(AUTHOR_NAME))
        }
        return null
    }

    fun getGenres() : MutableList<String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TBX_GENRES", null)
        val genreList: MutableList<String> = mutableListOf()
        while (cursor.moveToNext()) {
            genreList.add(cursor.getString(cursor.getColumnIndex(GENRE)))
        }
        return genreList
    }

    fun getGenreByName(genre: String) : Long {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TBX_GENRES WHERE $GENRE = '$genre'", null)
        if (!cursor.moveToNext()) {
            return saveGenre(genre)
        } else {
            return cursor.getLong(cursor.getColumnIndex(ID))
        }
    }

    fun saveGenre(genre: String) : Long {
        val db = this.writableDatabase
        val ctv = ContentValues()
        ctv.put(GENRE, genre)
        return db.insert(TBX_GENRES, ID, ctv)
    }


    fun getGenreById(id: String) {

    }

    fun getGenresBooksById(idBook: Long) : MutableList<String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TBX_GENRES_BOOKS WHERE $ID_BOOK = $idBook", null)
        val genreList: MutableList<String> = mutableListOf()
        while (cursor.moveToNext()) {
            genreList.add(cursor.getString(cursor.getColumnIndex(GENRE)))
        }
        return genreList
    }

    companion object {
        private const val NAME_DB = "BookList_Landi.db"
        private const val version_db = 1

        private const val TBX_BOOKS = "tbx_books"
        private const val TBX_AUTHORS = "tbx_authors"
        private const val TBX_GENRES = "tbx_genres"
        private const val TBX_GENRES_BOOKS = "tbx_genres_books"
        private const val TBX_BOOKS_READ = "tbx_books_read"

        private const val ID_PK = "id integer primary key autoincrement"

        private const val ID = "id"
        private const val TITLE = "title"
        private const val ID_AUTHOR = "id_author"
        private const val DATE_REGISTER = "date_register"

        private const val AUTHOR_NAME = "author_name"
        private const val GENRE = "genre"

        private const val ID_BOOK = "id_book"
        private const val ID_GENRE = "id_genre"
        private const val DATE_STARTED = "date_started"
        private const val DATE_FINISHED = "date_finished"

        private const val STATUS = "status"
    }



}