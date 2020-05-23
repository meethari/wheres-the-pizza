package com.hari.wheresthepizza

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.hari.wheresthepizza.adapter.FoodEventAdapter
import com.hari.wheresthepizza.data.FoodEvent
import kotlinx.android.synthetic.main.activity_foodevent_list.*
import kotlinx.android.synthetic.main.foodevent_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [FoodEventDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class FeedActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    lateinit var adapter: FoodEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foodevent_list)

        setSupportActionBar(toolbar)
        toolbar.title = "Feed"

        // line to allow java Dates.
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()

        //FirebaseFirestore.setLoggingEnabled(true)

        fab.setOnClickListener { view ->
            var addPostIntent = Intent()
            addPostIntent.setClass(this, AddFoodEventActivity::class.java)
            startActivityForResult(addPostIntent, 1)
        }

        if (foodevent_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(foodevent_list)

        val db = FirebaseFirestore.getInstance()
        val query = db.collection("posts")

        query.addSnapshotListener(
            object : EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@FeedActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }

                    for (docChange in querySnapshot?.getDocumentChanges()!!) {
                        when (docChange.type) {
                            DocumentChange.Type.ADDED -> {
                                val post = docChange.document.toObject(FoodEvent::class.java)
                                adapter.addPost(post, docChange.document.id)
                            }
                            DocumentChange.Type.MODIFIED -> {}
                            DocumentChange.Type.REMOVED -> {
                                adapter.deletePostByKey(docChange.document.id)
                            }
                        }
                    }
                }

            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {adapter.notifyDataSetChanged()}
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = FoodEventAdapter(
            this,
            twoPane
        )
        recyclerView.adapter = adapter

    }

}
