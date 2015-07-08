package com.zuehlke.groceryshared

import android.net.Uri
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.*
import java.util
import java.util.*

object CommUtil {
    public fun updateItemListFromDataEventBuffer(itemList: MutableList<ShoppingItem>, buffer: DataEventBuffer?, updatedCallback: () -> Unit) {
        buffer?.forEachIndexed { index, dataEvent ->
            println("--- Data changed: $dataEvent")
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                var item = dataEvent.getDataItem()
                if (item.getUri().getPath().compareTo(SHOPPING_LIST_DATA_PATH) == 0) {
                    updateItemListFromDataItem(item, itemList, updatedCallback)
                }
            }
        }
    }

    public fun updateItemListFromDataItem(item: DataItem?, itemList: MutableList<ShoppingItem>, updatedCallback: () -> Unit) {
        if (item != null) {
            itemList.clear()
            var dataMap = DataMapItem.fromDataItem(item).getDataMap()
            dataMap.keySet().sort().forEach { key ->
                itemList.add(ShoppingItem(dataMap[key]))
            }
            updatedCallback()
        }
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
        println("--- Sending data item")
        pendingResult.setResultCallback({ it ->
            if (it.getStatus().isSuccess()) {
                println("--- Sent data successfully")
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

    // TODO: Use this to properly query the current data item state
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