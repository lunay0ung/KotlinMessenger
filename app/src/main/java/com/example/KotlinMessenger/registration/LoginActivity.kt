package com.example.KotlinMessenger.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.KotlinMessenger.R
import com.example.KotlinMessenger.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Timber.plant(Timber.DebugTree())

        login_button_login.setOnClickListener {
            performLogin()
        }

        back_to_register_text_view.setOnClickListener { finish() }
    }//onCreate

    private fun performLogin(){
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()
        Timber.d("Attempt login with email/pw: $email/***")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful) {
                    Timber.d("로그인 안 됨 ${it.result}")
                    //return@addOnCompleteListener
                }

                startActivity(Intent(this, LatestMessagesActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)))

            }
            .addOnFailureListener {
                Timber.d("로그인 실패: ${it.message}")
                Toast.makeText(this, "로그인 실패: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}//LoginActivity
