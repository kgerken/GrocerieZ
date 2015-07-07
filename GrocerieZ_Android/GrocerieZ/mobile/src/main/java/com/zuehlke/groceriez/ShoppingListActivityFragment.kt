package com.zuehlke.groceriez

import android.app.Fragment
import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleCursorAdapter
import com.google.android.gms.games.internal.GamesContract
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import org.jetbrains.anko.onItemClick
import org.jetbrains.anko.onItemSelectedListener
import java.util.*


/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingListActivityFragment : Fragment() {

    private var listView: ListView? = null
    private var mAdapter: ShoppingListAdapter? = null

    // These are the Contacts rows that we will retrieve
    private val PROJECTION = arrayOf(BaseColumns._ID, "display_name");

    // This is the select criteria
    private val SELECTION = "((display_name NOTNULL) AND (display_name != '' ))";

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_shopping_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super<Fragment>.onActivityCreated(savedInstanceState)
        listView = find(R.id.shoppingList)

        listView?.onItemClick { parentView, clickedView, index, id ->
            println("--- Selected item $index (id $id)")
            var checkedView = clickedView as CheckedTextView?
            checkedView?.setChecked(!checkedView.isChecked())
            checkedView?.backgroundColor = if (checkedView?.isChecked() ?: false) Color.LTGRAY else Color.WHITE
        }

        var progressBar = ProgressBar(getActivity())
        progressBar.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
        progressBar.setIndeterminate(true)
        listView?.setEmptyView(progressBar)

        val itemList = assembleList()
        mAdapter = ShoppingListAdapter(getActivity(), itemList)
        listView?.setAdapter(mAdapter)
    }

    private fun assembleList(): MutableList<ShoppingItem> {
        var list = ArrayList<ShoppingItem>()
        list.add(ShoppingItem(1, "Worscht", true))
        list.add(ShoppingItem(2, "Käs", false))
        list.add(ShoppingItem(3, "Brot", false))
        return list
    }
}
