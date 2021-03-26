package com.example.androquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {

    lateinit var buttonA: Button
    lateinit var buttonB: Button
    lateinit var buttonC: Button
    lateinit var buttonD: Button
    lateinit var questionTxt: TextView
    lateinit var timerTxt: TextView
    lateinit var countDownTimer: CountDownTimer
    private var host: Boolean? = null
    private var myUserName: String? = null
    private var opponentUserName: String? = null
    private var questionsIndex: MutableList<Int>? = null
    var total = 0
    var score = 0
    var changeQuestion = 0
    lateinit var roomName: String
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(R.layout.activity_quiz)
        val b: Bundle? = intent.extras
        host = b!!.getBoolean("host")
        myUserName = b.getString("myUserName")
        opponentUserName = b.getString("opponentUserName")
        buttonA = optionA
        buttonB = optionB
        buttonC = optionC
        buttonD = optionD
        questionTxt = question
        timerTxt = timer
        total = 0
        score = 0
        changeQuestion = 0

    }

    override fun onStart() {
        super.onStart()
        Log.d("TOTAL AFTER MAIN RESET", total.toString())

        if (host == true) {
            roomName = myUserName + opponentUserName
            myRef.child("Match").child(roomName).child("p1").setValue(changeQuestion)
            myRef.child("Match").child(roomName).child("p1Score").setValue(score)
        } else {
            roomName = opponentUserName + myUserName
            myRef.child("Match").child(roomName).child("p2").setValue(changeQuestion)
            myRef.child("Match").child(roomName).child("p2Score").setValue(score)
        }

        myRef.child("Match").child(roomName).child("Question").get().addOnSuccessListener {
            questionsIndex = it.value as MutableList<Int>
        }

        reverseTimer(4, timerTxt)
        Handler().postDelayed({
            if (host == true) {
                myRef.child("Match").child(roomName).child("p2").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        countDownTimer.cancel()
                        total++
                        if (total > 5){
                            myRef.removeEventListener(this)
                        }
                        updateQuestion()

                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            } else {
                myRef.child("Match").child(roomName).child("p1").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        countDownTimer.cancel()
                        total++
                        if (total > 5){
                            myRef.removeEventListener(this)
                        }
                        updateQuestion()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }, 1000)
    }

    private fun updateQuestion() {
        Log.d("TOTAL IN UPDATE", total.toString())
        if (total > 5) {
            if (host == true) {
                myRef.child("Match").child(roomName).child("p1Score").setValue(score)
            } else {
                myRef.child("Match").child(roomName).child("p2Score").setValue(score)
            }
            Handler().postDelayed({
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("roomName", roomName)
                intent.putExtra("host", host)
                startActivity(intent)
                Handler().postDelayed({
                    finish()
                }, 300)
            }, 1000)
        } else {
            reverseTimer(15, timerTxt)
            val reference = myRef.child("Questions").child((questionsIndex!![total - 1].toString()))
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val map = snapshot.value
                    if (map is Map<*, *>) {
                        questionTxt.text = map["question"].toString()
                        buttonA.text = map["option1"].toString()
                        buttonB.text = map["option2"].toString()
                        buttonC.text = map["option3"].toString()
                        buttonD.text = map["option4"].toString()
                        val answer = map["answer"].toString()


                        buttonA.setOnClickListener {
                            if (buttonA.text.toString() == answer) {
                                score++
                                total++
                                changeQuestion++
                                if (host == true) {
                                    myRef.child("Match").child(roomName).child("p1").setValue(changeQuestion)
                                } else {
                                    myRef.child("Match").child(roomName).child("p2").setValue(changeQuestion)
                                }
                                countDownTimer.cancel()
                                updateQuestion()
                            } else {
                                total++
                                changeQuestion++
                                if (host == true) {
                                    myRef.child("Match").child(roomName).child("p1").setValue(changeQuestion)
                                } else {
                                    myRef.child("Match").child(roomName).child("p2").setValue(changeQuestion)
                                }
                                countDownTimer.cancel()
                                updateQuestion()
                            }
                        }
                        buttonB.setOnClickListener {
                            if (buttonB.text.toString() == answer) {
                                score++
                                total++
                                changeQuestion++
                                if (host == true) {
                                    myRef.child("Match").child(roomName).child("p1").setValue(changeQuestion)
                                } else {
                                    myRef.child("Match").child(roomName).child("p2").setValue(changeQuestion)
                                }
                                countDownTimer.cancel()
                                updateQuestion()
                            } else {
                                total++
                                changeQuestion++
                                if (host == true) {
                                    myRef.child("Match").child(roomName).child("p1").setValue(changeQuestion)
                                } else {
                                    myRef.child("Match").child(roomName).child("p2").setValue(changeQuestion)
                                }
                                countDownTimer.cancel()
                                updateQuestion()
                            }
                        }
                        buttonC.setOnClickListener {
                            if (buttonC.text.toString() == answer) {
                                score++
                                total++
                                changeQuestion++
                                if (host == true) {
                                    myRef.child("Match").child(roomName).child("p1").setValue(changeQuestion)
                                } else {
                                    myRef.child("Match").child(roomName).child("p2").setValue(changeQuestion)
                                }
                                countDownTimer.cancel()
                                updateQuestion()
                            } else {
                                total++
                                changeQuestion++
                                if (host == true) {
                                    myRef.child("Match").child(roomName).child("p1").setValue(changeQuestion)
                                } else {
                                    myRef.child("Match").child(roomName).child("p2").setValue(changeQuestion)
                                }
                                countDownTimer.cancel()
                                updateQuestion()
                            }
                        }
                        buttonD.setOnClickListener {
                            if (buttonD.text.toString() == answer) {
                                score++
                                total++
                                changeQuestion++
                                if (host == true) {
                                    myRef.child("Match").child(roomName).child("p1").setValue(changeQuestion)
                                } else {
                                    myRef.child("Match").child(roomName).child("p2").setValue(changeQuestion)
                                }
                                countDownTimer.cancel()
                                updateQuestion()
                            } else {
                                total++
                                changeQuestion++
                                if (host == true) {
                                    myRef.child("Match").child(roomName).child("p1").setValue(changeQuestion)
                                } else {
                                    myRef.child("Match").child(roomName).child("p2").setValue(changeQuestion)
                                }
                                countDownTimer.cancel()
                                updateQuestion()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }



    private fun reverseTimer(seconds: Int, tv: TextView) {
        countDownTimer = object : CountDownTimer((seconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var second = millisUntilFinished.toInt() / 1000
                second %= 60
                tv.text = "00:$second"
            }

            override fun onFinish() {
                cancel()
                total++
                updateQuestion()
            }
        }
        countDownTimer.start()
    }
}