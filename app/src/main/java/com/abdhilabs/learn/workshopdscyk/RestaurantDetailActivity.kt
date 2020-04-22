package com.abdhilabs.learn.workshopdscyk

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdhilabs.learn.workshopdscyk.adapter.RatingAdapter
import com.abdhilabs.learn.workshopdscyk.model.Rating
import com.abdhilabs.learn.workshopdscyk.model.Restaurant
import com.abdhilabs.learn.workshopdscyk.util.RestaurantUtil
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_restaurant_detail.*

class RestaurantDetailActivity : AppCompatActivity()
    , View.OnClickListener, EventListener<DocumentSnapshot>,
    RatingDialogFragment.RatingListener {

    companion object {
        const val TAG = "RestaurantDetail"
        const val KEY_RESTAURANT_ID = "key_restaurant_id"
    }

    private lateinit var mRatingDialog: RatingDialogFragment

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mRestaurantRef: DocumentReference
    private lateinit var mRestaurantRegistration: ListenerRegistration

    private lateinit var mRatingAdapter: RatingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        restaurant_button_back.setOnClickListener(this)
        fab_show_rating_dialog.setOnClickListener(this)

        val restaurantId = intent.extras?.getString(KEY_RESTAURANT_ID)

        mFirestore = FirebaseFirestore.getInstance()

        restaurantId?.apply {
            mRestaurantRef = mFirestore
                .collection("restaurants").document(restaurantId)
        }

        val ratingQuery = mRestaurantRef
            .collection("ratings")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)

        //recyclerview
        mRatingAdapter = object : RatingAdapter(ratingQuery) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    recycler_ratings.visibility = View.GONE
                    view_empty_ratings.visibility = View.VISIBLE
                } else {
                    recycler_ratings.visibility = View.VISIBLE
                    view_empty_ratings.visibility = View.GONE
                }
            }
        }

        recycler_ratings.layoutManager = LinearLayoutManager(this)
        recycler_ratings.adapter = mRatingAdapter

        mRatingDialog = RatingDialogFragment()

    }

    override fun onStart() {
        super.onStart()

        mRatingAdapter.startListening()
        mRestaurantRegistration = mRestaurantRef.addSnapshotListener(this)

    }

    override fun onStop() {
        super.onStop()

        mRatingAdapter.stopListening()

        mRestaurantRegistration.remove()

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.restaurant_button_back -> {
                onBackArrowClicked()
            }

            R.id.fab_show_rating_dialog -> {
                onAddRatingClicked()
            }

        }
    }

    private fun addRating(restaurantRef: DocumentReference, rating: Rating): Task<Void> {

        val ratingRef = restaurantRef.collection("ratings")
            .document()

        return mFirestore.runTransaction(Transaction.Function<Void> { transaction ->
            val restaurant = transaction.get(restaurantRef)
                .toObject(Restaurant::class.java)

            restaurant?.let {
                val newNumRating = restaurant.numRating?.plus(1)

                val oldRatingTotal = restaurant.avgRating?.times(restaurant.numRating!!)

                val newAvgRating = if (rating.rating != null) {
                    (oldRatingTotal?.plus(rating.rating!!))?.div(newNumRating!!)!!
                } else {
                    (oldRatingTotal?.plus(0))?.div(newNumRating!!)!!
                }


                restaurant.numRating = newNumRating
                restaurant.avgRating = newAvgRating
                transaction.set(restaurantRef, restaurant)
                transaction.set(ratingRef, rating)
            }

            return@Function null
        })

    }

    private fun onRestaurantLoaded(restaurant: Restaurant) {
        restaurant_name.text = restaurant.name
        restaurant_rating.rating = restaurant.avgRating!!.toFloat()
        restaurant_num_ratings.text =
            getString(R.string.fmt_num_ratings, restaurant.numRating)
        restaurant_city.text = restaurant.city
        restaurant_category.text = restaurant.category
        restaurant_price.text = RestaurantUtil.getPriceString(restaurant)

        Glide.with(restaurant_image.context)
            .load(restaurant.photo)
            .into(restaurant_image)

    }

    private fun onBackArrowClicked() {
        onBackPressed()
    }

    private fun onAddRatingClicked() {
        mRatingDialog.show(supportFragmentManager, RatingDialogFragment.TAG)
    }

    override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {

        if (p1 != null) {
            Log.w(TAG, "restaurant:onEvent", p1)
            return
        }

        onRestaurantLoaded(p0?.toObject(Restaurant::class.java)!!)

    }

    override fun onRating(rating: Rating) {

        addRating(mRestaurantRef, rating)
            .addOnSuccessListener {
                Log.d(TAG, "Rating added")

                hideKeyboard()
                recycler_ratings.smoothScrollToPosition(0)
            }
            .addOnFailureListener {
                Log.w(TAG, "Add rating failed", it)

                hideKeyboard()
                Snackbar.make(
                    findViewById(android.R.id.content), "Failed to add rating",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
