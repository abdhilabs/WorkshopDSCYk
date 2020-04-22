package com.abdhilabs.learn.workshopdscyk

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.abdhilabs.learn.workshopdscyk.model.Rating
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_rating.*

class RatingDialogFragment : DialogFragment(), View.OnClickListener {

    companion object {
        const val TAG = "RatingDialog"
    }

    interface RatingListener {

        fun onRating(rating: Rating)

    }

    private var mRatingListener: RatingListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_rating, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restaurant_form_button.setOnClickListener(this)
        restaurant_form_cancel.setOnClickListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is RatingListener) {
            mRatingListener = context
        }

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.restaurant_form_button -> {
                onSubmitClicked()
            }

            R.id.restaurant_form_cancel -> {
                onCancelClicked()
            }

        }
    }

    private fun onSubmitClicked() {
        val rating = Rating(
            FirebaseAuth.getInstance().currentUser?.uid,
            FirebaseAuth.getInstance().currentUser?.displayName,
            restaurant_form_rating.rating.toDouble(),
            restaurant_form_text.text.toString()
        )

        mRatingListener?.onRating(rating)
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

}