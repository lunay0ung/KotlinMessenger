package com.example.KotlinMessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import timber.log.Timber

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)


        Timber.plant(Timber.DebugTree())

        supportActionBar?.title = "Select User"

        //groupie라는 라이브러리를 통해 리사이클러뷰 어댑터 생성을 생략할 수 있다
   //     val adapter = GroupAdapter<ViewHolder>()


   //     recyclerview_newmessage.adapter = adapter

        fetchUsers()
    }//onCreate


    private fun fetchUsers(){

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                //p0 is a datasnapshot that contains all of the data


                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {

                    val user = it.getValue(User::class.java)
                    if(user != null) {
                        adapter.add(UserItem(user))
                    }
                    recyclerview_newmessage.adapter = adapter
                }//forEach

            }//onDataChange

            override fun onCancelled(p0: DatabaseError) {


            }//onCancelled
        })//addListenerForSingleValueEvent

    }//fetchUsers

}//NewMessageActivity




class UserItem(val user:User) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //will be called in out list for each user object later on

        Timber.d("username: {$user.username}")
        //데이터베이스에서 유저 정보를 가져온 후 뷰에 bind한다
        viewHolder.itemView.username_textview_new_message.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)

   }//bind

    override fun getLayout(): Int {
       return R.layout.user_row_new_message
    }//getLayout
}//UserItem class
