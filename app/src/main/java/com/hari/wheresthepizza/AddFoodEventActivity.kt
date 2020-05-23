package com.hari.wheresthepizza

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hari.wheresthepizza.adapter.FoodEventAdapter
import com.hari.wheresthepizza.data.FoodEvent
import com.hari.wheresthepizza.pickers.DatePicker
import com.hari.wheresthepizza.pickers.DateSetter
import com.hari.wheresthepizza.pickers.TimePicker
import com.hari.wheresthepizza.pickers.TimeSetter
import kotlinx.android.synthetic.main.activity_add_food_event.*
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.*


class AddFoodEventActivity : AppCompatActivity(), TimeSetter, DateSetter {

    var startTimeMinute = -1
    var startTimeHour = -1
    var endTimeMinute = -1
    var endTimeHour = -1
    var eventDateYear = -1
    var eventDateMonth = -1
    var eventDateDay = -1

    var uploadBitmap: Bitmap? = null
    var cameraPermissionProvided = false

    companion object {
        val SET_START_TIME = 1
        val SET_END_TIME = 2
        val CAMERA_REQUEST_CODE = 3
        val PERMISSION_REQUEST_CODE = 4
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food_event)
        setTitle("Add Post")

        btnAdd.setOnClickListener {
            if (true) {
            // TODO: uncomment after testing
            // if (allFieldsFilled()) {
                if (uploadBitmap != null) {
                    Log.d("PHOTO", "Called upload Post with Image()")
                    uploadPostWithImage()
                } else {
                    uploadPost("")
                    Log.d("PHOTO", "Called uploadPost")
                }
            }
        }

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        btnStartTimePicker.setOnClickListener {
            val timePickerDialog = TimePicker(this, SET_START_TIME)
            timePickerDialog.show(supportFragmentManager, "random_tag")
        }

        btnEndTimePicker.setOnClickListener {
            val timePickerDialog = TimePicker(this, SET_END_TIME)
            timePickerDialog.show(supportFragmentManager, "random_tag2")
        }

        btnDatePicker.setOnClickListener {
            val datePickerDialog = DatePicker(this)
            datePickerDialog.show(supportFragmentManager, "random_tag3")
        }

        hideEndTimeForLeftovers()
        requestNeededPermission()
    }

    private fun uploadPostWithImage() {
        // converts image to byte stream
        val baos = ByteArrayOutputStream()
        uploadBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInBytes = baos.toByteArray()

        val storageRef = FirebaseStorage.getInstance().getReference()
        // create file name
        val newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg"

        // creating an image reference by creating a child folder in storage
        val newImagesRef = storageRef.child("images/$newImage")

        // posting the image to that location
        newImagesRef.putBytes(imageInBytes)
            .addOnFailureListener { exception ->
                Toast.makeText(this@AddFoodEventActivity, exception.message, Toast.LENGTH_LONG).show()
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                newImagesRef.downloadUrl.addOnCompleteListener(object: OnCompleteListener<Uri> {
                    override fun onComplete(task: Task<Uri>) {
                        uploadPost(task.result.toString())
                        Log.d("PHOTO", "Called upload Post with the string " + task.result.toString())
                    }
                })
            }
    }

    private fun uploadPost(imgUrl: String) {
        val eventType =
            if (rbRegular.isChecked) {
                FoodEventAdapter.EVENT_REGULAR
            } else {
                FoodEventAdapter.EVENT_LEFTOVERS
            }

        var newPost = FoodEvent(
            FirebaseAuth.getInstance().uid!!,
            etName.text.toString(),
            etLocation.text.toString(),
            eventType,
            eventDateYear,
            eventDateMonth,
            eventDateDay,
            startTimeHour,
            startTimeMinute,
            endTimeHour,
            endTimeMinute,
            etDescription.text.toString(),
            imgUrl
        )


        FirebaseFirestore.getInstance().collection("posts").add(newPost)
            .addOnSuccessListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun hideEndTimeForLeftovers() {
        rbRegular.setOnClickListener {
            layoutEndTime.visibility = View.VISIBLE
        }

        rbLeftovers.setOnClickListener {
            layoutEndTime.visibility = View.GONE
        }
    }

    fun allFieldsFilled() : Boolean {
        return when {
            etName.text!!.isEmpty() -> {
                etName.setError("Please provide a title for your post.")
                false
            }
            etLocation.text!!.isEmpty() -> {
                etLocation.setError("Please mention a location for the food.")
                false
            }
            startTimeMinute == -1 -> {
                etStartTime.setError("Please set the start time")
                false
            }
            endTimeMinute == -1 && rbRegular.isChecked -> {
                etEndTime.setError("Please set the end time")
                false
            }
            eventDateDay == -1 -> {
                etEventDate.setError("Please set the event date")
                false
            }
            else -> true
        }
    }

    override fun setTime(minute: Int, hourOfDay: Int, targetId: Int) {
        if (targetId == SET_START_TIME) {
            etStartTime.setText(String.format("Start Time: %02d:%02d", hourOfDay, minute))
            etStartTime.setError(null)
            startTimeMinute = minute
            startTimeHour = hourOfDay
        } else if (targetId == SET_END_TIME) {
            etEndTime.setText(String.format("End Time: %02d:%02d", hourOfDay, minute))
            etEndTime.setError(null)
            endTimeMinute = minute
            endTimeHour = hourOfDay
        }
    }

    override fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        // January is considered to be 0, so we add 1 to month
        etEventDate.setText(String.format("Event Date: %d/%d/%d", dayOfMonth, month + 1, year))
        etEventDate.setError(null)
        eventDateDay = dayOfMonth
        eventDateMonth = month + 1
        eventDateYear = year
    }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                Toast.makeText(this,
                    "Need permissions to access camera", Toast.LENGTH_LONG).show()
            }

            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE)
        } else {
            cameraPermissionProvided = true
        }
    }

    fun startCamera(v: View) {
        if (cameraPermissionProvided) {
            startActivityForResult(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE),
            CAMERA_REQUEST_CODE)
            // buggy?
        } else {
            requestNeededPermission()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uploadBitmap = data!!.extras!!.get("data") as Bitmap

            // code to display retrieved image
            ivCameraPic.setImageBitmap(uploadBitmap)
            ivCameraPic.visibility = View.VISIBLE
        }
    }
}
