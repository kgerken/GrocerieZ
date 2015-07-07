package com.zuehlke.groceriez

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import android.widget.SimpleAdapter
import com.zuehlke.groceryshared.ShoppingItem
import org.jetbrains.anko.layoutInflater
import java.util.*

class ShoppingListAdapter(private val context: Context, public val items: MutableList<ShoppingItem>) : BaseAdapter() {

    val addItemString = "+ Add new +"

    override fun isEmpty(): Boolean {
        return false
    }

    override fun getItem(position: Int): Any? {
        return if (position < items.size()) items.get(position).title else addItemString
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view: CheckedTextView = (convertView as CheckedTextView?) ?: context.layoutInflater.inflate(android.R.layout.simple_selectable_list_item, null) as CheckedTextView
        view.setText(if (position < items.size()) items.get(position).title else addItemString)
        view.setChecked(if (position < items.size()) items.get(position).checked else false)
        view.setHeight(100)
        return view
    }

    override fun getCount(): Int {
        return items.size() + 1
    }

    override fun getItemId(position: Int): Long {
        return if (position < items.size()) items.get(position).id.toLong() else -1
    }

}
