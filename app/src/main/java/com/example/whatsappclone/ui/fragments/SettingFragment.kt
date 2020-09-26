package com.example.whatsappclone.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.whatsappclone.R
import com.example.whatsappclone.data.model.Users
import com.example.whatsappclone.ui.viewModel.SettingFragmentViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private val reqCode = 483
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChecker: String? = ""

    private val viewModel: SettingFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        viewModel.retrieveUserInfo().observe(viewLifecycleOwner, Observer {
            showUserInformation(it)
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

    private fun showUserInformation(user: Users) {
        if (context != null) {
            if (user.profile == "") {
                Picasso.get().load(R.drawable.ic_profile).into(profile_image_setting)
            } else {
                Picasso.get().load(user.profile).into(profile_image_setting)
            }
            if (user.cover == "") {
                Picasso.get().load(R.drawable.ic_profile).into(cover_image_setting)
            } else {
                Picasso.get().load(user.cover).into(cover_image_setting)
            }
            username_setting.text = user.username
        }

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
                        viewModel.child = url
                        viewModel.childrenName = "cover"
                        viewModel.updateChildren()
                    } else {
                        viewModel.child = url
                        viewModel.childrenName = "profile"
                        viewModel.updateChildren()
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
        builder.setPositiveButton("Create") { _, _ ->
            val str = editText.text.toString()
            if (str == "") {
                Toast.makeText(context, "please write something.....", Toast.LENGTH_LONG).show()
            } else {
                saveSocialLinkToDb(str)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun saveSocialLinkToDb(str: String) {
        when (socialChecker) {
            "facebook" -> {
                viewModel.child = "https://m.facebook.com/$str"
                viewModel.childrenName = "facebook"
                viewModel.updateChildren()
            }
            "instagram" -> {
                viewModel.child = "https://m.instagram.com/$str"
                viewModel.childrenName = "instagram"
                viewModel.updateChildren()

            }
            "website" -> {
                viewModel.child = "https://$str"
                viewModel.childrenName = "website"
                viewModel.updateChildren()

            }
        }
    }
}