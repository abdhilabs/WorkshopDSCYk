package com.abdhilabs.learn.workshopdscyk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdhilabs.learn.workshopdscyk.R
import com.abdhilabs.learn.workshopdscyk.model.Restaurant
import com.abdhilabs.learn.workshopdscyk.util.RestaurantUtil
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_restaurant.view.*

open class RestaurantAdapter(query: Query?, private val listener: OnRestaurantSelectedListener) :
    FirestoreAdapter<RestaurantAdapter.ViewHolder>(query) {

    interface OnRestaurantSelectedListener {
        fun onRestaurantSelected(restaurant: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(snapshot: DocumentSnapshot, listener: OnRestaurantSelectedListener) {
            val restaurant = snapshot.toObject(Restaurant::class.java)
            val resources = itemView.resources

            Glide.with(itemView.restaurant_item_image.context)
                .load(restaurant?.photo)
                .into(itemView.restaurant_item_image)

            itemView.restaurant_item_name.text = restaurant?.name
            itemView.restaurant_item_rating.rating = restaurant?.avgRating!!.toFloat()
            itemView.restaurant_item_city.text = restaurant.city
            itemView.restaurant_item_category.text = restaurant.category
            itemView.restaurant_item_num_ratings.text = resources
                .getString(R.string.fmt_num_ratings, restaurant.numRating)
            itemView.restaurant_item_price.text = RestaurantUtil.getPriceString(restaurant)

            itemView.setOnClickListener {
                listener.onRestaurantSelected(snapshot)
            }
        }
    }
}