package com.example.KotlinMessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.KotlinMessenger.R
import com.example.KotlinMessenger.models.User
import com.example.KotlinMessenger.registration.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber


//최신 메시지를 확인하는 액티비티
class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser : User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        Timber.plant(Timber.DebugTree())

        fetchCurrentUser()

        verifyUserInLoggedIn()


    }//onCreate

    private fun fetchCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                currentUser = p0.getValue(User::class.java)
                Timber.d("current user: ${currentUser?.username}")

            }
        })

    }//fetchCurrentUser

    private fun verifyUserInLoggedIn(){
        //유저가 로그인했는지 확인한다
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null) { //유저가 로그인하지 않은 상태라면 회원가입 화면으로 이동한다
            startActivity(Intent(this, RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)))
        }
    }//verifyUserInLoggedIn

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                startActivity(Intent(this, NewMessageActivity::class.java))
            }

            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)))
            }
        }//when
        return super.onOptionsItemSelected(item)
    }//onOptionsItemSelected

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }//onCreateOptionsMenu
}//LatestMessagesActivity
