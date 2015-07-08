package com.zuehlke.groceriez

import android.app.AlertDialog
import android.app.Fragment
import android.app.LoaderManager
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Loader
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.internal.GamesContract
import com.google.android.gms.wearable.*
import com.zuehlke.groceryshared.SHOPPING_LIST_DATA_PATH
import com.zuehlke.groceryshared.ShoppingItem
import com.zuehlke.groceryshared.CommUtil
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
    private var inputDialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_shopping_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super<Fragment>.onActivityCreated(savedInstanceState)
        listView = find(R.id.shoppingList)
        createInputDialog()
        setupListClickListener()
        setupListData()
        setupGoogleApiConnection()
    }

    private fun setupListClickListener() {
        listView?.onItemClick { parentView, clickedView, index, id ->
            if (index == itemList.size()) {
                addNewListItem()
            } else {
                println("--- Setting item $index checked: ${!itemList[index].checked}")
                itemList[index].checked = !itemList[index].checked
                listAdapter?.notifyDataSetChanged()
            }
            sendListToWearable()
        }
    }

    private fun addNewListItem() {
        inputDialog?.show()
    }

    private fun createInputDialog() {
        var builder = AlertDialog.Builder(getActivity())
        builder.setTitle("Add new item")
        var inputSection = getActivity().layoutInflater.inflate(R.layout.dialog_input, null)
        builder.setView(inputSection)
        val titleInput: EditText? = inputSection.find(R.id.itemEntry)
        builder.setPositiveButton(android.R.string.ok, { dialog, id ->
            var maxIndex = itemList.fold(0, { max: Int, item: ShoppingItem -> Math.max(max, item.id) })
            itemList.add(ShoppingItem(maxIndex + 1, titleInput?.text?.toString() ?: "item $maxIndex", false))
            listAdapter?.notifyDataSetChanged()
            titleInput?.text = ""
        })
        builder.setNegativeButton(android.R.string.cancel, { dialog, id ->
            titleInput?.text = ""
        })
        inputDialog = builder.create()
    }

    private fun setupListData() {
        var progressBar = ProgressBar(getActivity())
        progressBar.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
        progressBar.setIndeterminate(true)
        listView?.setEmptyView(progressBar)

        itemList = assembleList()
        listAdapter = ShoppingListAdapter(getActivity(), itemList)
        listView?.setAdapter(listAdapter)
    }

    private fun assembleList(): MutableList<ShoppingItem> {
        return ArrayList<ShoppingItem>()
    }

    override fun onResume() {
        super<Fragment>.onResume()
        googleApiClient?.connect()
    }

    override fun onPause() {
        super<Fragment>.onPause()
        Wearable.DataApi.removeListener(googleApiClient, this)
        googleApiClient?.disconnect()
    }

    // Data API

    private var googleApiClient: GoogleApiClient? = null

    private fun setupGoogleApiConnection() {
        googleApiClient = GoogleApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    private fun sendListToWearable() {
        async {
            CommUtil.sendItemListViaApiClient(itemList, googleApiClient)
        }
    }

    // DataListener

    override fun onDataChanged(buffer: DataEventBuffer?) {
        async {
            CommUtil.updateItemListFromDataEventBuffer(itemList, buffer, {
                uiThread {
                    listAdapter?.notifyDataSetChanged()
                }
            })
        }
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
