package br.com.landi.books.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.com.landi.books.R
import br.com.landi.books.model.Read
import br.com.landi.books.types.StatusRead
import br.com.landi.books.utils.Utils


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
            icon.setOnClickListener {
                icon.setOnClickListener { Utils.toastMessage(context,"Livro na lista") }
            }
            R.drawable.ic_book
        } else if (c.status == StatusRead.STATUS_READING) {
            icon.setOnClickListener { Utils.toastMessage(context,"Lendo livro...") }
            R.drawable.ic_reading
        } else {
            icon.setOnClickListener { Utils.toastMessage(context,"Livro já finalizado!") }
            R.drawable.ic_done
        }
        icon.setImageResource(imgResource)
        if (c.startedDate.isNotEmpty()) {
            txvReadStartDate.visibility = View.VISIBLE
            txvReadStartDate.text = "Início: ${c.startedDate}"
        } else {
            txvReadStartDate.visibility = View.GONE
        }
        if (c.finishedDate.isNotEmpty()) {
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

}