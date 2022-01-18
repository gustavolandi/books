package br.com.landi.books.view

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.*
import br.com.landi.books.repository.SQLiteHelper

@SuppressLint("AppCompatCustomView")
class GenreEditText(context: Context?, attrs: AttributeSet?) : AutoCompleteTextView(context, attrs) {

    var textWatcher: TextWatcher? = null
    var lastString: String? = null
    var separator = " "

    private fun init() {
        movementMethod = LinkMovementMethod.getInstance()
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    setAdapter(getArrayAdapter(s.toString()))
                }
            }
            override fun afterTextChanged(s: Editable) {
                val thisString = s.toString()
                if (thisString.isNotEmpty() && thisString != lastString) {
                    format()
                }
            }
        }
        addTextChangedListener(textWatcher)
    }

    private fun format() {
        val sb = SpannableStringBuilder()
        val fullString = text.toString()
        val strings = fullString.split(separator.toRegex()).toTypedArray()
        for (i in strings.indices) {
            val string = strings[i]
            sb.append(string)
            if (fullString[fullString.length - 1] != separator[0] && i == strings.size - 1) {
                break
            }
            val bd = convertViewToDrawable(createTokenView(string)) as BitmapDrawable
            bd.setBounds(0, 0, bd.intrinsicWidth, bd.intrinsicHeight)
            val startIdx = sb.length - string.length
            val endIdx = sb.length
            sb.setSpan(ImageSpan(bd), startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val myClickableSpan = MyClickableSpan(startIdx, endIdx)
            sb.setSpan(
                myClickableSpan,
                Math.max(endIdx - 2, startIdx),
                endIdx,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (i < strings.size - 1) {
                sb.append(separator)
            } else if (fullString[fullString.length - 1] == separator[0]) {
                sb.append(separator)
            }
        }
        lastString = sb.toString()
        text = sb
        setSelection(sb.length)
    }

    fun createTokenView(text: String?): View {
        val l = LinearLayout(context)
        l.orientation = LinearLayout.HORIZONTAL
        l.setBackgroundResource(br.com.landi.books.R.drawable.bordered_rectangle_rounded_corners)
        val tv = TextView(context)
        l.addView(tv)
        tv.text = text
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
        val im = ImageView(context)
        l.addView(im)
        im.setImageResource(R.drawable.ic_delete)
        im.scaleType = ImageView.ScaleType.FIT_CENTER
        return l
    }

    fun convertViewToDrawable(view: View): Any {
        val spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        view.measure(spec, spec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val b =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.translate(-view.scrollX.toFloat(), -view.scrollY.toFloat())
        view.draw(c)
        view.isDrawingCacheEnabled = true
        val cacheBmp = view.drawingCache
        val viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true)
        view.destroyDrawingCache()
        return BitmapDrawable(context.resources, viewBmp)
    }

    private inner class MyClickableSpan(var startIdx: Int, var endIdx: Int) :
        ClickableSpan() {
        override fun onClick(widget: View) {
            val s = text.toString()
            val s1 = s.substring(0, startIdx)
            val s2 = s.substring(Math.min(endIdx + 1, s.length - 1), s.length)
            this@GenreEditText.setText(s1 + s2)
        }
    }

    init {
        init()
    }

    private fun getArrayAdapter(text: String) : ArrayAdapter<String> {
        return ArrayAdapter<String>(
            context,
            R.layout.simple_dropdown_item_1line, SQLiteHelper(context).getGenres(text)
        )
    }
}