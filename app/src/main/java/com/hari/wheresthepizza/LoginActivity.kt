package com.hari.wheresthepizza

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun loginClick(v: View) {
        if (!isFormValid()) {
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnSuccessListener {
                startActivity(Intent().setClass(this@LoginActivity, FeedActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_LONG).show()
            }
    }

    fun registerClick(v: View) {
        if (!isFormValid()) {
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnSuccessListener {
                Toast.makeText(this@LoginActivity, "Registration OK, please log in.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_LONG).show()
            }
    }

    fun isFormValid() : Boolean {
        return when {
            etEmail.text!!.isEmpty() -> {
                etEmail.error = "Email ID cannot be empty"
                false
            }

            etPassword.text!!.isEmpty() -> {
                etPassword.error = "Password cannot be empty"
                false
            }

            else -> true
        }
    }

}
