package com.example.quadcare.Models
import android.annotation.SuppressLint
import com.example.quadcare.constants.*
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBStorage(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val createEmployeeTable = "CREATE TABLE " + APP_TABLE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_ANAME + " VARCHAR(256)," +
                COL_ADESCRIPTION + " VARCHAR(256))";
        p0?.execSQL(createEmployeeTable)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertData(tableName: String, insertValues: HashMap<String, Any>) {
        val db = this.writableDatabase
        var cv = ContentValues()

        for (key in insertValues.keys) {
                cv.put(key, insertValues[key] as String)
        }
        db.insert(tableName, null, cv)
    }

    @SuppressLint("Range")
    fun getData(tableName: String): ArrayList<activity> {
        var list: ArrayList<activity> = ArrayList()
        val db = this.readableDatabase
        val query = SELECT_ALL_QUERY + tableName
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                var employee = activity()
                employee.actName = result.getString(result.getColumnIndex(COL_ANAME))
                employee.actDecription = result.getString(result.getColumnIndex(COL_ADESCRIPTION))
                list.add(employee)
            } while (result.moveToNext())
        }
        return list
    }
}

class activity() {

    var actName: String = ""
    var actDecription: String = ""
}