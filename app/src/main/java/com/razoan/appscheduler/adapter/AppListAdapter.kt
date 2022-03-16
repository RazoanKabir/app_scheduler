package com.razoan.appscheduler.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.razoan.appscheduler.R
import com.razoan.appscheduler.model.PackageAppInfo

class AppListAdapter(var apps: ArrayList<PackageAppInfo>): RecyclerView.Adapter<AppListAdapter.ParentViewHolder>() {

    private lateinit var context: Context
    var activity: Activity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListAdapter.ParentViewHolder {
        context = parent.context
        activity= parent.context as Activity?

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_list, parent, false)
        return AppListAdapter.ParentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        holder.ivIcon?.setImageDrawable(apps[position].icon)
        holder.tvPackageNameOfApp?.text = apps[position].packageName
        holder.tvNameOfApp?.text = apps[position].appName
    }

    class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivIcon: ImageView? = itemView.findViewById<ImageView>(R.id.ivSelectedAppIcon)
        val tvNameOfApp: TextView? = itemView.findViewById<TextView>(R.id.tvNameOfApp)
        val tvPackageNameOfApp: TextView? = itemView.findViewById<TextView>(R.id.tvPackageNameOfApp)

    }
}

