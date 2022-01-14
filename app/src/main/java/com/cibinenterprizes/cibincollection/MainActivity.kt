package com.cibinenterprizes.cibincollection

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.network_alart_dialog.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkConnection()
        auth = FirebaseAuth.getInstance()


        welcome_login.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
        welcome_register.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }

    private fun checkConnection() {

        val manager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo

        if (null == networkInfo) {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.network_alart_dialog)

            dialog.setCanceledOnTouchOutside(false)

            dialog.window!!.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.btn_try_again.setOnClickListener {
                recreate()
            }
            dialog.show()
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }

        if (user != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        if(requestCode == LOCATION_PERMISSION_REQUEST)
        {
            if(grantResults.contains(PackageManager.PERMISSION_GRANTED))
            {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED)
                {

                    return
                }

            }else{
                Toast.makeText(this,"User has not granted location access permission", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}