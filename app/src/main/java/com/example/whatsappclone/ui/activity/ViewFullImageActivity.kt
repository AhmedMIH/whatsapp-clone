package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ViewFullImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)
        val imageUrl = intent.getStringExtra("image_url")
        val image_viewer =findViewById<ImageView>(R.id.image_viewer)
        Picasso.get().load(imageUrl).into(image_viewer)
    }
}