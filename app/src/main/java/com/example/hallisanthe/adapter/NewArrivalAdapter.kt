package com.example.hallisanthe.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.R
import com.example.hallisanthe.model.Product

class NewArrivalAdapter(
    private val context: Context,
    private var productList: MutableList<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<NewArrivalAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView      = view.findViewById(R.id.imgNewArrival)
        val name: TextView      = view.findViewById(R.id.tvNewArrivalName)
        val price: TextView     = view.findViewById(R.id.tvNewArrivalPrice)
        val location: TextView  = view.findViewById(R.id.tvNewArrivalLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_new_arrival, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = productList[position]
        holder.name.text     = p.name
        holder.price.text    = "₹ ${p.price}"
        holder.location.text = if (p.location.isNotEmpty()) "📍 ${p.location}" else ""

        if (p.imageUrl.isNotEmpty()) {
            try {
                val bytes  = Base64.decode(p.imageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.img.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.img.setImageResource(R.drawable.ic_launcher_background)
            }
        }
        holder.itemView.setOnClickListener { onItemClick(p) }
    }

    override fun getItemCount() = productList.size

    fun updateList(newList: MutableList<Product>) {
        productList = newList
        notifyDataSetChanged()
    }
}