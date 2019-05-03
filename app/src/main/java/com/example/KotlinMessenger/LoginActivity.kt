package com.example.KotlinMessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.KotlinMessenger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Timber.plant(Timber.DebugTree())

        login_button_login.setOnClickListener {



        }

        back_to_register_text_view.setOnClickListener { finish() }
    }//onCreate

    private fun performLogin(){
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()
        Timber.d("Attempt login with email/pw: $email/***")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{

            }
            .addOnFailureListener {

            }
    }
}//LoginActivity
