package com.example.hsjsinstagram

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    lateinit var googleSignInClient : GoogleSignInClient
    lateinit var callbackManager : CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            signInAndSignup()
        }
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        /*
        업데이트 해야됨
        facebook_sign_in_button.setOnClickListener {
            facebookLogin()
        }*/
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //printHashKey()
        callbackManager = CallbackManager.Factory.create()
    }
    //tD0iloCc+9kfj5ZTqNg7QbC+57A=
    /*private fun printHashKey() {
        try {
            val info: PackageInfo = getPackageManager()
                .getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }*/
    private fun googleLogin(){
        var signInIntent = googleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result: ActivityResult ->
        if(result.resultCode == RESULT_OK){
            val intent: Intent = result.data!!
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try{
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            }catch (e: ApiException){
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    fun facebookLogin(){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

            }
        })
    }
    fun handleFacebookAccessToken(token: AccessToken){
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener {
            task->
            if(task.isSuccessful){
                moveMainPage(auth.currentUser)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

    }
    private fun firebaseAuthWithGoogle(account : GoogleSignInAccount){
        var credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                task->
            if(task.isSuccessful){
                //로그인
                moveMainPage(task.result.user)
            }
        }
    }
    private fun signInAndSignup(){
        auth.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            .addOnCompleteListener {
                task->
                if(task.isSuccessful){
                    //계정 생성
                    moveMainPage(task.result.user)
                }else{
                    //계정 있으면 로그인
                    signInEmail()
                }
            }
    }
    private fun signInEmail(){
        auth.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            .addOnCompleteListener {
                    task->
                if(task.isSuccessful){
                    //로그인
                    moveMainPage(task.result.user)
                }
            }
    }
    private fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}