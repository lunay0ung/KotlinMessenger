package com.example.KotlinMessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.KotlinMessenger.R
import com.example.KotlinMessenger.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import timber.log.Timber

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        Timber.plant(Timber.DebugTree())


        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) //NewMessageActivity에서 유저정보를 통째로 받아옴
        //val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.username //액션바에 유저네임을 띄운다

        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        recyclerview_chat_log.adapter = adapter
    }//onCreate
}//ChatLogActivity

class ChatFromItem : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        }//bind

    override fun getLayout(): Int {

        return R.layout.chat_from_row

       }//getLayout
}//ChatFromItem

class ChatToItem : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }//bind

    override fun getLayout(): Int {

        return R.layout.chat_to_row

    }//getLayout
}//ChatToItem