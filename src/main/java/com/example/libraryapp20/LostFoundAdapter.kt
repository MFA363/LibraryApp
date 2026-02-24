package com.example.libraryapp20

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LostFoundAdapter(
    private val itemList: List<LostItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<LostFoundAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClaimClick(item: LostItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lost_found, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.desc.text = item.description ?: ""
        holder.date.text = "Found: ${item.dateFound ?: ""}"
        holder.location.text = "Loc: ${item.locationFound ?: ""}"

        if (item.status == "Claim Pending") {
            holder.btnClaim.text = "PENDING"
            holder.btnClaim.isEnabled = false
            holder.btnClaim.setOnClickListener(null)
        } else {
            holder.btnClaim.text = "CLAIM"
            holder.btnClaim.isEnabled = true
            holder.btnClaim.setOnClickListener {
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onClaimClick(itemList[pos])
                }
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val desc: TextView = itemView.findViewById(R.id.tvDescription)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val location: TextView = itemView.findViewById(R.id.tvLocation)
        val btnClaim: Button = itemView.findViewById(R.id.btnClaim)
    }
}
