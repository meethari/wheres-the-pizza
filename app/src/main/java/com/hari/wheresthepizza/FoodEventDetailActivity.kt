package com.hari.wheresthepizza

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.hari.wheresthepizza.data.FoodEvent
import kotlinx.android.synthetic.main.activity_foodevent_detail.*

/**
 * An activity representing a single FoodEvent detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [FeedActivity].
 */
class FoodEventDetailActivity : AppCompatActivity() {

    lateinit var post: FoodEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foodevent_detail)
        setSupportActionBar(detail_toolbar)

        post = intent.getSerializableExtra(FoodEventDetailFragment.FOOD_EVENT_KEY) as FoodEvent

        if (post.imgUrl.isNotEmpty()) {
            Glide.with(this).load(post.imgUrl).into(kbvFoodImage)
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Coming soon - the option to edit your posts!", Snackbar.LENGTH_LONG)
                .setAction("Dismiss", null).show()
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = FoodEventDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(FoodEventDetailFragment.FOOD_EVENT_KEY, post)
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.foodevent_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                navigateUpTo(Intent(this, FeedActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
