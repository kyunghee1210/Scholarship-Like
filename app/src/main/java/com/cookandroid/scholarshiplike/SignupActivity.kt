package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.FragmentLoginTermsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_profile_logout.*

class SignupActivity :AppCompatActivity() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    val auth = Firebase.auth
    val db = Firebase.firestore

    lateinit var txtEmail:String
    lateinit var txtPassword:String
    lateinit var txtPasswordConfirm:String
    lateinit var txtNickname:String
    lateinit var txtUniv:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //버튼 클릭을 통합 처리
        btnClick()
    }

    // 버튼 클릭 통합 처리
    fun btnClick(){
        var isExistBlank = false
        var isPWSame = false

        //회원가입 버튼을 클릭하였을 때
        btn_signup.setOnClickListener() {
            txtEmail = txt_email.text.toString()
            txtPassword = txt_password.text.toString()
            txtPasswordConfirm = txt_password_confirm.text.toString()
            txtNickname = txt_nickname.text.toString()
            txtUniv = txt_univ.text.toString()

            isExistBlank = txtEmail.isEmpty() || txtPassword.isEmpty() || txtPasswordConfirm.isEmpty() || txtNickname.isEmpty() || txtUniv.isEmpty()
            isPWSame = txtPassword == txtPasswordConfirm

            if (isExistBlank) {
                Toast.makeText(this, "빈 항목이 있습니다", Toast.LENGTH_SHORT).show()
            }
            else if (!isPWSame) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }

            // 조건 만족시, 아이디 생성 & 유저 DB 업데이트
            if(!isExistBlank && isPWSame) {
                createEmailId(txtEmail, txtPassword)    // 유저 계정 만들기
            }
        }

        // 돌아가기 버튼 클릭 시
        btn_goto_back.setOnClickListener() {
            finish()    // 현재 액티비티 제거
        }
    }

    // 아이디 생성
    private fun createEmailId(email : String, pw : String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                    updateUserDB()  // 유저 DB 저장
                }
                else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }

    // 유저 DB 저장
    fun updateUserDB() {
        val uId = auth.currentUser?.uid
        val userProfileSet = hashMapOf(
            "닉네임" to txtNickname,
            "소속 대학교" to txtUniv
        )

        db.collection("Users").document(uId!!)
            .set(userProfileSet)
            .addOnSuccessListener { // 성공 시 MainActivity로 이동
                Log.d(TAG, "User profile DB successfully written!")
                val iT = Intent(this, MainActivity::class.java)
                startActivity(iT)
                finish()
            }
            .addOnFailureListener {e ->
                Log.w(TAG, "Error writting user DB!", e)
            }
    }
}