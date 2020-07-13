package com.maikson.exportxpress

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.media.ExifInterface
import android.net.ParseException
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import com.maikson.exportxpress.interfaces.PDFInterface
import com.maikson.exportxpress.singletons.UserSingleton
import com.maikson.exportxpress.sqlite.DBManager
import com.maikson.exportxpress.util.SharedPreference
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import maikson.ExportXpressDEMO.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import okhttp3.RequestBody.create as create1

class UploadActivity : AppCompatActivity() {

     var myList: ArrayList<String>? = null
     var listFiles: ListView? = null
     var IMAGES_DIR = "Infor_scanner"
     var progressBar: ProgressBar? = null
     var files: Array<File>? = null
     var dbManager: DBManager? = null
     var sharedPreference: SharedPreference?=null
     val mapsPrefix = "https://www.google.com/maps?q="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        sharedPreference = SharedPreference(this)
        listFiles = findViewById(R.id.listFiles)
        val btnUpload: Button = findViewById(R.id.btnUpload)
        progressBar = findViewById(R.id.progressBar)
        myList = ArrayList()
        listarPasta()
        files = listarPasta()
        dbManager = DBManager(this)
        dbManager!!.open()

        btnUpload.setOnClickListener {
            try{
                chamarAsyncTask(files!!)
            }catch(e : java.lang.Exception){
                Log.e("erro no botao", e.toString())
            }
        }
    }

    private fun chamarAsyncTask(files: Array<File>) {
        val uploadFiles = UploadFileAsync()
        uploadFiles.execute(files)
    }


        inner class UploadFileAsync : AsyncTask<Array<File> , Int, Int>() {

        override fun onPreExecute() {
                progressBar!!.isIndeterminate = true
        }

        override fun doInBackground(vararg params: Array<File>): Int? {
            val reset = 0

            publishProgress(1)

            files!!.forEach {

                var locationReaded = readGeoTagImage(it.absolutePath)
                val longitudeR = "-" + locationReaded.longitude.toString()
                val latitudeR  = "-" + locationReaded.latitude.toString()
                val mapsAddress = "$mapsPrefix$latitudeR,$longitudeR"
                var bm = BitmapFactory.decodeFile(it.absolutePath)
                var pdfPath = createPdfBox(bm, it.absolutePath) //pdf salvo, jpeg deletado


                //ENVIO
                try {
                    var pdfString = "http://azure.infordoc.com/Maikson/Applications/mobile/uploadedFiles/"
                    uploadPDF(pdfPath,mapsAddress)
                    Thread.sleep(1000)
                    pdfString += UserSingleton.cnpj + pdfPath.substring(pdfPath.lastIndexOf("/"))
                    dbManager!!.insert("website", pdfString)
                    //Se enviou deleta ambos arquivos
                    File(pdfPath).delete()
                    it.delete()
                } catch (e: Exception) {
                    //Se não enviou deleta apenas PDF
                    File(pdfPath).delete()
                }

            }

            return reset
        }

        override fun onPostExecute(reset: Int?) {

            progressBar!!.progress = reset!!
            progressBar!!.isIndeterminate = false
            Toast.makeText(applicationContext, "Finalizado", Toast.LENGTH_SHORT).show()
            listFiles!!.adapter = null
            listarPasta()
        }

            override fun onProgressUpdate(vararg values: Int?) {
                progressBar!!.progress = values[0]!!

                super.onProgressUpdate(*values)
            }
    }


    fun uploadPDF(path: String, mapAddress: String) {

        val todayDate = Calendar.getInstance().time
        val formatter =  SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val todayString = formatter.format(todayDate)
        val pdfName = Calendar.getInstance().timeInMillis.toString()
        val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
        val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(PDFInterface.IMAGEURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

        val file = File(path)
        val requestBody = create1(MediaType.parse("*/*"), file)
        val fileToUpload = MultipartBody.Part.createFormData("filename", file.name, requestBody)
        val filename = RequestBody.create(MediaType.parse("text/plain"), pdfName)
        val getResponse = retrofit.create(PDFInterface::class.java!!)

            val geoloc = RequestBody.create(MediaType.parse("text/plain"), mapAddress)
            val data = RequestBody.create(MediaType.parse("text/plain"), todayString)
            var getCPF = UserSingleton.cpf!!
            if (getCPF.isNullOrEmpty()){
                getCPF = sharedPreference!!.getValueString("cpf")!!
            }
            var getCNPJ = UserSingleton.cnpj!!
            if (getCNPJ.isNullOrEmpty()){
                getCNPJ = sharedPreference!!.getValueString("cnpj")!!
            }
            val cpf = RequestBody.create(MediaType.parse("text/plain"), getCPF)
            val cnpj = RequestBody.create(MediaType.parse("text/plain"), getCNPJ)
            val call = getResponse.uploadImage(fileToUpload, filename, data, geoloc, cpf, cnpj)
        try{
            call.execute()
        }catch (e : Exception){
            Log.e("erro Upload: ", e.toString())
        }
    }

    private fun  listarPasta(): Array<File> {
        myList!!.clear()
        val dir = File(Environment.getExternalStorageDirectory(), IMAGES_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val files = dir.listFiles()

        for (i in files.indices) {
            myList!!.add(files[i].name)
            Log.d("Files", "FileName:" + files[i].name)
        }

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, myList)
        arrayAdapter.notifyDataSetChanged()
        listFiles!!.adapter = arrayAdapter

        return files
    }

    fun  readGeoTagImage(imagePath : String): Location
    {
        val loc =  Location("")
        try {
            val exif = ExifInterface(imagePath)
            val latlong = FloatArray(2)

            if (exif.getLatLong(latlong)) {
                loc.latitude = latlong[0].toDouble()
                loc.longitude = latlong[1].toDouble()
            }
            val date : String = exif.getAttribute (ExifInterface.TAG_DATETIME);
            val fmt_Exif =  SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
            loc.time = fmt_Exif.parse(date).time

        } catch ( e : IOException) {
            e.printStackTrace()
        } catch ( e : ParseException) {
            e.printStackTrace()
        }
        return loc
    }

    fun createPdfBox(bitmap : Bitmap, string : String): String{
        val directory = File(string).path
        val path = directory.substringBeforeLast("/")
        val filename = File(string).nameWithoutExtension
        val targetPdf = "$path/$filename.pdf"
        val document =  PDDocument()
        val  w : Float  = bitmap.width.toFloat()
        val  h: Float =   bitmap.height.toFloat()
        val page =  PDPage(PDRectangle(w, h))
        document.addPage(page)


        var contentStream =  PDPageContentStream (document, page)
        var ximage : PDImageXObject = JPEGFactory.createFromImage(document, bitmap, 0.75f, 72)
        contentStream.drawImage(ximage, 0f, 0f)
        contentStream.close()
        try {
            document.save(targetPdf)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        document.close()
        return targetPdf
    }
}
