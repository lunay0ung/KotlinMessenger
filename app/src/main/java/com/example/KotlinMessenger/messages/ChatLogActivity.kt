package com.example.KotlinMessenger.messages

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.KotlinMessenger.R
import com.example.KotlinMessenger.models.ChatMessage
import com.example.KotlinMessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import timber.log.Timber

class ChatLogActivity : AppCompatActivity(){

    //Timber을 쓰기 때문에 TAG선언은 필요 없지만 참고코드로 보관
    /*companion object {
        val TAG = "ChatLoog"
    }
*/

    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        Timber.plant(Timber.DebugTree())


        recyclerview_chat_log.adapter = adapter


        //val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) //NewMessageActivity에서 유저정보를 통째로 받아옴
        //val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) //NewMessageActivity에서 유저정보를 통째로 받아옴

        supportActionBar?.title = toUser?.username //액션바에 유저네임을 띄운다

        //setupDummyData()

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Timber.d("Attempt to send message")
            performSendMessage()
        }

    }//onCreate

    private fun listenForMessages(){
       // val reference = FirebaseDatabase.getInstance().getReference("/messages")

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")


        //notify us every piece of data that belongs to 'messages' database
        reference.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage =  p0.getValue(ChatMessage:: class.java) //모든 메시지를 클래스객체로 저장했기 때문에 불러오는 것도 간단하다

                if(chatMessage != null) {

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?:return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    }
                    else {
                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                    Timber.d(chatMessage.text)
                }

            }//onChildAdded

            override fun onCancelled(p0: DatabaseError) {
            }//onCancelled

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }//listenForMessages

    private fun performSendMessage() {
        val text = edittext_chat_log.text.toString()  //유저가 채팅창에 입력한 것을 받아서
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY) //NewMessageActivity에서 유저정보를 통째로 받아옴
        val toId = user.uid

        if(fromId == null) return

      //  val reference = FirebaseDatabase.getInstance().getReference("/messages").push() //파이어베이스를 통해 메시지를 보냄
        //메시지 저장 노드를 세분화
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push() //파이어베이스를 통해 메시지를 보냄
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()


        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Timber.d("saved our message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }
            .addOnFailureListener {
                Timber.d("failed saved our message: ${reference.key}")
                toast("Failed to send your message")
            }

        toReference.setValue(chatMessage)


        //help keep track of latest messages between current logged in user and the counter part
        val latestMessageReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageReference.setValue(chatMessage)

        val latestMessageToReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToReference.setValue(chatMessage)


    }//performSendMessage

    //더미 채팅데이터 삽입
/*    private fun setupDummyData(){
        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(ChatFromItem("From"))
        adapter.add(ChatToItem("to"))

        recyclerview_chat_log.adapter = adapter
    }//setupDummyData*/

    //toast
    fun Context.toast(resourceId: Int) = toast(getString(resourceId))
    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

}//ChatLogActivity

class ChatFromItem(val text:String, val user: User) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textview_chat_from_row.text = text

        //load our user image into the star
        val uri = user.profileImageUrl
        Timber.d("currentUser's profileImageUrl $uri")
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetImageView)

        }//bind

    override fun getLayout(): Int {

        return R.layout.chat_from_row

       }//getLayout
}//ChatFromItem

class ChatToItem(val text:String, val user:User) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_chat_to_row.text = text

        //load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)

    }//bind

    override fun getLayout(): Int {

        return R.layout.chat_to_row

    }//getLayout
}//ChatToItem