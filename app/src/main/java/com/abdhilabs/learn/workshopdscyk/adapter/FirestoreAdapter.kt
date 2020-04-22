package com.abdhilabs.learn.workshopdscyk.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

abstract class FirestoreAdapter<VH : RecyclerView.ViewHolder>(
    private var query: Query?
) : RecyclerView.Adapter<VH>(), EventListener<QuerySnapshot> {

    companion object {
        const val TAG = "Firestore Adapter"
    }

    private var mRegistration: ListenerRegistration? = null

    private var mSnapshot = ArrayList<DocumentSnapshot>()

    fun stopListening() {
        if (mRegistration != null) {
            mRegistration!!.remove()
            mRegistration = null
        }

        mSnapshot.clear()
        notifyDataSetChanged()
    }

    fun startListening() {
        if (mRegistration == null) {
            mRegistration = query?.addSnapshotListener(this)
        }
    }

    fun setQuery(query: Query?) {
        stopListening()

        mSnapshot.clear()
        notifyDataSetChanged()

        this.query = query
        startListening()
    }

    protected open fun getSnapshot(index: Int): DocumentSnapshot {
        return mSnapshot[index]
    }

    protected open fun onError(e: FirebaseFirestoreException) {}

    protected open fun onDataChanged() {}

    override fun getItemCount(): Int = mSnapshot.size

    override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
        if (p1 != null) {
            Log.w(TAG, "onEvent:error", p1)
            onError(p1)
            return
        }

        for (change in p0!!.documentChanges) {
            when (change.type) {
                DocumentChange.Type.ADDED -> {
                    onDocumentAdded(change)
                }
                DocumentChange.Type.MODIFIED -> {
                    onDocumentModified(change)
                }
                DocumentChange.Type.REMOVED -> {
                    onDocumentRemoved(change)
                }
            }
        }
        onDataChanged()
    }

    private fun onDocumentRemoved(change: DocumentChange) {
        mSnapshot.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    private fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            mSnapshot[change.oldIndex] = change.document
            notifyDataSetChanged()
        } else {
            mSnapshot.removeAt(change.oldIndex)
            mSnapshot.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    private fun onDocumentAdded(change: DocumentChange) {
        mSnapshot.add(change.newIndex, change.document)
        notifyItemInserted(change.newIndex)
    }
}