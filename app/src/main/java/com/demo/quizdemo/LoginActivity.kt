package com.demo.quizdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private  var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
    }

    fun registerHandler(view: View) {
        val email = email.text.toString()
        val password = password.text.toString()
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        val currentUser = mAuth!!.currentUser
                        if (currentUser != null){
                            myRef.child("Users").child(splitString(currentUser.email.toString())).child("Request").setValue(currentUser.uid)
                        }

                        Toast.makeText(
                                applicationContext,
                                "Successful registration, please login now!",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                    else{
                        Toast.makeText(
                                applicationContext,
                                "Failed registration, maybe you already have an account!",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }

    }
    fun loginHandler(view: View) {
        val email = email.text.toString()
        val password = password.text.toString()
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_LONG).show()
                        loadMain()
                    }
                    else{
                        Toast.makeText(
                                applicationContext,
                                "Failed login, check password or create new account",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }
    }

    override fun onStart() {
        super.onStart()
        loadMain()
    }

    private fun loadMain(){
        val currentUser = mAuth!!.currentUser
        val intent = Intent(this, UserActivity::class.java)
        // checking if user is not null
        if (currentUser != null) {
            // storing to database

            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)

            startActivity(intent)
        }
    }

    private fun splitString(str: String): String{
        val splitString = str.split("@")
        return splitString[0]
    }
}