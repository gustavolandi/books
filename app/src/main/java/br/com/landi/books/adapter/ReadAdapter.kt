package br.com.landi.books.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.com.landi.books.R
import br.com.landi.books.model.Read
import br.com.landi.books.repository.SQLiteHelper
import br.com.landi.books.types.GetStatus
import br.com.landi.books.types.StatusRead
import br.com.landi.books.utils.Utils
import br.com.landi.books.adapter.UtilsAdapter.Companion.spinnerDialogStatus
import br.com.landi.books.adapter.UtilsAdapter.Companion.validateDialogStatusFields
import java.util.*


class ReadAdapter(
    val context: Context,
    var list: MutableList<Read>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = convertView
            ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.model_read,
                null
            )

        v.setOnClickListener {

        }
        v.setOnLongClickListener {
            return@setOnLongClickListener(true)
        }

        val c: Read = getItem(position)

        val txvReadTitle =
            v.findViewById<View>(R.id.txvReadTitle) as TextView
        val txvReadAuthor =
            v.findViewById<View>(R.id.txvReadAuthor) as TextView
        val txvReadStatus =
            v.findViewById<View>(R.id.txvReadStatus) as TextView
        val txvReadStartDate =
            v.findViewById<View>(R.id.txvReadStartDate) as TextView
        val txvReadFinishDate =
            v.findViewById<View>(R.id.txvReadFinishDate) as TextView
        txvReadTitle.text = c.titleBook
        txvReadAuthor.text = c.authorName
        txvReadStatus.text = c.status.status
        val icon = v.findViewById<View>(R.id.imgReadingBook) as ImageView
        val imgResource = if (c.status == StatusRead.STATUS_NOT_INITIALIZED) {
            R.drawable.ic_book
        } else if (c.status == StatusRead.STATUS_READING) {
            R.drawable.ic_reading
        } else {
            R.drawable.ic_done
        }
        icon.setOnClickListener{
            if (c.status != StatusRead.STATUS_FINISHED) dialogUpdateStatus(c.status,position)
                else Utils.toastMessage(context,"Livro já finalizado!")
        }
        icon.setImageResource(imgResource)
        if (c.startedDate?.isNotEmpty() == true) {
            txvReadStartDate.visibility = View.VISIBLE
            txvReadStartDate.text = "Início: ${c.startedDate}"
        } else {
            txvReadStartDate.visibility = View.GONE
        }
        if (c.finishedDate?.isNotEmpty() == true) {
            txvReadFinishDate.visibility = View.VISIBLE
            txvReadFinishDate.text = "Fim: ${c.finishedDate}"
        } else {
            txvReadFinishDate.visibility = View.GONE
        }
        return v
    }

    override fun getItem(position: Int): Read {
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return list.get(position).id
    }

    override fun getCount(): Int {
        return list.size
    }

    fun refresh(list: MutableList<Read>) {
        this.list = list
        notifyDataSetChanged()
    }

    private fun dialogUpdateStatus(status : StatusRead, positionBook: Int) {
        with(Dialog(context)) {
            setContentView(R.layout.dialog_status)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            spinnerDialogStatus(context,this,getItem(positionBook).startedDate!!,status)
            btnStatusUpdate(this,positionBook)
            show()
        }
    }


    private fun btnStatusUpdate(dialog: Dialog,positionBook: Int){
        val btnOk = dialog.findViewById<RelativeLayout>(R.id.btnSubmitStatus)
        btnOk.setOnClickListener {
            val spinner = dialog.findViewById<Spinner>(R.id.spinnerStatus)
            val edtStartDate = dialog.findViewById<EditText>(R.id.edtStatusDataStarted)
            val edtFinishDate = dialog.findViewById<EditText>(R.id.edtStatusDataFinished)
            if (validateDialogStatusFields(dialog)) {
                updateStatus(GetStatus.getStatus(spinner.selectedItem.toString()),
                    edtStartDate.text.toString(),
                    edtFinishDate.text.toString(),
                    positionBook)
                dialog.dismiss()
            }
        }
    }

    private fun updateStatus(status: StatusRead, startedDate : String, finishDate : String, position: Int) {
        val db  = SQLiteHelper(context)
        list = db
            .updateReadBook(Read(id = getItem(position).id,startedDate = startedDate,finishedDate = finishDate, status = status))
            .sortedWith(Utils.comparatorReadStatus())
            .toMutableList()
        refresh(list)
    }

}