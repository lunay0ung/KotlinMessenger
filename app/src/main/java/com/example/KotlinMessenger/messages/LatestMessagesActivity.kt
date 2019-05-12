package com.example.KotlinMessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.KotlinMessenger.R
import com.example.KotlinMessenger.models.ChatMessage
import com.example.KotlinMessenger.models.User
import com.example.KotlinMessenger.registration.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
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


        //setupDummyRows()
        recyclerview_latest_messages.adapter = adapter

        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserInLoggedIn()


    }//onCreate


    class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){

        override fun bind(viewHolder: ViewHolder, position: Int) {
            //viewHolder.itemView.username_textview_latest_message

            viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

        }

        override fun getLayout(): Int {
           return R.layout.latest_message_row
        }
    }

    val latestMeessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMeessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }//refreshRecyclerViewMessages

    private fun listenForLatestMessages(){

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            //if a new user messages to us it will be notified via onChildAdded
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                //we convert the snapshot that we are seeing into a chat message
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                //then put the actual message into our hashmap
                latestMeessagesMap[p0.key!!] = chatMessage
                //the key is belong to the user that we are messaging

                //and reload everything inside of recyclerview
                //by first clearing out all messages and just adding it back in by using all of the values inside of the hashmap
                refreshRecyclerViewMessages()


                //convert the snapshot that we are seeing into an actual message
                // val chatMessage = p0.getValue(ChatMessage::class.java)?:return
                // adapter.add(LatestMessageRow(chatMessage))

            }

            //if a user messages us again we are notified onChildChanged function and follow the same logic as above
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                //called every time node(in databse) gets modified
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latestMeessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()

              //  adapter.add(LatestMessageRow(chatMessage))
            }



            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })//addChildEventListener

    }//listenForLatestMessages

    val adapter = GroupAdapter<ViewHolder>()


/*    private fun setupDummyRows(){

        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(LatestMessageRow())
        adapter.add(LatestMessageRow())

        recyclerview_latest_messages.adapter = adapter

    }//setupDummyRows*/

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
