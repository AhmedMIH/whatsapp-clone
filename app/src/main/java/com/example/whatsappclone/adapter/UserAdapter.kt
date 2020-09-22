package com.example.whatsappclone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.ui.activity.MassageChatActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.ui.activity.VisitUserProfileActivity
import com.example.whatsappclone.data.model.Chat
import com.example.whatsappclone.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context: Context, val mUsers: List<Users>, val isChat: Boolean) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var lastMassageString: String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.user_search_item_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: Users = mUsers[position]
        holder.onBind(user, context,isChat)
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var username: TextView = view.findViewById(R.id.username_user_search)
        var profileImageView: CircleImageView = view.findViewById(R.id.profile_image_user_search)
        var online: CircleImageView = view.findViewById(R.id.image_online_user_search)
        var offline: CircleImageView = view.findViewById(R.id.image_offline_user_search)
        var lastMassage: TextView = view.findViewById(R.id.massage_last_user_search)

        fun onBind(user: Users, context: Context, isChat: Boolean) {
            username.text = user.username
            if (user.profile == "") {
                Picasso.get().load(R.drawable.ic_profile).into(profileImageView)
            } else {
                Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile)
            }

            if (isChat) {
                retrieveLastMassage(user.uid, lastMassage)
            } else {
                lastMassage.visibility = View.GONE
            }

            if (isChat) {
                if (user.status == "online") {
                    online.visibility = View.VISIBLE
                    offline.visibility = View.GONE
                }
                if (user.status == "offline") {
                    online.visibility = View.GONE
                    offline.visibility = View.VISIBLE
                }
            } else {
                online.visibility = View.GONE
                offline.visibility = View.GONE
            }

            itemView.setOnClickListener {
                val option = arrayOf<CharSequence>(
                    "send Massage",
                    "Visit profile"
                )
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle("what do you want ?")
                builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
                    if (which == 0) {
                        val intent = Intent(context, MassageChatActivity::class.java)
                        intent.putExtra("visit_id", user.uid)
                        context.startActivity(intent)
                    }
                    if (which == 1) {
                        val intent = Intent(context, VisitUserProfileActivity::class.java)
                        intent.putExtra("visit_id", user.uid)
                        context.startActivity(intent)
                    }
                })
                builder.show()
            }

        }

        private fun retrieveLastMassage(userOnlineId: String?, lastMassageTxt: TextView) {
            lastMassageString = "default massage"

            val firebaseUsers = FirebaseAuth.getInstance().currentUser
            val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (snapshot in p0.children) {
                        val chat = snapshot.getValue(Chat::class.java)
                        if (firebaseUsers!!.uid != null && chat != null) {
                            if ((chat.receiver == firebaseUsers!!.uid && chat.sender == userOnlineId) ||
                                (chat.sender == firebaseUsers!!.uid && chat.receiver == userOnlineId)
                            ) {
                                lastMassageString = chat.massage!!
                            }
                        }
                    }
                    when (lastMassageString) {
                        "default massage" -> lastMassageTxt.text = "no massage"
                        "sent you an image" -> lastMassageTxt.text = "image sent"
                        else -> lastMassageTxt.text = lastMassageString
                    }
                    lastMassageString = "default massage"
                }

            })
        }
    }
}

