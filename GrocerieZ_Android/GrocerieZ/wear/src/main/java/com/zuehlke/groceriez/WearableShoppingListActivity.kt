package com.zuehlke.groceriez

import android.app.Activity
import android.os.Bundle
import android.support.wearable.view.WatchViewStub
import android.support.wearable.view.WearableListView
import android.view.View
import android.widget.TextView
import com.zuehlke.groceryshared.ShoppingItem
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import java.util.*

public class WearableShoppingListActivity : Activity(), WearableListView.ClickListener {

    private var listView: WearableListView? = null
    private var shoppingList: MutableList<ShoppingItem> = ArrayList<ShoppingItem>()
    private var shoppingListAdapter: WearableShoppingListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_list)
        listView = find(R.id.wearable_list)
        assembleList()
        shoppingListAdapter = WearableShoppingListAdapter(this, shoppingList)
        listView?.setAdapter(shoppingListAdapter)
        listView?.setClickListener(this)
    }

    private fun assembleList() {
        shoppingList = ArrayList<ShoppingItem>()
        shoppingList.add(ShoppingItem(1, "Worscht", true))
        shoppingList.add(ShoppingItem(2, "Käs", false))
        shoppingList.add(ShoppingItem(3, "Brot", false))

    }

    override fun onClick(viewHolder: WearableListView.ViewHolder?) {
        if (viewHolder != null) {
            var itemId = viewHolder.itemView.getTag();
            var item = shoppingList.filter{ item -> item.id == itemId }.firstOrNull()
            item?.checked = !(item?.checked ?: true)
            shoppingListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onTopEmptyRegionClick() {
    }
}
