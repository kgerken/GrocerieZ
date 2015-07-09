package com.zuehlke.groceriez

import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zuehlke.groceryshared.ShoppingItem
import org.jetbrains.anko.find
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
        var view: View = convertView ?: context.layoutInflater.inflate(R.layout.list_item, null)
        var iconView: ImageView = view.find(R.id.checkmark)
        var titleView: TextView = view.find(R.id.name)
        var checked = if (position < items.size()) items.get(position).checked else false
        var checkmarkResource = if (checked) android.R.drawable.checkbox_on_background else android.R.drawable.checkbox_off_background
        iconView.setImageResource(checkmarkResource)
        iconView.setVisibility(if (position < items.size()) View.VISIBLE else View.INVISIBLE)
        titleView.setText(if (position < items.size()) items.get(position).title else addItemString)
        return view
    }

    override fun getCount(): Int {
        return items.size() + 1
    }

    override fun getItemId(position: Int): Long {
        return if (position < items.size()) items.get(position).id.toLong() else -1
    }

}
