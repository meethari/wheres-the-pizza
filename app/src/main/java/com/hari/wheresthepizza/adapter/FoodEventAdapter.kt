package com.hari.wheresthepizza.adapter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hari.wheresthepizza.*
import com.hari.wheresthepizza.data.FoodEvent
import kotlinx.android.synthetic.main.foodevent_list_content.view.*

class FoodEventAdapter(
    private val parentActivity: FeedActivity,
    private val twoPane: Boolean
) :
    RecyclerView.Adapter<FoodEventAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    var keys = mutableListOf<String>()
    var posts = mutableListOf<FoodEvent>()

    companion object {

        val EVENT_REGULAR = 1
        val EVENT_LEFTOVERS = 2

    }



    init {
        onClickListener = View.OnClickListener { v ->
            val foodEventKey = v.tag as String
            val post = getPostByKey(foodEventKey)
            if (twoPane) {
                val fragment = FoodEventDetailFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(FoodEventDetailFragment.FOOD_EVENT_KEY, post)
                    }
                }
                parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.foodevent_detail_container, fragment)
                    .commit()
            } else {
                val intent = Intent(
                    v.context,
                    FoodEventDetailActivity::class.java
                ).apply {
                    putExtra(FoodEventDetailFragment.FOOD_EVENT_KEY, post)
                    Log.d("some_tag", post.description)
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.foodevent_list_content, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.tvTitle.text = post.title
        holder.tvLocation.text = post.locationName
        holder.tvTiming.text = returnTimeRangeString(post)
        
        with(holder.itemView) {
            tag = keys[holder.adapterPosition]
            setOnClickListener(onClickListener)
        }

        if (post.uid == FirebaseAuth.getInstance().uid) {
            holder.btnDelete.visibility = View.VISIBLE
        } else {
            holder.btnDelete.visibility = View.GONE
        }

        holder.btnDelete.setOnClickListener {
            val key = keys[holder.adapterPosition]
            FirebaseFirestore.getInstance().collection("posts").document(key).delete()
            deletePostByKey(key)
        }
    }

    override fun getItemCount() = posts.size

    fun addPost(post: FoodEvent, key: String) {
        keys.add(key)
        posts.add(post)
        notifyDataSetChanged()
    }

    fun deletePostByKey(key: String) {
        // to remove a post from the adapter using its key
        val index = keys.indexOf(key)
        if (index > -1) {
            keys.removeAt(index)
            posts.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun getPostByKey(key: String): FoodEvent {
        val index = keys.indexOf(key)
        return posts[index]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.tvTitle
        val tvLocation: TextView = view.tvLocation
        val tvTiming: TextView = view.tvTiming
        val btnDelete: Button = view.btnDelete
    }
}
