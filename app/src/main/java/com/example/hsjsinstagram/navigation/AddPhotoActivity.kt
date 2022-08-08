package com.example.hsjsinstagram.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputBinding
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.hsjsinstagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    lateinit var storage: FirebaseStorage
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Initiate storage
        storage = FirebaseStorage.getInstance()

        //앨범 띄우기
        var i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        photoResult.launch(i)
        addPhoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }
    fun contentUpload(){
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMAGE_" + timestamp + "_.png"

        var storagePath = storage.reference.child("images").child(imageFileName)

        storagePath.putFile(photoUri!!).addOnSuccessListener {
            Toast.makeText(this,getString(R.string.upload_success), Toast.LENGTH_LONG).show()
        }
    }
    var photoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        photoUri = result.data?.data
        addPhoto_image.setImageURI(photoUri)
    }


}