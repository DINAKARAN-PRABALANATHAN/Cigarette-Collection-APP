package com.cibinenterprizes.cibincollection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cibinenterprizes.cibincollection.Model.UserDetails
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import kotlinx.android.synthetic.main.activity_register.*
import java.util.concurrent.TimeUnit

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var verificationId: String
    var lantitude: String? = null
    var longitude: String? = null
    lateinit var token: PhoneAuthProvider.ForceResendingToken
    var verificationInProgress: Boolean = false
    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 1
    val databaseReference= FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        var phoneNumber = findViewById<EditText>(R.id.registration_verify_number)
        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
        var countryCode = findViewById<CountryCodePicker>(R.id.ccp)
        progressBar2.setVisibility(View.INVISIBLE)

        registration_login.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        registration_verify_button.setOnClickListener {
            val phoneNum = "+" + countryCode.selectedCountryCode + phoneNumber.text.toString().trim()
            if(!verificationInProgress){
                if(!phoneNumber.text.toString().trim().isEmpty() && phoneNumber.text.toString().length == 10){
                    Log.i("Register", phoneNum)
                    progressBar.setVisibility(View.VISIBLE)
                    requestOTP(phoneNum)
                }else{
                    phoneNumber.setError("Phone number is not Valid")
                }
            }else{
                val userOTP = registration_otp.text.toString().trim()
                if (!userOTP.isEmpty() && userOTP.length == 6){
                    var credential = PhoneAuthProvider.getCredential(verificationId, userOTP)
                    verifyAuth(credential, phoneNum)
                }else{
                    registration_otp.setError("Valid OTP is required.")
                }
            }
        }
    }



    private fun requestOTP(phoneNum: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, object:
            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationInProgress = true
                progressBar.setVisibility(View.GONE)
                registration_otp.setVisibility(View.VISIBLE)
                verificationId = p0
                token = p1

                registration_verify_button.setText("Verify")
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                verifyAuth(p0, phoneNum)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@Register, "Cannot Create Account "+p0.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun verifyAuth(credential: PhoneAuthCredential, phoneNum: String) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                registration.setText("PROFILE DETAILS")
                ccp.setVisibility(View.GONE)
                registration_mobile_img.setVisibility(View.GONE)
                registration_verify_number.setVisibility(View.GONE)
                registration_otp.setVisibility(View.GONE)
                registration_verify_button.setVisibility(View.GONE)
                registration_user_name.setVisibility(View.VISIBLE)
                registration_store_name.setVisibility(View.VISIBLE)
                registration_mobile_number.setVisibility(View.VISIBLE)
                registration_mobile_number.setText(phoneNum)
                registration_map.setVisibility(View.VISIBLE)
                linearLayout.setVisibility(View.VISIBLE)
                registration_button.setVisibility(View.VISIBLE)
                registration_map.setOnClickListener {
                    startActivityForResult(Intent(this,ReisterMapsActivity::class.java),2858)
                }
                databaseReference.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var presentId = snapshot.child("ID").getValue().toString()
                        var storeId = presentId.toInt()
                        var id = storeId+1
                        registration_button.setOnClickListener {
                            if(lantitude == null && longitude == null){
                                Toast.makeText(this@Register, "Please give your store location...", Toast.LENGTH_SHORT).show()
                            }else{
                                progressBar2.setVisibility(View.VISIBLE)
                                if (register_terms_and_conditions.isChecked) {
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                                    val db = FirebaseDatabase.getInstance().reference
                                    val userName = registration_user_name.text.toString()
                                    val storeName = registration_store_name.text.toString()
                                    val mobileNumber = registration_mobile_number.text.toString()
                                    val termsAndConditions = "Accepted"
                                    val userDetails = UserDetails(userName,storeName,mobileNumber,lantitude,longitude,termsAndConditions, "S"+id, uid,"0","0")
                                    db.child("User Details").child("S"+id).setValue(userDetails).addOnCompleteListener {
                                        db.child("ID").setValue(id).addOnCompleteListener {
                                            db.child("Store ID").child(uid).child("id").setValue("S"+id).addOnCompleteListener {
                                                progressBar2.setVisibility(View.GONE)
                                                startActivity(Intent(this@Register, HomeActivity::class.java))
                                            }
                                        }
                                    }

                                }else{
                                    Toast.makeText(this@Register, "Please accept terms and conditions...", Toast.LENGTH_SHORT).show()
                                    progressBar2.setVisibility(View.GONE)
                                    return@setOnClickListener
                                }
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

                Toast.makeText(this, "Mobile Number is verified", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Mobile Number is Not verified", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==2858){
            lantitude = data!!.getStringExtra("Lantitude")
            longitude = data!!.getStringExtra("Longitude")
        }
    }
}