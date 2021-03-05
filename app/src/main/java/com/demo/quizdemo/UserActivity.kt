package com.demo.quizdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {
    private  var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var myEmail: String ? = null
    var req : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(R.layout.activity_user)
        val b: Bundle? = intent.extras
        myEmail = b!!.getString("email")
        val mainText = main_text
        mainText.text = myEmail

        val accText = main_ac_email
        val request : View = findViewById(R.id.request_btn)
        val accept : View = findViewById(R.id.accept_btn)


        val intent = Intent(this, QuizActivity::class.java)

        myRef.child("Users").child(splitString(myEmail.toString())).child("Ready").setValue(false)
        myRef.child("Users").child(splitString(myEmail.toString())).child("Request").setValue("")
        myRef.child("Users").child(splitString(myEmail.toString())).child("Host").setValue(false)

        myRef.child("Users").child(splitString(myEmail.toString())).child("Request").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                accText.text = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        request.setOnClickListener(){
            val userEmail = main_rq_email.text.toString()
            myRef.child("Users").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(i in snapshot.children)
                    {
                        if(i.key.toString() == splitString(userEmail))
                        {
                            myRef.child("Users").child(splitString(myEmail.toString())).child("Ready").setValue(true)
                            myRef.child("Users").child(splitString(userEmail)).child("Request").setValue(splitString(myEmail.toString()))
                            req = true
                        }
                    }
                    if(req){
                        Toast.makeText(applicationContext, "Request Sent", Toast.LENGTH_SHORT).show()
                        matchStart(userEmail, intent)
                    }
                    else{
                        Toast.makeText(applicationContext, "Request Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        accept.setOnClickListener(){
            val userEmail = accText.text.toString()
            if(userEmail != "")
            {
                myRef.child("Users").child(splitString(myEmail.toString())).child("Ready").setValue(true)
                startActivity(intent)
            }
        }
    }

    private  fun matchStart(userEmail : String, it : Intent){
        myRef.child("Users").child(splitString(userEmail)).child("Ready").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value as Boolean)
                {
                    Toast.makeText(applicationContext, "Request Accepted", Toast.LENGTH_SHORT).show()
                    startActivity(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun splitString(str: String): String{
        val splitString = str.split("@")
        return splitString[0]
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