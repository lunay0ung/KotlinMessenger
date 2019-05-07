package com.example.KotlinMessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String) : Parcelable {

    //유저 클래스 생성자
    constructor() : this("", "", "")


}