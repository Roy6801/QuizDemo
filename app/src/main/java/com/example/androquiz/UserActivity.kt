package com.example.androquiz

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
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var myUserName: String? = null
    var req: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(R.layout.activity_user)
        val b: Bundle? = intent.extras
        myUserName = b!!.getString("username")
        val mainText = main_text
        mainText.text = myUserName

        val accText = main_ac_username
        val request = request_btn
        val accept = accept_btn

        myRef.child("Users").child(myUserName!!).child("Ready").setValue(false)
        myRef.child("Users").child(myUserName!!).child("Request").setValue("")
        myRef.child("Users").child(myUserName!!).child("Host").setValue(false)

        val intent = Intent(this, QuizActivity::class.java)

        myRef.child("Users").child(myUserName!!).child("Request").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                accText.text = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        request.setOnClickListener {
            val opponentUserName = main_rq_username.text.toString()
            myRef.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        if (i.key.toString() == opponentUserName) {
                            val numbers = randomNumCreator()
                            val roomName = myUserName + opponentUserName
                            myRef.child("Match").child(roomName).child("Question").setValue(numbers)
                            myRef.child("Users").child(myUserName!!).child("Ready").setValue(true)
                            myRef.child("Users").child(myUserName!!).child("Host").setValue(true)
                            myRef.child("Users").child(opponentUserName).child("Request").setValue(myUserName)
                            req = true
                        }
                    }
                    if (req) {
                        Toast.makeText(applicationContext, "Request Sent", Toast.LENGTH_SHORT).show()
                        matchStart(opponentUserName, intent)
                    } else {
                        Toast.makeText(applicationContext, "Request Failed", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        accept.setOnClickListener {
            val opponentUserName = accText.text.toString()
            if (opponentUserName != "") {
                myRef.child("Users").child(myUserName!!).child("Ready").setValue(true)
                intent.putExtra("myUserName", myUserName)
                intent.putExtra("opponentUserName", opponentUserName)
                intent.putExtra("host", false)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun matchStart(opponentUserName: String, it: Intent) {
        myRef.child("Users").child(opponentUserName).child("Ready").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value as Boolean) {
                    Toast.makeText(applicationContext, "Request Accepted", Toast.LENGTH_SHORT).show()
                    it.putExtra("myUserName", myUserName)
                    it.putExtra("opponentUserName", opponentUserName)
                    it.putExtra("host", true)
                    startActivity(it)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun randomNumCreator(): MutableList<Int> {
        val numbers = mutableListOf<Int>()
        for (i in 1..50) {
            val num = (1..10).random()
            if (numbers.contains(num)) {
                continue
            } else {
                numbers.add(num)
            }

            if (numbers.size == 5) {
                break
            }
        }
        return numbers
    }
}