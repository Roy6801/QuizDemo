package com.example.androquiz

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
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        loadUserActivity()
    }


    fun registerHandler(view: View) {
        val email = email.text.toString()
        val password = password.text.toString()

        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){task ->
            if (task.isSuccessful){
                val currentUser = mAuth!!.currentUser
                if (currentUser != null){
                    myRef.child("Users").child(splitString(email))
                }
                Toast.makeText(applicationContext, "Successful Registration! You can log in now!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "Failed Registration, Maybe You Already Have An Account?", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loginHandler(view: View) {
        val email = email.text.toString()
        val password = password.text.toString()
        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){task ->
            if (task.isSuccessful){
                Toast.makeText(applicationContext, "Successful Login!", Toast.LENGTH_SHORT).show()
                loadUserActivity()
            }else{
                Toast.makeText(applicationContext, "Failed Login!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loadUserActivity(){
        val currentUser = mAuth!!.currentUser
        val intent = Intent(this, UserActivity::class.java)
        if (currentUser != null){
            intent.putExtra("username", splitString(currentUser.email!!))
            startActivity(intent)
            finish()
        }
    }

    private fun splitString(str: String): String {
        val splitArray = str.split("@")
        return splitArray[0]
    }


}