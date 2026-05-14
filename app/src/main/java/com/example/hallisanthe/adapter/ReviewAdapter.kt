package com.example.hallisanthe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.R
import com.example.hallisanthe.model.Review
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewAdapter(
    private var reviewList: MutableList<Review>
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView    = view.findViewById(R.id.tvReviewerName)
        val tvRating: TextView  = view.findViewById(R.id.tvReviewRating)
        val tvComment: TextView = view.findViewById(R.id.tvReviewComment)
        val tvDate: TextView    = view.findViewById(R.id.tvReviewDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = reviewList[position]
        holder.tvName.text    = r.reviewerName
        holder.tvComment.text = r.comment
        holder.tvRating.text  = "⭐".repeat(r.rating.toInt())
        holder.tvDate.text    = SimpleDateFormat(
            "dd MMM yyyy", Locale.getDefault()
        ).format(Date(r.timestamp))
    }

    override fun getItemCount() = reviewList.size

    fun updateList(newList: MutableList<Review>) {
        reviewList = newList
        notifyDataSetChanged()
    }
}