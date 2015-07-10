package com.zuehlke.groceryshared

import android.net.Uri
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.*
import org.jetbrains.anko.AnkoLogger
import java.util
import java.util.*

object CommUtil: AnkoLogger {
    public fun updateItemListFromDataEventBuffer(itemList: MutableList<ShoppingItem>, buffer: DataEventBuffer?, updatedCallback: () -> Unit) {
        buffer?.forEachIndexed { index, dataEvent ->
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                var item = dataEvent.getDataItem()
                if (item.getUri().getPath().compareTo(SHOPPING_LIST_DATA_PATH) == 0) {
                    updateItemListFromDataItem(item, itemList, updatedCallback)
                }
            }
        }
    }

    private fun updateItemListFromDataItem(dataItem: DataItem, itemList: MutableList<ShoppingItem>, updatedCallback: () -> Unit) {
        itemList.clear()
        var dataMap = DataMapItem.fromDataItem(dataItem).getDataMap()
        dataMap.keySet().sort().forEach { key ->
            var shoppingItem = ShoppingItem(dataMap[key])
            itemList.add(shoppingItem)
        }
        updatedCallback()
    }

    public fun sendItemListViaApiClient(itemList: MutableList<ShoppingItem>, googleApiClient: GoogleApiClient?) {
        var putDataMapReq = PutDataMapRequest.create(SHOPPING_LIST_DATA_PATH)

        for (shoppingListEntry in itemList) {
            var key: String = shoppingListEntry.id.toString()
            var datamap: DataMap = shoppingListEntry.toDataMap()
            putDataMapReq.getDataMap().putDataMap(key, datamap)
        }
        var putDataReq: PutDataRequest = putDataMapReq.asPutDataRequest()
        var pendingResult = Wearable.DataApi.putDataItem(googleApiClient, putDataReq)
        pendingResult.setResultCallback({ it ->
            if (it.getStatus().isSuccess()) {
                debug("--- Sent data successfully")
            }
        })
    }

    public fun updateListFromRemote(itemList: MutableList<ShoppingItem>, googleApiClient: GoogleApiClient?, updatedCallback: () -> Unit) {
        if (googleApiClient != null) {
            var pendingResult = Wearable.DataApi.getDataItem(googleApiClient, getUriForRemoteShoppingList(googleApiClient))
            pendingResult.setResultCallback({result ->
                updateItemListFromDataItem(result.getDataItem(), itemList, updatedCallback)
            })
        }
    }

    private fun getUriForRemoteShoppingList(googleApiClient: GoogleApiClient): Uri {
        var nodeId = getRemoteNodeId(googleApiClient)
        return Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).authority(nodeId).path(SHOPPING_LIST_DATA_PATH).build()
    }

    private fun getLocalNodeId(googleApiClient: GoogleApiClient): String {
        var nodeResult = Wearable.NodeApi.getLocalNode(googleApiClient).await()
        return nodeResult.getNode().getId()
    }

    private fun getRemoteNodeId(googleApiClient: GoogleApiClient): String? {
        var nodesResult = Wearable.NodeApi.getConnectedNodes(googleApiClient).await()
        var nodes = nodesResult.getNodes()
        if (nodes.size() > 0) {
            return nodes.first().getId()
        }
        return null
    }
}