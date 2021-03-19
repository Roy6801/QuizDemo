package com.demo.quizdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    lateinit var  wonTxt: TextView
    lateinit var  lostTxt: TextView
    lateinit var  tieTxt: TextView
    lateinit var  p1Score: TextView
    lateinit var  p2Score: TextView
    lateinit var  backBtn: Button
    var p1: Int = 0
    var p2: Int = 0
    var host: Boolean ? = null
    lateinit var roomName: String
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(R.layout.activity_result)
        val b: Bundle ? = intent.extras
        host = b!!.getBoolean("host")
        roomName = b.getString("roomName").toString()
        wonTxt = won
        lostTxt = lost
        tieTxt = tie
        p1Score = p1_score
        p2Score = p2_score
        myRef.child("Match").child(roomName).child("p1Score").get().addOnSuccessListener {
            p1 = it.value.toString().toInt()
        }
        myRef.child("Match").child(roomName).child("p2Score").get().addOnSuccessListener {
            p2 = it.value.toString().toInt()
        }
        Handler().postDelayed({
            if (host == true){
                if (p1 > p2){
                    wonTxt.visibility = View.VISIBLE
                }
                else if(p1 < p2){
                    lostTxt.visibility = View.VISIBLE
                }
                else{
                    tieTxt.visibility = View.VISIBLE
                }
                p1Score.text = "Your Score: $p1"
                p2Score.text = "Opponent's Score: $p2"
            }else{
                if (p2 > p1){
                    wonTxt.visibility = View.VISIBLE
                }
                else if(p2 < p1){
                    lostTxt.visibility = View.VISIBLE
                }
                else{
                    tieTxt.visibility = View.VISIBLE
                }
                p1Score.text = "Your Score: $p2"
                p2Score.text = "Opponent's Score: $p1"
            }
        }, 500)
    }

    fun backHandler(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}