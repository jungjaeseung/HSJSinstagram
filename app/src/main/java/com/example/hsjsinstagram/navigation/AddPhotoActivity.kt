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
import bolts.Task
import com.example.hsjsinstagram.R
import com.example.hsjsinstagram.navigation.Model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    lateinit var storage: FirebaseStorage
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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


        //Promise method
        storagePath.putFile(photoUri!!).continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storagePath.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

                //Insert downloadUrl of image
                contentDTO.imageUrl = uri.toString()
                //Insert uid of user
                contentDTO.uid = auth.currentUser?.uid
                //Insert userId
                contentDTO.userId = auth.currentUser?.email
                //Insert explain of content
                contentDTO.explain = addPhoto_edit_explain.text.toString()
                //Insert timestamp
                contentDTO.timestamp = System.currentTimeMillis()

                firestore.collection("images")?.document()?.set(contentDTO)

                setResult(Activity.RESULT_OK)

                finish()
        }

//        //Callback method
//        storagePath.putFile(photoUri!!).addOnSuccessListener {
//            Toast.makeText(this,getString(R.string.upload_success), Toast.LENGTH_LONG).show()
//            storagePath.downloadUrl.addOnSuccessListener { uri->
//                var contentDTO = ContentDTO()
//
//                //Insert downloadUrl of image
//                contentDTO.imageUrl = uri.toString()
//                //Insert uid of user
//                contentDTO.uid = auth.currentUser?.uid
//                //Insert userId
//                contentDTO.userId = auth.currentUser?.email
//                //Insert explain of content
//                contentDTO.explain = addPhoto_edit_explain.text.toString()
//                //Insert timestamp
//                contentDTO.timestamp = System.currentTimeMillis()
//
//                firestore.collection("images")?.document()?.set(contentDTO)
//
//                setResult(Activity.RESULT_OK)
//
//                finish()
//            }
//        }
    }
    var photoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        photoUri = result.data?.data
        addPhoto_image.setImageURI(photoUri)
    }


}