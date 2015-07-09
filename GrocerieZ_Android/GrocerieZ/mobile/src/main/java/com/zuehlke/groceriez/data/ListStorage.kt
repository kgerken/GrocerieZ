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
//val Fragment.database: OldschoolListStorage
//    get() = OldschoolListStorage.Factory.getInstance(getActivity().getApplicationContext())

class ListStorage(context: Context)
: ManagedSQLiteOpenHelper(context, "shoppingList", null, DATABASE_VERSION), AnkoLogger {

    object Factory {
        private var instance: ListStorage? = null

        synchronized fun getInstance(ctx: Context): ListStorage {
            if (instance == null) {
                println("--- Initializing DB instance")
                instance = ListStorage(ctx.getApplicationContext())
                instance?.myDb = instance?.getWritableDatabase()
                instance?.onCreate(instance?.myDb!!)
                instance?.myDb?.close()
            }
            return instance!!
        }
    }

    private val TABLE_NAME = "shoppingList"
    private val KEY_ID = "id"
    private val KEY_TITLE = "title"
    private val KEY_CHECKED = "checked"

    var myDb: SQLiteDatabase? = null

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        throw UnsupportedOperationException()
    }

    override fun onCreate(db: SQLiteDatabase) {
        println("===== CREATING TABLE =====")
        db.createTable(TABLE_NAME, true,
                 KEY_ID to INTEGER + PRIMARY_KEY,
                 KEY_TITLE to TEXT,
                 KEY_CHECKED to BLOB)
        println("===== TABLE CREATED =====")
    }

    override fun getWritableDatabase(): SQLiteDatabase? {
        println("===== GETTING WRITABLE DB =====")
        return super<ManagedSQLiteOpenHelper>.getWritableDatabase()
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
                var updated = update(TABLE_NAME, KEY_CHECKED to if (item.checked) 1 else 0)
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
        println("--- Loading list from DB")
        var result: List<ShoppingItem> = ArrayList<ShoppingItem>()
        use {
            try {
                println("--- Performing select")
                var parser = rowParser { id: Int, title: String, checked: Int ->
                    ShoppingItem(id, title, checked > 0)
                }
                result = select(TABLE_NAME, KEY_ID, KEY_TITLE, KEY_CHECKED)
                        .orderBy(KEY_ID, SqlOrderDirection.ASC)
                        .exec {
                    parseList(parser)
                }
                println("--- List result: $result")
            } catch (exception: SQLiteException) {
                error("SELECT threw exception: $exception")
            } catch (exception: Exception) {
                error("Some other exception during SELECT: $exception")
            }
        }
        return result
    }
}

class OldschoolListStorage(context: Context)
: SQLiteOpenHelper(context, "shoppingList", null, DATABASE_VERSION) {

    object Factory {
        private var instance: OldschoolListStorage? = null

        synchronized fun getInstance(ctx: Context): OldschoolListStorage {
            if (instance == null) {
                instance = OldschoolListStorage(ctx.getApplicationContext())
                instance?.myDb = instance?.getWritableDatabase()
                instance?.onCreate(instance?.myDb!!)
            }
            return instance!!
        }
    }

    private val TABLE_NAME = "shoppingList"
    private val KEY_ID = "id"
    private val KEY_TITLE = "title"
    private val KEY_CHECKED = "checked"

    var myDb: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        // FIXME: OnCreate does not get called
        println("===== CREATING OS TABLE =====")
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY, $KEY_TITLE TEXT, $KEY_CHECKED INTEGER)")
        println("===== OS TABLE CREATED =====")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        throw UnsupportedOperationException()
    }

    override fun getWritableDatabase(): SQLiteDatabase? {
        println("===== GETTING WRITABLE OS DB =====")
        return super.getWritableDatabase()
    }

    public fun addListItem(item: ShoppingItem) {
        var db = getWritableDatabase()
        println("--- Writing to database: $db")
        try {
            var count = db?.compileStatement("INSERT INTO $TABLE_NAME ($KEY_TITLE, $KEY_CHECKED) " +
                    "VALUES ('${item.title}', ${if (item.checked) 1 else 0})")?.executeInsert()
            error("Inserted $count rows")
        } catch (exception: Exception) {
            error("INSERT threw exception: $exception")
        }
        db?.close()
    }

    public fun updateItemState(item: ShoppingItem) {
    }

    public fun deleteListItem(item: ShoppingItem) {
    }

    public fun getShoppingList(): List<ShoppingItem> {
        println("--- Loading list from DB")
        var db = getReadableDatabase()
        var result = ArrayList<ShoppingItem>()
        try {
            var queryBuilder = SQLiteQueryBuilder()
            queryBuilder.setTables(TABLE_NAME)
            var cursor = queryBuilder.query(db, arrayOf(KEY_ID, KEY_TITLE, KEY_CHECKED), null, null, null, null, "$KEY_ID ASC", null)
            println("--- Got ${cursor.getCount()} rows in DB")
            if (cursor.getCount() > 0) {
                cursor.moveToFirst()
                do {
                    result.add(ShoppingItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2) > 0))
                } while (cursor.moveToNext())
                cursor.close()
            }
        } catch (exception: SQLException) {
            error("SELECT threw exception: $exception")
        }
        db.close()
        return result
    }
}