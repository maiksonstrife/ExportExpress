package com.maikson.exportxpress

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.*
import com.maikson.exportxpress.view.MenuSelectionActivity
import android.widget.Toast
import com.maikson.exportxpress.singletons.*
import com.maikson.exportxpress.util.*
import maikson.ExportXpressDEMO.R


class StartActivity : AppCompatActivity() {
     var username: EditText? = null
     var passwordUser: EditText? = null
     var pbbar: ProgressBar? = null
     var cnpj: EditText? = null
     var button: Button? = null
     var cadastrarButton: Button? = null
     var senhaButton: Button?= null
     var sharedPreference: SharedPreference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        cnpj = findViewById(R.id.cnpjEditText)
        passwordUser = findViewById(R.id.senhaEditText)
        username = findViewById(R.id.cpfEditText)
        pbbar = findViewById(R.id.progressBar)
        pbbar!!.visibility = View.GONE
        button  = findViewById(R.id.loginButton)
        cadastrarButton = findViewById(R.id.buttonCadastrar)
        senhaButton = findViewById(R.id.buttonSenha)
        sharedPreference = SharedPreference(this)

        //Pegando SharedePreferences
        if (sharedPreference!!.getValueString("cpf")!=null) {
            val savedString = sharedPreference!!.getValueString("cpf")!!
            username!!.setText(savedString)
            Toast.makeText(this,"Data Retrieved",Toast.LENGTH_SHORT).show()
        }else{
            username!!.hint="12234456678"
        }
        if (sharedPreference!!.getValueString("cnpj")!=null) {
            val savedString = sharedPreference!!.getValueString("cnpj")!!
            cnpj!!.setText(savedString)
        }else{
            cnpj!!.hint="05555555000555"
        }
        if (sharedPreference!!.getValueString("password")!=null) {
            val savedString = sharedPreference!!.getValueString("password")!!
            passwordUser!!.setText(savedString)
        }else{
            passwordUser!!.hint="Senha Usuario"
        }





        //region Animação
        var rellay1 = RelativeLayout(this)
        var rellay2 = RelativeLayout(this)
        var handler = Handler()
        var runnable = Runnable {
            rellay1.visibility = View.VISIBLE
            rellay2.visibility = View.VISIBLE
        }
        rellay1 = findViewById(R.id.rellay1)
        rellay2 = findViewById(R.id.rellay2)
        handler.postDelayed(runnable, 2000) //2000 is the timeout for the splash
        // endregion



        //requisitando permissões do aplicativo
        checkPermissions()

        val buttonClickListener = CooldownClick { //adiciona cooldown no botão
            pbbar!!.visibility = View.VISIBLE
            checkLogin()
        }

        val buttonCadastrarClickListener = CooldownClick { //adiciona cooldown no botão
            val recipient = "maiksonstrife@gmail.com"
            val subject = "Cadastro ExportXpress"
            val message = "Olá, sou da empresa ___________ e gostaria de conhecer/Contratar o produto ExportXpress®"
            sendEmail(recipient, subject, message)
        }

        val buttonSenhaClickListener = CooldownClick { //adiciona cooldown no botão
            val recipient = "maiksonstrife@gmail.com"
            val subject = "Senha ExportXpress DEMO"
            val message = "O serviço Demo não faz verificação de usuário"
            sendEmail(recipient, subject, message)
        }


        cadastrarButton!!.setOnClickListener(buttonCadastrarClickListener)
        senhaButton!!.setOnClickListener(buttonSenhaClickListener)

        button!!.setOnClickListener(buttonClickListener) //login

    }


    fun checkLogin () {

        try{

            //Salvar SharedPreferences
            sharedPreference!!.save("cpf",username!!.text.toString())
            sharedPreference!!.save("cnpj",cnpj!!.text.toString())
            sharedPreference!!.save("password", passwordUser!!.text.toString())

            //salvar singleton
            UserSingleton.cpf = username!!.text.toString()
            UserSingleton.cnpj = cnpj!!.text.toString()

            val intent = Intent(this@StartActivity, MenuSelectionActivity::class.java)

            //lança activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                ContextCompat.startActivity(this@StartActivity, intent, null)
                pbbar!!.visibility = View.GONE


            } else {
                startActivity(intent)
                pbbar!!.visibility = View.GONE
            }
        }catch (e : Exception){
            pbbar!!.visibility = View.GONE
            Log.e("Erro: ", e.toString())
        }



    }

    fun checkPermissions(){

        var permissions1: Array<String> = emptyArray()


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso ao GPS", Toast.LENGTH_SHORT).show()
            //val permissions1 = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            //ActivityCompat.requestPermissions(this, permissions1,0)

            permissions1 += (Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso ao GPS", Toast.LENGTH_SHORT).show()
            //val permissions2 = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            //ActivityCompat.requestPermissions(this, permissions2,0)

            permissions1 += (Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso ao GPS", Toast.LENGTH_SHORT).show()
            //val permissions3 = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //ActivityCompat.requestPermissions(this, permissions3,0)

            permissions1 += (Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso ao GPS", Toast.LENGTH_SHORT).show()
            //val permissions4 = arrayOf(Manifest.permission.CAMERA)
            //ActivityCompat.requestPermissions(this, permissions4,0)

            permissions1 += (Manifest.permission.CAMERA)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Autorize o aplicativo para acesso a INTERNET", Toast.LENGTH_SHORT).show()
            //val permissions4 = arrayOf(Manifest.permission.CAMERA)
            //ActivityCompat.requestPermissions(this, permissions4,0)

            permissions1 += (Manifest.permission.INTERNET)
        }

        try {
            ActivityCompat.requestPermissions(this, permissions1, 0)
        }catch(e : java.lang.Exception){
            Toast.makeText(this, "Permissões Aceitas", Toast.LENGTH_SHORT).show()
        }

    }

    private fun sendEmail(recipient: String, subject: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}