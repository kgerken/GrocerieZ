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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.internal.GamesContract
import com.google.android.gms.wearable.*
import com.zuehlke.groceryshared.SHOPPING_LIST_DATA_PATH
import com.zuehlke.groceryshared.ShoppingItem
import org.jetbrains.anko.*
import java.util.*


/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingListActivityFragment : Fragment(), DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AnkoLogger {

    private var itemList: MutableList<ShoppingItem> = ArrayList<ShoppingItem>()
    private var listView: ListView? = null
    private var listAdapter: ShoppingListAdapter? = null

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

        setupListClickListener()
        setupListData()
        setupGoogleApiConnection()
    }

    private fun setupListClickListener() {
        listView?.onItemClick { parentView, clickedView, index, id ->
            if (index == itemList.size()) {
                itemList.add(ShoppingItem(index, "Item $index", false))
                listAdapter?.notifyDataSetChanged()
            } else {
                var checkedView = clickedView as CheckedTextView?
                checkedView?.setChecked(!checkedView.isChecked())
                checkedView?.backgroundColor = if (checkedView?.isChecked() ?: false) Color.LTGRAY else Color.WHITE
            }
            sendListToWearable()
        }
    }

    private fun setupListData() {
        var progressBar = ProgressBar(getActivity())
        progressBar.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
        progressBar.setIndeterminate(true)
        listView?.setEmptyView(progressBar)

        itemList = assembleList()
        listAdapter = ShoppingListAdapter(getActivity(), itemList!!)
        listView?.setAdapter(listAdapter)
    }

    private fun assembleList(): MutableList<ShoppingItem> {
        var list = ArrayList<ShoppingItem>()
        list.add(ShoppingItem(1, "Worscht", true))
        list.add(ShoppingItem(2, "Käs", false))
        list.add(ShoppingItem(3, "Brot", false))
        return list
    }

    // Data API

    private var googleApiClient: GoogleApiClient? = null

    private fun setupGoogleApiConnection() {
        googleApiClient = GoogleApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private fun sendListToWearable() {
        var putDataMapReq = PutDataMapRequest.create(SHOPPING_LIST_DATA_PATH)

        for (shoppingListEntry in itemList ?: emptyList<ShoppingItem>()) {
            var key: String = shoppingListEntry.id.toString()
            var datamap: DataMap = shoppingListEntry.toDataMap();
            putDataMapReq.getDataMap().putDataMap(key, datamap);
        }
        var putDataReq: PutDataRequest = putDataMapReq.asPutDataRequest();
        var pendingResult = Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
        println("--- Sending data item")
        pendingResult.setResultCallback({ it ->
            if (it.getStatus().isSuccess()) {
                println("--- Sent data successfully")
            }
        })
    }

    override fun onDataChanged(buffer: DataEventBuffer?) {
        buffer?.forEachIndexed { index, dataEvent ->
            dataEvent.getDataItem().getData() // TODO: Something
        }
    }

    override fun onConnected(bundle: Bundle?) {
        info("--- API connection established")
    }

    override fun onConnectionSuspended(p0: Int) {
        info("--- API connection suspended")
    }

    override fun onConnectionFailed(result: ConnectionResult?) {
        info("--- API connection failed: $result")
        if (result?.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            info("--- Wearable API is unavailable")
        }
    }

}
