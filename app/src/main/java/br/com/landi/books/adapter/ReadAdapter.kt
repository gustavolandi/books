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
import android.widget.AdapterView

import android.widget.AdapterView.OnItemSelectedListener





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
        return list.get(position).id.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    fun refresh(list: MutableList<Read>) {
        this.list = list
        notifyDataSetChanged()
    }

    private fun showToast(position: Int) {
        Utils.toastMessage(
            context, getItem(position).toString()
        )
    }

    private fun dialogUpdateStatus(status : StatusRead, position: Int) {
        val listFilter = GetStatus.getStatus().toMutableList()
        if (status == StatusRead.STATUS_READING) {
            listFilter.remove( "Não Iniciado")
        }
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
            context,
            R.layout.spinner_layout, listFilter
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(Dialog(context)) {
            setContentView(R.layout.dialog_status)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            val spinner = findViewById<RelativeLayout>(R.id.spinnerStatus) as Spinner
            val edtStartDate = findViewById<EditText>(R.id.edtStatusDataStarted)
            val edtFinishDate = findViewById<EditText>(R.id.edtStatusDataFinished)
            spinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View,
                    position: Int,
                    id: Long
                ) {
                    if (spinner.selectedItem.toString() == StatusRead.STATUS_READING.status) {
                        edtStartDate.visibility = View.VISIBLE
                        edtFinishDate.visibility = View.GONE
                        if (getItem(position).startedDate!!.isNotEmpty()) {
                            edtStartDate.setText(getItem(position).startedDate)
                            edtStartDate.isEnabled = false
                        }
                    } else if (spinner.selectedItem.toString() == StatusRead.STATUS_FINISHED.status){
                        val edtFinishDate = findViewById<EditText>(R.id.edtStatusDataFinished)
                        if (getItem(position).startedDate!!.isNotEmpty()) {
                            edtStartDate.setText(getItem(position).startedDate)
                            edtStartDate.isEnabled = false
                        }
                        edtStartDate.visibility = View.VISIBLE
                        edtFinishDate.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // your code here
                }
            }
            spinner.adapter = dataAdapter
            val btnOk = findViewById<RelativeLayout>(R.id.btnSubmitStatus)
            btnOk.setOnClickListener {
                var validateFields = true
                var startedDate = ""
                var finishDate = ""
                if (spinner.selectedItem.toString() == StatusRead.STATUS_READING.status) {
                    if (edtStartDate.text.toString().isEmpty()){
                        edtStartDate.error = "Preencha o campo"
                        validateFields = false
                    } else {
                        startedDate = edtStartDate.text.toString()
                    }
                } else if (spinner.selectedItem.toString() == StatusRead.STATUS_FINISHED.status) {
                    if (edtStartDate.text.toString().isEmpty()){
                        edtStartDate.error = "Preencha o campo"
                        validateFields = false
                    } else if (edtFinishDate.text.toString().isEmpty()){
                        edtFinishDate.error = "Preencha o campo"
                        validateFields = false
                    }
                    startedDate = edtStartDate.text.toString()
                    finishDate = edtFinishDate.text.toString()
                }
                if (validateFields) {
                    updateStatus(GetStatus.getStatus(spinner.selectedItem.toString()),startedDate,finishDate,position)
                    dismiss()

                }

            }
            show()
        }
    }

    private fun updateStatus(status: StatusRead, startedDate : String, finishDate : String, position: Int) {
        val db  = SQLiteHelper(context)
        list = db.updateReadBook(Read(id = getItem(position).id,startedDate = startedDate,finishedDate = finishDate, status = status))
        refresh(list)
    }

    companion object {

    }

}