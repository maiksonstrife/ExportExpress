package com.maikson.exportxpress

import android.content.Intent
import android.database.sqlite.SQLiteCursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SimpleCursorAdapter
import android.widget.AdapterView
import android.widget.ListView
import com.maikson.exportxpress.sqlite.DBManager
import com.maikson.exportxpress.sqlite.DatabaseHelper
import maikson.ExportXpressDEMO.R

class ListaEnvios : AppCompatActivity() {

    private var dbManager: DBManager? = null
    private var listView: ListView? = null
    private var adapter: SimpleCursorAdapter? = null
    internal val from = arrayOf(DatabaseHelper._ID, DatabaseHelper.SUBJECT, DatabaseHelper.DESC)
    internal val to = intArrayOf(R.id.id, R.id.title, R.id.desc)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_envios)

        dbManager = DBManager(this)
        dbManager!!.open()
        val cursor = dbManager!!.fetch()
        listView = findViewById(R.id.listFiles)
        adapter = SimpleCursorAdapter(this, R.layout.activity_view_record, cursor, from, to, 0)
        adapter!!.notifyDataSetChanged()
        listView!!.adapter = adapter
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // This is your listview's selected item
            var item = parent.getItemAtPosition(position) as SQLiteCursor
            var web = item.getString(cursor!!.getColumnIndex("description"))
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(web))
            startActivity(browserIntent)
        }
    }
}
