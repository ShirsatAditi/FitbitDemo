package com.example.fitbitdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitbitdemo.Model.Distance
import kotlinx.android.synthetic.main.row_distances.view.*

/**
 * DistanceAdapter
 *
 * @author Aditi Shirsat
 */

class DistanceAdapter(private val mList: ArrayList<Distance>) :
    RecyclerView.Adapter<DistanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_distances,parent,false))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tv_title.text = mList[position].activity
        holder.itemView.tv_desc.text = mList[position].distance
    }

    class ViewHolder (itemView:View) : RecyclerView.ViewHolder(itemView)
}