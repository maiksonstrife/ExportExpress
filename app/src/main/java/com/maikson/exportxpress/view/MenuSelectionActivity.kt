package com.maikson.exportxpress.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import com.maikson.exportxpress.ListaEnvios
import com.maikson.exportxpress.MainActivity
import com.maikson.exportxpress.UploadActivity
import com.maikson.exportxpress.util.CooldownClick
import kotlinx.android.synthetic.main.activity_menu_selection.*
import maikson.ExportXpressDEMO.R

class MenuSelectionActivity : AppCompatActivity() {
    var barcodebutton: Button? = null
    var ftpbutton: Button? = null
    var sqlViewButton: Button? = null
    var xpressAdminButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_selection)

        barcodebutton  = findViewById(R.id.barcodeButton)
        ftpbutton = findViewById(R.id.ftpButton)
        sqlViewButton = findViewById(R.id.sqlViewButton)
        xpressAdminButton = findViewById(R.id.buttonXpressAdmin)

        val barcodeButtonClickListener = CooldownClick {
            val intent = Intent(this@MenuSelectionActivity, MainActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, intent, null)
            } else {
                startActivity(intent)
            }
        }

        val ftpButtonClickListener = CooldownClick {
            val intent = Intent(this@MenuSelectionActivity, UploadActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, intent, null)
            } else {
                startActivity(intent)
            }
        }

        val sqlButtonClickListener = CooldownClick {
            val intent = Intent(this@MenuSelectionActivity, ListaEnvios::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, intent, null)
            } else {
                startActivity(intent)
            }
        }

        val xpressAdminButtonClickListener = CooldownClick {

            val url = "http://azure.infordoc.com/maikson/Bootstrap/EntregasXpress/login.html"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startActivity(this@MenuSelectionActivity, i, null)
            } else {
                startActivity(i)
            }
        }

        var rellay1: RelativeLayout = RelativeLayout(this)
        var rellay2: RelativeLayout = RelativeLayout(this)
        var handler = Handler()
        var runnable: Runnable = Runnable {
            rellay1.visibility = View.VISIBLE

        }
        rellay1 = findViewById(R.id.rellay1)
        handler.postDelayed(runnable, 2000) //2000 is the timeout for the splash

        barcodeButton!!.setOnClickListener(barcodeButtonClickListener)
        ftpbutton!!.setOnClickListener(ftpButtonClickListener)
        sqlViewButton!!.setOnClickListener(sqlButtonClickListener)
        xpressAdminButton!!.setOnClickListener(xpressAdminButtonClickListener)

    }

}
