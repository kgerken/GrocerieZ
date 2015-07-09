package com.zuehlke.groceriez.data

import android.app.Fragment
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import com.zuehlke.groceryshared.ShoppingItem
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.db.*
import java.util.*

val DATABASE_VERSION = 2

val Fragment.database: ListStorage
    get() = ListStorage.Factory.getInstance(getActivity().getApplicationContext())

class ListStorage(context: Context)
: ManagedSQLiteOpenHelper(context, "shoppingList", null, DATABASE_VERSION), AnkoLogger {

    object Factory {
        private var instance: ListStorage? = null

        synchronized fun getInstance(ctx: Context): ListStorage {
            if (instance == null) {
                instance = ListStorage(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    private val TABLE_NAME = "shoppingList"
    private val KEY_ID = "id"
    private val KEY_TITLE = "title"
    private val KEY_CHECKED = "checked"

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        throw UnsupportedOperationException()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(TABLE_NAME, true,
                 KEY_ID to INTEGER + PRIMARY_KEY,
                 KEY_TITLE to TEXT,
                 KEY_CHECKED to BLOB)
    }

    public fun addListItem(item: ShoppingItem) {
        use {
            try {
                insert(TABLE_NAME,
                        KEY_ID to item.id,
                        KEY_TITLE to item.title,
                        KEY_CHECKED to item.checked)
            } catch (exception: SQLiteException) {
                error("INSERT threw exception")
            } catch (exception: Exception) {
                error("Some other exception during INSERT: $exception")
            }
        }
    }

    public fun updateItemState(item: ShoppingItem) {
        use {
            try {
                update(TABLE_NAME, KEY_CHECKED to if (item.checked) 1 else 0)
                        .where("id = {id}", "id" to item.id).exec()
            } catch (exception: SQLiteException) {
                error("UPDATE threw exception: $exception")
            } catch (exception: Exception) {
                error("Some other exception during UPDATE: $exception")
            }
        }
    }

    public fun deleteListItem(item: ShoppingItem) {
        use {
            try {
                delete(TABLE_NAME, "id = {id}", "id" to item.id)
            } catch (exception: SQLiteException) {
                error("DELETE threw exception: $exception")
            } catch (exception: Exception) {
                error("Some other exception during DELETE: $exception")
            }
        }
    }

    public fun getShoppingList(): List<ShoppingItem> {
        var result: List<ShoppingItem> = ArrayList<ShoppingItem>()
        use {
            try {
                var parser = rowParser { id: Int, title: String, checked: Int ->
                    ShoppingItem(id, title, checked > 0)
                }
                result = select(TABLE_NAME, KEY_ID, KEY_TITLE, KEY_CHECKED)
                        .orderBy(KEY_ID, SqlOrderDirection.ASC)
                        .exec {
                    parseList(parser)
                }
            } catch (exception: SQLiteException) {
                error("SELECT threw exception: $exception")
            } catch (exception: Exception) {
                error("Some other exception during SELECT: $exception")
            }
        }
        return result
    }
}
