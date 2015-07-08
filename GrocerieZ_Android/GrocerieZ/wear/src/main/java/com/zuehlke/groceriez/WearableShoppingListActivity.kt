package com.zuehlke.groceriez

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.wearable.view.WatchViewStub
import android.support.wearable.view.WearableListView
import android.view.View
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataApi
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import com.zuehlke.groceryshared.CommUtil
import com.zuehlke.groceryshared.ShoppingItem
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import java.util.*

public class WearableShoppingListActivity : Activity(),
        WearableListView.ClickListener,
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AnkoLogger {

    private var listView: WearableListView? = null
    private var itemList: MutableList<ShoppingItem> = ArrayList<ShoppingItem>()
    private var listAdapter: WearableShoppingListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_list)
        configureListView()
        setupGoogleApiConnection()
    }

    private fun configureListView() {
        listView = find(R.id.wearable_list)
        assembleList()
        listAdapter = WearableShoppingListAdapter(this, itemList)
        listView?.setAdapter(listAdapter)
        listView?.setClickListener(this)
    }

    private fun assembleList() {
        itemList = ArrayList<ShoppingItem>()
//        itemList.add(ShoppingItem(1, "Worscht", true))
//        itemList.add(ShoppingItem(2, "Käs", false))
//        itemList.add(ShoppingItem(3, "Brot", false))
    }

    override fun onClick(viewHolder: WearableListView.ViewHolder?) {
        if (viewHolder != null) {
            var itemId = viewHolder.itemView.getTag();
            var item = itemList.filter{ item -> item.id == itemId }.firstOrNull()
            item?.checked = !(item?.checked ?: true)
            listAdapter?.notifyDataSetChanged()
            CommUtil.sendItemListViaApiClient(itemList, googleApiClient)
        }
    }

    override fun onTopEmptyRegionClick() {
    }

    override fun onResume() {
        super<Activity>.onResume()
        googleApiClient?.connect()
    }

    override fun onPause() {
        super<Activity>.onPause()
        Wearable.DataApi.removeListener(googleApiClient, this)
        googleApiClient?.disconnect()
    }

    // Data API

    private var googleApiClient: GoogleApiClient? = null

    private fun setupGoogleApiConnection() {
        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    // DataListener

    override fun onDataChanged(buffer: DataEventBuffer?) {
        CommUtil.updateItemListFromDataEventBuffer(itemList, buffer, {
            runOnMainThread(Runnable {
                listAdapter?.notifyDataSetChanged()
            })
        })
    }

    private fun runOnMainThread(runnable: Runnable): Boolean {
        var mainHandler = Handler(getMainLooper())
        return mainHandler.post(runnable)
    }

    // ConnectionCallbacks

    override fun onConnected(bundle: Bundle?) {
        info("--- API connection established")
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    override fun onConnectionSuspended(p0: Int) {
        info("--- API connection suspended")
    }

    // ConnectionFailedListener

    override fun onConnectionFailed(result: ConnectionResult?) {
        info("--- API connection failed: $result")
        if (result?.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            info("--- Wearable API is unavailable")
        }
    }
}
