package com.hari.wheresthepizza

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.hari.wheresthepizza.adapter.FoodEventAdapter
import com.hari.wheresthepizza.data.FoodEvent
import kotlinx.android.synthetic.main.activity_foodevent_detail.*
import java.lang.Exception


class FoodEventDetailFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.foodevent_detail, container, false)

        arguments?.let {
            if (it.containsKey(FOOD_EVENT_KEY)) {

                val post = it.getSerializable(FOOD_EVENT_KEY) as FoodEvent
                initView(post, rootView)


            }
        }

        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView(post: FoodEvent, rootView: View) {
        activity?.toolbar_layout?.title = post.title

        var tvTitle: TextView = rootView.findViewById(R.id.tvTitle)
        var tvLocation: TextView = rootView.findViewById(R.id.tvLocation)
        var tvDescription: TextView = rootView.findViewById(R.id.tvDescription)
        var tvTiming: TextView = rootView.findViewById(R.id.tvTiming)

        tvTitle.text = post.title
        tvLocation.text = post.locationName
        tvDescription.text = post.description
        tvTiming.text = returnTimeRangeString(post)
    }

    companion object {
        const val FOOD_EVENT_KEY = "food_event_key"
    }
}
