package com.zuehlke.groceryshared

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.*

object CommUtil {
    public fun updateItemListFromDataEventBuffer(itemList: MutableList<ShoppingItem>, buffer: DataEventBuffer?, updatedCallback: () -> Unit) {
        buffer?.forEachIndexed { index, dataEvent ->
            println("--- Data changed: $dataEvent")
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                var item = dataEvent.getDataItem()
                if (item.getUri().getPath().compareTo(SHOPPING_LIST_DATA_PATH) == 0) {
                    itemList.clear()
                    var dataMap = DataMapItem.fromDataItem(item).getDataMap()
                    dataMap.keySet().sort().forEach { key ->
                        itemList.add(ShoppingItem(dataMap[key]))
                    }
                    updatedCallback()
                }
            }
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
}