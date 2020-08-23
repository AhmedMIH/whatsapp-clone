package com.example.whatsappclone.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.whatsappclone.R
import com.example.whatsappclone.modelClass.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {

    var userRef: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val reqCode = 483
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChecker: String? = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        userRef!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: Users? = p0.getValue(Users::class.java)
                    if (context != null) {
//                        view.username_setting.text = user!!.username
//                        Picasso.get().load(user.profile).into(view.profile_image_setting)
//                        Picasso.get().load(user.cover).into(view.cover_image_setting)
                    }

                }
            }

        })

        view.profile_image_setting.setOnClickListener {
            pickImage()
        }

        view.cover_image_setting.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        view.set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }

        view.set_website.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }

        view.set_instagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }


        return view
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, reqCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "uploading.....", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, pleas wait....")
        progressBar.show()
        if (imageUri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    if (coverChecker == "cover") {
                        val mapCoverImage = HashMap<String, Any>()
                        mapCoverImage["cover"] = url
                        userRef!!.updateChildren(mapCoverImage)
                        coverChecker = ""
                    } else {
                        val mapProfileImage = HashMap<String, Any>()
                        mapProfileImage["profile"] = url
                        userRef!!.updateChildren(mapProfileImage)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }

    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert)

        if (socialChecker == "website") {
            builder.setTitle("Write Url : ")
        } else {
            builder.setTitle("Write username : ")
        }
        val editText = EditText(context)

        if (socialChecker == "website") {
            editText.hint = "e.g www.google.com"
        } else {
            editText.hint = "e.g ahmed21"
        }
        builder.setView(editText)
        builder.setPositiveButton("Create") { dialog, which ->
            val str = editText.text.toString()
            if (str == "") {
                Toast.makeText(context, "please write something.....", Toast.LENGTH_LONG).show()
            } else {
                saveSocialLink(str)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocialUrl = HashMap<String, Any>()

        when (socialChecker) {
            "facebook" -> {
                mapSocialUrl["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram" -> {
                mapSocialUrl["instagram"] = "https://m.instagram.com/$str"
            }
            "website" -> {
                mapSocialUrl["website"] = "https://$str"
            }
        }
        userRef!!.updateChildren(mapSocialUrl).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"updated successfully",Toast.LENGTH_SHORT).show()
            }
        }
    }

}