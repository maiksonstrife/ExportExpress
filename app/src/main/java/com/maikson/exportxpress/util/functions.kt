package com.maikson.exportxpress.util

import android.content.Context
import android.media.RingtoneManager
import android.widget.Toast
import maikson.ExportXpressDEMO.R

fun unrecognizedCode( context: Context, callbackClear: ()->Unit = {} ){
    Toast
        .makeText(
            context,
            context.getString(R.string.unrecognized_code),
            Toast.LENGTH_SHORT )
        .show()

    callbackClear()
}

/*
 * Para que seja possível lançar um sinal sonoro logo
 * após a leitura de algum código de barra.
 * */
fun notification(context: Context){
    try {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context.applicationContext, notification)
        ringtone.play()
    }
    catch(e: Exception) { }
}