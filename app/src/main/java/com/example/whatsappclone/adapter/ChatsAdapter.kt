package com.example.whatsappclone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.ViewFullImageActivity
import com.example.whatsappclone.modelClass.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(val mContext: Context, val mChatList: List<Chat>, val imageUrl: String) :
    RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        return if (viewType == 1) {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.massage_item_right, parent, false)
            ChatsViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.massage_item_left, parent, false)
            ChatsViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mChatList[position].sender == firebaseUser.uid) {
            1
        } else {
            0
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val chat: Chat = mChatList[position]

        if(imageUrl == ""){
            Picasso.get().load(R.drawable.ic_profile).into(holder.profileImage)
        }
        else {
            Picasso.get().load(imageUrl).into(holder.profileImage)
        }

        //image massage
        if (chat.massage == "sent you an image" && chat.url != "") {
            if (chat.sender == firebaseUser.uid) {
                holder.textMassage!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.right_image_view)
                holder.right_image_view!!.setOnClickListener {
                    val option = arrayOf<CharSequence>(
                        "View Full Image",
                        "Delete Image",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                    builder.setTitle("what do you want ?")
                    builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
                        if(which == 0){
                            val intent = Intent(mContext,ViewFullImageActivity::class.java)
                            intent.putExtra("image_url",chat.url)
                            mContext.startActivity(intent)
                        }
                        if (which == 1 ){
                            deleteMassage(position,holder)
                        }
                        if (which == 2){
                            dialog.cancel()
                        }
                    })
                    builder.show()
                }
            } else if (chat.sender != firebaseUser.uid) {
                holder.textMassage!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.left_image_view)
                holder.left_image_view!!.setOnClickListener {
                    val option = arrayOf<CharSequence>(
                        "View Full Image",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                    builder.setTitle("what do you want ?")
                    builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
                        if(which == 0){
                            val intent = Intent(mContext,ViewFullImageActivity::class.java)
                            intent.putExtra("image_url",chat.url)
                            mContext.startActivity(intent)
                        }
                        if (which == 1){
                            dialog.cancel()
                        }
                    })
                    builder.show()
                }
            }
        }
        //text massage
        else {
            holder.textMassage!!.text = chat.massage
            if(chat.sender == firebaseUser.uid){
                holder.textMassage!!.setOnClickListener {
                    val option = arrayOf<CharSequence>(
                        "Delete massage",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("what do you want ?")
                    builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0 ){
                            deleteMassage(position,holder)
                        }
                        if (which == 1){
                            dialog.cancel()
                        }
                    })
                    builder.show()
                }
            }
        }


        if (position == mChatList.size - 1) {
            if (chat.isSeen == true) {
                holder.text_seen!!.text = "seen"
                if (chat.massage == "sent you an image" && chat.url != "") {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            } else {
                holder.text_seen!!.text = "sent"
                if (chat.massage == "sent you an image" && chat.url != "") {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            }


        } else {
            holder.text_seen!!.visibility = View.GONE
        }
    }

    private fun deleteMassage(position: Int,holder: ChatsViewHolder) {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).massageID!!)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(holder.itemView.context, "Deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Failed, Not Deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    inner class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView? = null
        var textMassage: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null

        init {
            profileImage = itemView.findViewById(R.id.profile_image_massage_item)
            textMassage = itemView.findViewById(R.id.text_massage_item)
            left_image_view = itemView.findViewById(R.id.image_massage_item_left)
            right_image_view = itemView.findViewById(R.id.image_massage_item_right)
            text_seen = itemView.findViewById(R.id.text_seen_item)
        }
    }

}