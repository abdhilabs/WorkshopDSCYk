package com.abdhilabs.learn.workshopdscyk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdhilabs.learn.workshopdscyk.R
import com.abdhilabs.learn.workshopdscyk.model.Rating
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_rating.view.*

open class RatingAdapter(query: Query) : FirestoreAdapter<RatingAdapter.ViewHolder>(query) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(rating: Rating?) {
            itemView.rating_item_name.text = rating?.userName
            rating?.rating?.let {
                itemView.rating_item_rating.rating = it.toFloat()
            }

            itemView.rating_item_text.text = rating?.text
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rating, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getSnapshot(position).toObject(Rating::class.java))

}