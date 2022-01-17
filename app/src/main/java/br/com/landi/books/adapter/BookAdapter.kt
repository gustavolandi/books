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

        val c: Book = getItem(position)
        val txvBookTitle =
            v.findViewById<View>(R.id.txvBookTitle) as TextView
        val txvBookAuthorName =
            v.findViewById<View>(R.id.txvBookAuthor) as TextView
        val iconAddBookReadList =
            v.findViewById<View>(R.id.imgAddBookReadList) as ImageView
        txvBookTitle.text = c.title
        txvBookAuthorName.text = c.authorName
        iconAddBookReadList.setOnClickListener {
            dialogUpdateStatus(status = StatusRead.STATUS_NOT_INITIALIZED,position)
        }
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

    private fun showToast(position: Int) {
        Utils.toastMessage(
            context, getItem(position).toString()
        )
    }

    private fun dialogUpdateStatus(status : StatusRead, positionBook: Int) {
        with(Dialog(context)) {
            setContentView(R.layout.dialog_status)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            spinnerDialogStatus(this,positionBook,adapterSpinnerDialogUpdate(status))
            btnStatusUpdate(this,positionBook)
            show()
        }
    }

    private fun adapterSpinnerDialogUpdate(status : StatusRead): ArrayAdapter<String>{
        val listFilter = GetStatus.getStatus().toMutableList()
        if (status == StatusRead.STATUS_READING) {
            listFilter.remove( StatusRead.STATUS_NOT_INITIALIZED.status)
        }
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
            context,
            R.layout.spinner_layout, listFilter
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return dataAdapter
    }

    private fun spinnerDialogStatus(dialog: Dialog, positionBook: Int, dataAdapter: ArrayAdapter<String>){
        val spinner = dialog.findViewById<Spinner>(R.id.spinnerStatus)
        val edtStartDate = dialog.findViewById<EditText>(R.id.edtStatusDataStarted)
        val edtFinishDate = dialog.findViewById<EditText>(R.id.edtStatusDataFinished)
        spinner.adapter = dataAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View,
                                        position: Int, id: Long) {
                if (spinner.selectedItem.toString() == StatusRead.STATUS_READING.status) {
                    edtStartDate.visibility = View.VISIBLE
                    edtFinishDate.visibility = View.GONE
                    edtFinishDate.setText("")
                } else if (spinner.selectedItem.toString() == StatusRead.STATUS_FINISHED.status){
                    edtStartDate.visibility = View.VISIBLE
                    edtFinishDate.visibility = View.VISIBLE
                }
                edtStartDate.setOnClickListener { selectDate(edtStartDate, edtFinishDate, maxDate = edtFinishDate.text.toString()) }
                edtStartDate.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        selectDate(edtStartDate,edtFinishDate, maxDate = edtFinishDate.text.toString())
                    }
                }
                edtFinishDate.setOnClickListener { selectDate(edtFinishDate, null, minDate = edtStartDate.text.toString()) }
                edtFinishDate.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        selectDate(edtFinishDate,null, minDate = edtStartDate.text.toString())
                    }
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
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

    private fun validateDialogStatusFields(dialog: Dialog): Boolean {
        val spinner = dialog.findViewById<Spinner>(R.id.spinnerStatus)
        val edtStartDate = dialog.findViewById<EditText>(R.id.edtStatusDataStarted)
        val edtFinishDate = dialog.findViewById<EditText>(R.id.edtStatusDataFinished)
        var validateFields = true
        if (spinner.selectedItem.toString() == StatusRead.STATUS_READING.status) {
            if (edtStartDate.text.toString().isEmpty()){
                edtStartDate.error = "Preencha o campo"
                validateFields = false
            }
        } else if (spinner.selectedItem.toString() == StatusRead.STATUS_FINISHED.status) {
            if (edtStartDate.text.toString().isEmpty()){
                edtStartDate.error = "Preencha o campo"
                validateFields = false
            } else if (edtFinishDate.text.toString().isEmpty()){
                edtFinishDate.error = "Preencha o campo"
                validateFields = false
            }
            val startDate = Utils.getCalendarByDate(edtStartDate.text.toString())
            val finishDate = Utils.getCalendarByDate(edtFinishDate.text.toString())
            if (finishDate.timeInMillis < startDate.timeInMillis) {
                edtFinishDate.error = "Data final de leitura deve ser maior ou igual à data inicial de leitura"
                validateFields = false
            }
        }
        return validateFields
    }

    private fun updateStatus(status: StatusRead, startedDate : String, finishDate : String, position: Int) {
        val db  = SQLiteHelper(context)
        db.saveReadBook(Read(idBook = getItem(position).id, status = status, startedDate = startedDate, finishedDate = finishDate))
        Utils.toastMessage(context,"Livro salvo na lista de leitura")
    }

    private fun selectDate(edtSelected : EditText, edtNext : EditText?, minDate : String = "", maxDate : String = "") {
        val cal = Calendar.getInstance()
        CustomDialog(context).showDatePickerDialog(
            cal,
            object : Action {
                override fun execute(selectedYear : Int, selectedMonth : Int, selectedDay : Int) {
                    val day = validateFieldDataZeros(selectedDay)
                    val month = validateFieldDataZeros(selectedMonth + 1)
                    edtSelected.setText("$day/$month/$selectedYear")
                    edtNext?.requestFocus()
                }
            },
            minDate,
            maxDate)
    }

    private fun validateFieldDataZeros(field: Int) : String {
        var fieldReturn = field.toString()
        if (field < 10) {
            fieldReturn = "0$fieldReturn"
        }
        return fieldReturn
    }

}