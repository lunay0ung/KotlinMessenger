package com.example.KotlinMessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import timber.log.Timber
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private val SELECT_PHOTO = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        Timber.plant(Timber.DebugTree())


        register_button_register.setOnClickListener{
            performRegister()

        }//register

        already_have_an_account_text_view.setOnClickListener {
            Timber.d("Try to show Login Activity")
            Timber.d("나와?")

            startActivity(Intent(this, LoginActivity::class.java))
        }//already_have_an_account

        selectphoto_button_register.setOnClickListener {
            Timber.d("사진 추가")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, SELECT_PHOTO)

        }//select photo

    }//onCreate


    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null) {

            Timber.d("사진 선택됨")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)

        }//if

    }//onActivityResult

    private fun performRegister(){
        val username = username_edittext_register.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        Timber.d("email is $email")
        Timber.d("password is $password")

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일/비밀번호를 입력해주세요.",Toast.LENGTH_LONG).show()
            return
        }

        //Firebase Authentification to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) {
                    Timber.d("result : ${it.result}")
                    return@addOnCompleteListener
                }

                //else if successful
                Timber.d("유저 생성 with uid: ${it.result!!.user.uid}")

                uploadImageToFirebaseStorage()
            }

            .addOnFailureListener{
                Toast.makeText(this, "회원가입에 실패했습니다. 원인: ${it.message}", Toast.LENGTH_LONG).show()
                Timber.d("Failed to creat  user: {${it.message}")
            }


    }

    //유저가 업로드한 프로필 사진을 스토리지에 업로드한다
    private fun uploadImageToFirebaseStorage(){

        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Timber.d("사진 업로드 잘 됨: {${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Timber.d("File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                Timber.d("사진 업로드 실패: ${it.message}")
            }
    }//uploadImageToFirebaseStorage


    //가입한 유저 정보를 데이터베이스에 저장한다
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Timber.d("유저정보를 데이터베이스에 저장함: $it")

                //회원가입에 성공하면 메시지 확인 화면으로 이동  --이동하면서 기존에 쌓인 액티비티 스택을 없애거나 새로운 태스크를 생성한다
                startActivity(Intent(this, LatestMessagesActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)))

            }

            .addOnFailureListener {
                Timber.d("유저정보 데이터베이스 저장 실패: ${it.message}")
            }
    }//saveUserToFirebaseDatabase

}//RegisterActivity


class User(val uid: String, val username: String, val profileImageUrl: String) {

    //유저 클래스 생성자
    constructor() : this("", "", "")
}