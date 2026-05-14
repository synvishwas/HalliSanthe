package com.example.hallisanthe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.R

data class OnboardingSlide(
    val emoji: String,
    val title: String,
    val description: String
)

class OnboardingAdapter(
    private val slides: List<OnboardingSlide>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView       = view.findViewById(R.id.tvEmoji)
        val tvTitle: TextView       = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OnboardingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_onboarding, parent, false)
        )

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.tvEmoji.text       = slides[position].emoji
        holder.tvTitle.text       = slides[position].title
        holder.tvDescription.text = slides[position].description
    }

    override fun getItemCount() = slides.size
}