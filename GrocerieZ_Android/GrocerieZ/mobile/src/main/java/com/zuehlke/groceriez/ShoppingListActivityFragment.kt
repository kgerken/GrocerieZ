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
import com.zuehlke.groceriez.data.ListStorage
import com.zuehlke.groceriez.data.database
import com.zuehlke.groceryshared.SHOPPING_LIST_DATA_PATH
import com.zuehlke.groceryshared.ShoppingItem
import com.zuehlke.groceryshared.CommUtil
import org.jetbrains.anko.*
import java.util.*


public class ShoppingListActivityFragment : Fragment(), DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AnkoLogger {

    private var itemList: MutableList<ShoppingItem> = ArrayList<ShoppingItem>()
    private var listView: ListView? = null
    private var listAdapter: ShoppingListAdapter? = null
    private var inputDialog: AlertDialog? = null
    private var deleteDialog: AlertDialog? = null
    private var longClickedIndex: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_shopping_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super<Fragment>.onActivityCreated(savedInstanceState)
        async {
            database
        }
        listView = find(R.id.shoppingList)
        createInputDialog()
        createDeleteDialog()
        setupListClickListener()
        configureListView()
        setupGoogleApiConnection()
    }

    private fun setupListClickListener() {
        listView?.onItemClick { parentView, clickedView, index, id ->
            if (index == itemList.size()) {
                inputDialog?.show()
            } else {
                var item = itemList[index]
                item.checked = !item.checked
                listAdapter?.notifyDataSetChanged()
                async {
                    database.updateItemState(item)
                }
                sendListToWearable()
            }
        }
        listView?.onItemLongClick { parentView, clickedView, index, id ->
            if (index == itemList.size()) {
                false
            } else {
                longClickedIndex = index
                deleteDialog?.setTitle("Delete ${itemList[index].title}?")
                deleteDialog?.show()
                true
            }
        }
    }

    private fun createInputDialog() {
        var builder = AlertDialog.Builder(getActivity())
        builder.setTitle("Add new item")
        var inputSection = getActivity().layoutInflater.inflate(R.layout.dialog_input, null)
        builder.setView(inputSection)
        val titleInput: EditText? = inputSection.find(R.id.itemEntry)
        builder.setPositiveButton(android.R.string.ok, { dialog, id ->
            // FIXME: "Ok" sometimes removes items instead of adding? WTF???
            var maxIndex = itemList.fold(0, { max: Int, item: ShoppingItem -> Math.max(max, item.id) })
            var item = ShoppingItem(maxIndex + 1, titleInput?.text?.toString() ?: "item $maxIndex", false)
            itemList.add(item)
            listAdapter?.notifyDataSetChanged()
            titleInput?.text = ""
            async {
                database.addListItem(item)
            }
            sendListToWearable()
        })
        builder.setNegativeButton(android.R.string.cancel, { dialog, id ->
            titleInput?.text = ""
        })
        inputDialog = builder.create()
    }

    private fun createDeleteDialog() {
        var builder = AlertDialog.Builder(getActivity())
        builder.setTitle("Delete?")
        builder.setPositiveButton("Delete", { dialog, id ->
            var item = itemList.remove(longClickedIndex)
            listAdapter?.notifyDataSetChanged()
            async {
                database.deleteListItem(item)
            }
            sendListToWearable()
        })
        builder.setNegativeButton("Keep", { dialog, id ->
        })
        deleteDialog = builder.create()
    }

    private fun configureListView() {
        var progressBar = ProgressBar(getActivity())
        progressBar.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
        progressBar.setIndeterminate(true)
        listView?.setEmptyView(progressBar)

        itemList = ArrayList<ShoppingItem>()
        listAdapter = ShoppingListAdapter(getActivity(), itemList)
        listView?.setAdapter(listAdapter)

        loadShoppingList()
    }

    private fun loadShoppingList() {
        async {
            var shoppingList = database.getShoppingList()
            itemList.clear()
            itemList.addAll(shoppingList)
            uiThread {
                listAdapter?.notifyDataSetChanged()
            }
        }
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
        println("--- Got data update from remote")
        async {
            CommUtil.updateItemListFromDataEventBuffer(itemList, buffer, {
                println("--- Update processed")
                itemList.forEach { item -> database.updateItemState(item) }
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
