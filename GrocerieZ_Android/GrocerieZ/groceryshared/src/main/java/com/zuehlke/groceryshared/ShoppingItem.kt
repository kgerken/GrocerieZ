package com.zuehlke.groceryshared

import com.google.android.gms.wearable.DataMap

val ITEM_FIELD_ID = "id"
val ITEM_FIELD_TITLE = "title"
val ITEM_FIELD_CHECKED = "checked"

val SHOPPING_LIST_DATA_PATH = "/shoppingList"

data class ShoppingItem(val id: Int, var title: String, var checked: Boolean) {

    public fun toDataMap(): DataMap {
        var map: DataMap = DataMap()
        map.putInt(ITEM_FIELD_ID, id)
        map.putString(ITEM_FIELD_TITLE, title)
        map.putBoolean(ITEM_FIELD_CHECKED, checked)
        return map
    }
}