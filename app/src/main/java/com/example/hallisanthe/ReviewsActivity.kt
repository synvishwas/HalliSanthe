package com.example.hallisanthe

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.adapter.ReviewAdapter
import com.example.hallisanthe.model.Review
import com.example.hallisanthe.utils.LanguageHelper
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class ReviewsActivity : AppCompatActivity() {

    private lateinit var recyclerReviews: RecyclerView
    private lateinit var tvAvgRating: TextView
    private lateinit var tvNoReviews: TextView
    private lateinit var etReviewerName: EditText
    private lateinit var etReviewComment: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var btnSubmit: Button

    private val db = FirebaseFirestore.getInstance()
    private val reviewList = mutableListOf<Review>()
    private lateinit var adapter: ReviewAdapter

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        recyclerReviews = findViewById(R.id.recyclerReviews)
        tvAvgRating     = findViewById(R.id.tvAvgRating)
        tvNoReviews     = findViewById(R.id.tvNoReviews)
        etReviewerName  = findViewById(R.id.etReviewerName)
        etReviewComment = findViewById(R.id.etReviewComment)
        ratingBar       = findViewById(R.id.ratingBar)
        btnSubmit       = findViewById(R.id.btnSubmitReview)

        val productId = intent.getStringExtra("productId") ?: ""

        recyclerReviews.layoutManager = LinearLayoutManager(this)
        adapter = ReviewAdapter(reviewList)
        recyclerReviews.adapter = adapter

        findViewById<TextView>(R.id.btnBackReviews).setOnClickListener { finish() }

        loadReviews(productId)

        btnSubmit.setOnClickListener {
            val name    = etReviewerName.text.toString().trim()
            val comment = etReviewComment.text.toString().trim()
            val rating  = ratingBar.rating

            if (name.isEmpty()) {
                etReviewerName.error = "Required"
                return@setOnClickListener
            }
            if (comment.isEmpty()) {
                etReviewComment.error = "Required"
                return@setOnClickListener
            }

            submitReview(productId, name, comment, rating)
        }
    }

    private fun loadReviews(productId: String) {
        db.collection("reviews")
            .whereEqualTo("productId", productId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                reviewList.clear()
                var totalRating = 0f

                for (doc in snapshot.documents) {
                    val r = doc.toObject(Review::class.java)
                    if (r != null) {
                        reviewList.add(r)
                        totalRating += r.rating
                    }
                }

                adapter.updateList(reviewList)

                val avg = if (reviewList.isEmpty()) 0f
                else totalRating / reviewList.size

                tvAvgRating.text = "%.1f ⭐ (${reviewList.size})".format(avg)
                tvNoReviews.visibility =
                    if (reviewList.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    private fun submitReview(
        productId: String,
        name: String,
        comment: String,
        rating: Float
    ) {
        val review = Review(
            id           = UUID.randomUUID().toString(),
            productId    = productId,
            reviewerName = name,
            comment      = comment,
            rating       = rating,
            timestamp    = System.currentTimeMillis()
        )

        db.collection("reviews").document(review.id).set(review)
            .addOnSuccessListener {
                Toast.makeText(this, "⭐ Review submitted!", Toast.LENGTH_SHORT).show()
                etReviewerName.text.clear()
                etReviewComment.text.clear()
                ratingBar.rating = 5f
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}