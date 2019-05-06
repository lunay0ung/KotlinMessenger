package com.example.KotlinMessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth


//최신 메시지를 확인하는 액티비티
class LatestMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        verifyUserInLoggedIn()


    }//onCreate


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
