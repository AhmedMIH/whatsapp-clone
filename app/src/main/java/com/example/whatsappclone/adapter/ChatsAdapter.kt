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
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.ui.ClickListener
import com.example.whatsappclone.ui.activity.ViewFullImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ChatsAdapter(
    val mContext: Context,
    val mChatList: List<Chat>,
    val imageUrl: String,
    val listener: ClickListener
) :
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
        holder.onBind(chat)
    }

    inner class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage = itemView.findViewById<ImageView>(R.id.profile_image_massage_item)
        var textMassage = itemView.findViewById<TextView>(R.id.text_massage_item)
        var left_image_view = itemView.findViewById<ImageView>(R.id.image_massage_item_left)
        var right_image_view = itemView.findViewById<ImageView>(R.id.image_massage_item_right)
        var text_seen = itemView.findViewById<TextView>(R.id.text_seen_item)

        fun onBind(chat: Chat) {
            if (imageUrl == "") {
                Picasso.get().load(R.drawable.ic_profile).into(profileImage)
            } else {
                Picasso.get().load(imageUrl).into(profileImage)
            }

            //image massage
            if (chat.massage == "sent you an image" && chat.url != "") {
                if (chat.sender == firebaseUser.uid) {
                    textMassage!!.visibility = View.GONE
                    right_image_view!!.visibility = View.VISIBLE
                    Picasso.get().load(chat.url).into(right_image_view)
                    right_image_view!!.setOnClickListener {
                        val option = arrayOf<CharSequence>(
                            "View Full Image",
                            "Delete Image",
                            "Cancel"
                        )
                        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                        builder.setTitle("what do you want ?")
                        builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
                            if (which == 0) {
                                val intent = Intent(mContext, ViewFullImageActivity::class.java)
                                intent.putExtra("image_url", chat.url)
                                mContext.startActivity(intent)
                            }
                            if (which == 1) {
                                listener.deleteMassage(mChatList[position])
                            }
                            if (which == 2) {
                                dialog.cancel()
                            }
                        })
                        builder.show()
                    }
                } else if (chat.sender != firebaseUser.uid) {
                    textMassage!!.visibility = View.GONE
                    left_image_view!!.visibility = View.VISIBLE
                    Picasso.get().load(chat.url).into(left_image_view)
                    left_image_view!!.setOnClickListener {
                        val option = arrayOf<CharSequence>(
                            "View Full Image",
                            "Cancel"
                        )
                        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                        builder.setTitle("what do you want ?")
                        builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
                            if (which == 0) {
                                val intent = Intent(mContext, ViewFullImageActivity::class.java)
                                intent.putExtra("image_url", chat.url)
                                mContext.startActivity(intent)
                            }
                            if (which == 1) {
                                dialog.cancel()
                            }
                        })
                        builder.show()
                    }
                }
            }
            //text massage
            else {
                textMassage!!.text = chat.massage
                if (chat.sender == firebaseUser.uid) {
                    textMassage!!.setOnClickListener {
                        val option = arrayOf<CharSequence>(
                            "Delete massage",
                            "Cancel"
                        )
                        val builder: AlertDialog.Builder =
                            AlertDialog.Builder(itemView.context)
                        builder.setTitle("what do you want ?")
                        builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
                            if (which == 0) {
                                Log.d("delete","delete + ${mChatList[position]}")
                                listener.deleteMassage(mChatList[position])
                            }
                            if (which == 1) {
                                dialog.cancel()
                            }
                        })
                        builder.show()
                    }
                }
            }


            if (position == mChatList.size - 1) {
                if (chat.isSeen == true) {
                    text_seen!!.text = "seen"
                    if (chat.massage == "sent you an image" && chat.url != "") {
                        val lp: RelativeLayout.LayoutParams? =
                            text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                        lp!!.setMargins(0, 245, 10, 0)
                        text_seen!!.layoutParams = lp
                    }
                } else {
                    text_seen!!.text = "sent"
                    if (chat.massage == "sent you an image" && chat.url != "") {
                        val lp: RelativeLayout.LayoutParams? =
                            text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                        lp!!.setMargins(0, 245, 10, 0)
                        text_seen!!.layoutParams = lp
                    }
                }


            } else {
                text_seen!!.visibility = View.GONE
            }
        }

//        private fun deleteMassage(position: Int) {
//            val ref = FirebaseDatabase.getInstance().reference.child("Chats")
//                .child(mChatList.get(position).massageID!!)
//                .removeValue()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(itemView.context, "Deleted", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(
//                            itemView.context,
//                            "Failed, Not Deleted",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//
//        }
    }

}