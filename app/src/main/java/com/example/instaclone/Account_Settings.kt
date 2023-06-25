package com.example.instaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.instaclone.Modal.User
import com.example.instaclone.databinding.ActivityAccountSettingsBinding
import com.example.instaclone.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class Account_Settings : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        binding.logoutbtn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            val intent=Intent(this@Account_Settings, SigninActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }
        binding.changeImageTextBtn.setOnClickListener {
            checker = "clicked"
//            CropImage.activity()
//                .setAspectRatio(1, 1)
//                .start(this@Account_Settings)
//
        }


        binding.saveinforprofilebtn.setOnClickListener {
            if (checker == "clicked")
            {
//                uploadImageAndUpdateInfo()
            }
            else
            {
                updateUserInfoOnly()
            }
        }


        userInfo()
    }

    private fun userInfo() {
        val usersRef =
            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(binding.profileimageviewprofilefrag)
                    binding.usernameprofilefrag.setText(user!!.getUsername())
                    binding.fullnameprofilefrag.setText(user!!.getFullname())
                    binding.bioprofilefrag.setText(user!!.getBio())
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun updateUserInfoOnly()
    {
        when {
            TextUtils.isEmpty(binding.fullnameprofilefrag.text.toString()) -> Toast.makeText(this, "Please write full name first.", Toast.LENGTH_LONG).show()
            binding.usernameprofilefrag.text.toString() == "" -> Toast.makeText(this, "Please write user name first.", Toast.LENGTH_LONG).show()
            binding.bioprofilefrag.text.toString() == "" -> Toast.makeText(this, "Please write your bio first.", Toast.LENGTH_LONG).show()
            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = binding.fullnameprofilefrag.text.toString().toLowerCase()
                userMap["username"] = binding.usernameprofilefrag.text.toString().toLowerCase()
                userMap["bio"] = binding.bioprofilefrag.text.toString().toLowerCase()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account Information has been updated successfully.", Toast.LENGTH_LONG).show()

                val intent = Intent(this@Account_Settings, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}