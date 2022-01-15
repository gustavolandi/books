package br.com.landi.books.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.com.landi.books.R
import br.com.landi.books.model.Book
import br.com.landi.books.utils.Utils


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

}