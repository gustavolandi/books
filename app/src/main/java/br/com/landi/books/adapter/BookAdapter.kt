package br.com.landi.books.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.com.landi.books.R
import br.com.landi.books.model.Book
import br.com.landi.books.model.Read
import br.com.landi.books.repository.SQLiteHelper
import br.com.landi.books.types.GetStatus
import br.com.landi.books.types.StatusRead
import br.com.landi.books.utils.Action
import br.com.landi.books.utils.Utils
import br.com.landi.todolist.dialog.CustomDialog
import java.util.*
import android.content.Intent
import android.content.res.Resources
import android.view.View.GONE
import android.view.View.VISIBLE
import br.com.landi.books.adapter.UtilsAdapter.Companion.spinnerDialogStatus
import br.com.landi.books.adapter.UtilsAdapter.Companion.validateDialogStatusFields
import br.com.landi.books.utils.Utils.Companion.BOOK_READ_UPDATE


class BookAdapter(
    val context: Context,
    var list: MutableList<Book>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = convertView
            ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.model_book,
                null
            )
        v.setOnClickListener {

        }
        v.setOnLongClickListener {
            return@setOnLongClickListener(true)
        }

        val book: Book = getItem(position)
        val txvBookTitle =
            v.findViewById<View>(R.id.txvBookTitle) as TextView
        val txvBookAuthorName =
            v.findViewById<View>(R.id.txvBookAuthor) as TextView
        val iconAddBookReadList =
            v.findViewById<View>(R.id.imgAddBookReadList) as ImageView
        txvBookTitle.text = book.title
        txvBookAuthorName.text = book.authorName
        iconAddBookReadList.setOnClickListener {
            dialogUpdateStatus(status = StatusRead.STATUS_NOT_INITIALIZED,position)
        }
        addGenres(v,book)
        return v
    }

    override fun getItem(position: Int): Book {
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return list.get(position).id
    }

    override fun getCount(): Int {
        return list.size
    }


    fun refresh(todoList: MutableList<Book>) {
        this.list = todoList
        notifyDataSetChanged()
    }

    private fun dialogUpdateStatus(status : StatusRead, positionBook: Int) {
        with(Dialog(context)) {
            setContentView(R.layout.dialog_status)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            spinnerDialogStatus(context,this,status = status)
            btnStatusUpdate(this,positionBook)
            show()
        }
    }

    private fun btnStatusUpdate(dialog: Dialog, positionBook: Int){
        val btnSalvar = dialog.findViewById<RelativeLayout>(R.id.btnSubmitStatus)
        val txvSalvar = dialog.findViewById<TextView>(R.id.txvBtnDialogStatus)
        txvSalvar.text = "Salvar"
        btnSalvar.setOnClickListener {
            val spinner = dialog.findViewById<Spinner>(R.id.spinnerStatus)
            val edtStartDate = dialog.findViewById<EditText>(R.id.edtStatusDataStarted)
            val edtFinishDate = dialog.findViewById<EditText>(R.id.edtStatusDataFinished)
            val validateFields = validateDialogStatusFields(dialog)
            if (validateFields) {
                updateStatus(
                    GetStatus.getStatus(spinner.selectedItem.toString()),
                    edtStartDate.text.toString(),
                    edtFinishDate.text.toString(),
                    positionBook)
                val i = Intent(BOOK_READ_UPDATE)
                context.sendBroadcast(i)
                dialog.dismiss()
            }
        }
    }

    private fun updateStatus(status: StatusRead, startedDate : String, finishDate : String, position: Int) {
        val db  = SQLiteHelper(context)
        db.saveReadBook(Read(idBook = getItem(position).id, status = status, startedDate = startedDate, finishedDate = finishDate))
        Utils.toastMessage(context,"Livro salvo na lista de leitura")
    }

    private fun addGenres(v: View,book: Book) {
        val relativeLayout =
            v.findViewById<View>(R.id.rlLayoutGenres) as RelativeLayout
        if (book.genreList.size == 0) {
            relativeLayout.visibility = GONE
            return
        }
        relativeLayout.visibility = VISIBLE
        relativeLayout.removeAllViewsInLayout()
        var txSize = 0f
        var id = 0
        var firstId = 0
        var line = 0
        var belowId = 0
        for (i in book.genreList) {
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val tv = TextView(context)
            tv.text = i
            tv.id = View.generateViewId()
            tv.textSize = 15F
            tv.background = v.resources.getDrawable(
                R.drawable.bordered_rectangle_rounded_corners,
                null
            )
            val textSize = (tv.textSize * i.length)
            txSize += textSize
            if (txSize >= Resources.getSystem().displayMetrics.widthPixels) {
                belowId = firstId
                firstId = tv.id
                params.addRule(RelativeLayout.BELOW, belowId)
                params.setMargins(5, 5, 0, 0)
                txSize = 0f
                line++
            } else {
                params.addRule(RelativeLayout.RIGHT_OF, id)
                params.setMargins(5, 5, 0, 0)
                if (line != 0) {
                    params.addRule(RelativeLayout.BELOW, belowId)
                }
            }
            tv.layoutParams = params
            relativeLayout.addView(tv)
            if (id == 0) {
                firstId = tv.id
            }
            id = tv.id
        }
    }

}