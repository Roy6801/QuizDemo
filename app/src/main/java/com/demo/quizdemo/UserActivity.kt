package com.demo.quizdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user.*
import java.lang.Exception

class UserActivity : AppCompatActivity() {
    private  var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var myEmail: String ? = null
    var p1Ready: Boolean ? = null
    var p2Ready: Boolean ? = null
    var host: Boolean = false
    var numbers: MutableList<Int> ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(R.layout.activity_user)
        var b: Bundle? = intent.extras
        myEmail = b!!.getString("email")
        incomingCalls()
        val mainText = main_text
        mainText.text = myEmail
    }

    fun requestHandler(view: View) {
        val userEmail = main_email.text.toString()
        myRef.child("Users").child(splitString(userEmail)).child("Request").push().setValue(myEmail)
        myRef.child("Users").child(splitString(myEmail.toString())).child("Host").setValue(true)
        myRef.child("Users").child(splitString(myEmail.toString())).child("Ready").setValue(true)

    }
    fun acceptHandler(view: View) {
        val userEmail = main_email.text.toString()
        Log.d("ACCEPT HANDLER", "IN ACCESS")
        myRef.child("Users").child(splitString(myEmail.toString())).child("Request").push().setValue(userEmail)
        myRef.child("Users").child(splitString(myEmail.toString())).child("Host").setValue(false)
        myRef.child("Users").child(splitString(myEmail.toString())).child("Ready").setValue(true)
    }



    fun startHandler(view: View) {
        var p1ready = false
        var p2ready = false
        val userEmail = main_email.text.toString()
        myRef.child("Users").child(splitString(myEmail.toString())).child("Host").get().addOnSuccessListener { host ->
            if (host.value as Boolean){
                numbers = randomCreater()
            }

            myRef.child("Users").child(splitString(myEmail.toString())).child("Ready").get().addOnSuccessListener {
                p1ready = it.value as Boolean
                Log.d("START P1", p1ready.toString())
            }
            myRef.child("Users").child(splitString(userEmail)).child("Ready").get().addOnSuccessListener {
                p2ready = it.value as Boolean
                Log.d("START P2", p2ready.toString())
            }

            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }

    private fun splitString(str: String): String{
        val splitString = str.split("@")
        return splitString[0]
    }

    private fun incomingCalls(){
        myRef.child("Users").child(splitString(myEmail.toString())).child("Request")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            var td = snapshot.value as HashMap<String, Any>
                            if (td != null){
                                val value: String
                                for (key in td.keys){
                                    value = td[key] as String
                                    main_email.setText(value)
                                    myRef.child("Users").child(splitString(myEmail.toString())).child("Request").setValue(true)
                                    break
                                }
                            }
                        }catch (ex: Exception){

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }

    private fun randomCreater(): MutableList<Int>{
        val numbers = mutableListOf<Int>()
        for (i in 1..50){
            val num = (1..10).random()
            if (numbers.contains(num)){
                continue
            }
            else{
                numbers.add(num)
            }

            if (numbers.size == 5){
                break
            }
        }
        return numbers
    }


}