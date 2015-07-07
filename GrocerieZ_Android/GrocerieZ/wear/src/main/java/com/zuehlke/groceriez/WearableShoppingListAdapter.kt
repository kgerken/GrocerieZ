package com.zuehlke.groceriez

import android.content.Context
import android.support.wearable.view.WearableListView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zuehlke.groceryshared.ShoppingItem
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater

public class WearableShoppingListAdapter(private val context: Context, private val items: MutableList<ShoppingItem>) : WearableListView.Adapter() {
    // TODO: List dataset
    private val layoutInflater: LayoutInflater = context.layoutInflater

    public class ItemViewHolder(itemView: View) : WearableListView.ViewHolder(itemView) {
        val textView: TextView = itemView.find(R.id.name)
        val checkmarkView: ImageView = itemView.find(R.id.checkmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WearableListView.ViewHolder {
        return ItemViewHolder(layoutInflater.inflate(R.layout.list_item, null))
    }

    override fun onBindViewHolder(holder: WearableListView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder
        itemHolder.textView.setText(items[position].title)
        itemHolder.checkmarkView.setImageResource(if (items[position].checked) android.R.drawable.checkbox_off_background else android.R.drawable.checkbox_on_background)
        holder.itemView.setTag(items[position].id)
    }

    override fun getItemCount(): Int {
        return items.size()
    }
}