package com.maikson.exportxpress.crop

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.maikson.exportxpress.SourceManager
import com.maikson.exportxpress.processor.Corners
import com.maikson.exportxpress.processor.TAG
import com.maikson.exportxpress.processor.cropPicture
import com.maikson.exportxpress.processor.enhancePicture
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File
import java.io.FileOutputStream
import android.provider.MediaStore
import android.content.ContentValues
import com.maikson.exportxpress.singletons.BarcodeSingleton
import android.location.Location
import android.location.LocationManager
import android.media.ExifInterface
import com.maikson.exportxpress.util.GPS
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


const val IMAGES_DIR = "Infor_scanner"

class CropPresenter(val context: Context, private val iCropView: ICropView.Proxy) {
    private val picture: Mat? = SourceManager.pic

    private val corners: Corners? = SourceManager.corners
    private var croppedPicture: Mat? = null
    private var enhancedPicture: Bitmap? = null
    private var croppedBitmap: Bitmap? = null

    init {
        iCropView.getPaperRect().onCorners2Crop(corners, picture?.size())
        val bitmap = Bitmap.createBitmap(picture?.width() ?: 1080, picture?.height()
                ?: 1920, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(picture, bitmap, true)
        iCropView.getPaper().setImageBitmap(bitmap)

    }

    //opcional, adicionar fotos a galeria do celular
    fun addImageToGallery(filePath: String, context: Context) {

        val values = ContentValues()

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.MediaColumns.DATA, filePath)

        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    fun crop() {



        if (picture == null) {
            Log.i(TAG, "picture null?")
            return
        }

        if (croppedBitmap != null) {
            Log.i(TAG, "already cropped")
            return
        }

        Observable.create<Mat> {
            it.onNext(cropPicture(picture, iCropView.getPaperRect().getCorners2Crop()))
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pc ->
                    Log.i(TAG, "cropped picture: " + pc.toString())
                    croppedPicture = pc
                    croppedBitmap = Bitmap.createBitmap(pc.width(), pc.height(), Bitmap.Config.ARGB_8888)
                    Utils.matToBitmap(pc, croppedBitmap)
                    iCropView.getCroppedPaper().setImageBitmap(croppedBitmap)
                    iCropView.getPaper().visibility = View.GONE
                    iCropView.getPaperRect().visibility = View.GONE
                }
    }

    fun enhance() {
        if (croppedBitmap == null) {
            Log.i(TAG, "picture null?")
            return
        }

        Observable.create<Bitmap> {
            it.onNext(enhancePicture(croppedBitmap))
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pc ->
                    enhancedPicture = pc
                    iCropView.getCroppedPaper().setImageBitmap(pc)
                }
    }

    fun save() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "please grant WRITE file permission and try again", Toast.LENGTH_SHORT).show()
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "please grant GPS permission and try again", Toast.LENGTH_SHORT).show()
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "please grant NETWORK permission and try again", Toast.LENGTH_SHORT).show()
        }

        else {
            val dir = File(Environment.getExternalStorageDirectory(), IMAGES_DIR)
            if (!dir.exists()) {
                dir.mkdirs()
            }


            //Método caso tenha usado enhanced
            val pic = enhancedPicture
            if (null != pic) {
                var s1 = BarcodeSingleton
                var filename = s1.barcode  //teste singleton
                val file = File(dir, "$filename.jpeg")
                val outStream = FileOutputStream(file)
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) as Location


                pic.compress(Bitmap.CompressFormat.JPEG, 100, outStream)

                outStream.flush()
                outStream.close()


                //Insere geolocalização nos metadados do arquivo
                MarkGeoTagImage(file.absolutePath, location)



                //addImageToGallery(file.absolutePath, this.context) //Se quiser deixar na galeria visivel

            } else {
                val cropPic = croppedBitmap
                var s1 = BarcodeSingleton
                var filename = s1.barcode

                if (null != cropPic) {
                    val file = File(dir, "$filename.jpeg")
                    val outStream = FileOutputStream(file)
                    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) as Location

                    //salva o bitmap no outStream
                    cropPic.compress(Bitmap.CompressFormat.JPEG, 100, outStream)

                    outStream.flush()
                    outStream.close()

                    //Insere geolocalização nos metadados do arquivo
                    MarkGeoTagImage(file.absolutePath, location)


                    //addImageToGallery(file.absolutePath, this.context) //Se quiser deixar na galeria visivel
                }
            }
        }
    }

    fun MarkGeoTagImage(imagePath: String, location: Location) {
        try {
            val exif = ExifInterface(imagePath)
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS.convert(location.latitude))
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.convert(location.latitude))
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(location.longitude))
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.convert(location.longitude))
            val fmt_Exif = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
            exif.setAttribute(ExifInterface.TAG_DATETIME, fmt_Exif.format(Date(location.time)))
            exif.saveAttributes()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}