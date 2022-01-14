package com.cibinenterprizes.cibincollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.concurrent.TimeUnit

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var verificationId: String
    lateinit var token: PhoneAuthProvider.ForceResendingToken
    var verificationInProgress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        var phoneNumber = findViewById<EditText>(R.id.login_verify_number)
        var progressBar = findViewById<ProgressBar>(R.id.login_progressBar)
        var countryCode = findViewById<CountryCodePicker>(R.id.login_ccp)

        login_verify_button.setOnClickListener {
            if(!verificationInProgress){
                if(!phoneNumber.text.toString().trim().isEmpty() && phoneNumber.text.toString().length == 10){
                    val phoneNum = "+" + countryCode.selectedCountryCode + phoneNumber.text.toString().trim()
                    Log.i("Register", phoneNum)
                    progressBar.setVisibility(View.VISIBLE)
                    requestOTP(phoneNum)
                }else{
                    phoneNumber.setError("Phone number is not Valid")
                }
            }else{
                val userOTP = login_otp.text.toString().trim()
                if (!userOTP.isEmpty() && userOTP.length == 6){
                    var credential = PhoneAuthProvider.getCredential(verificationId, userOTP)
                    verifyAuth(credential)
                }else{
                    login_otp.setError("Valid OTP is required.")
                }
            }
        }
        login_register_button.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }
    }
    private fun requestOTP(phoneNum: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, object:
            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationInProgress = true
                login_progressBar.setVisibility(View.GONE)
                login_otp.setVisibility(View.VISIBLE)
                verificationId = p0
                token = p1

                login_verify_button.setText("Verify")
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                verifyAuth(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@Login, "Cannot Create Account "+p0.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun verifyAuth(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }
}