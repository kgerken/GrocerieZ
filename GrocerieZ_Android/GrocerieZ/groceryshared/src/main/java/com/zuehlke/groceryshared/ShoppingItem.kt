package com.zuehlke.groceryshared

import com.google.android.gms.wearable.DataMap

val ITEM_FIELD_ID = "id"
val ITEM_FIELD_TITLE = "title"
val ITEM_FIELD_CHECKED = "checked"

val SHOPPING_LIST_DATA_PATH = "/shoppingList"

data class ShoppingItem(val id: Int, var title: String, var checked: Boolean) {

    constructor(dataMap: DataMap) : this(
            dataMap.getInt(ITEM_FIELD_ID, 1337),
            dataMap.getString(ITEM_FIELD_TITLE, "unknown"),
            dataMap.getBoolean(ITEM_FIELD_CHECKED, false)
    )

    public fun toDataMap(): DataMap {
        var map: DataMap = DataMap()
        map.putInt(ITEM_FIELD_ID, id)
        map.putString(ITEM_FIELD_TITLE, title)
        map.putBoolean(ITEM_FIELD_CHECKED, checked)
        return map
    }
}