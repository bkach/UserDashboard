/*
 * UserDashboard
 * Copyright (C) 2018 Boris Kachscovsky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kachscovsky.boris.userdashboard.dashboard

import android.arch.lifecycle.MutableLiveData
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kachscovsky.boris.userdashboard.R
import com.kachscovsky.boris.userdashboard.repository.User
import com.squareup.picasso.Picasso

/**
 * An adapter for the Dashboard's [RecyclerView] which populates RecyclerView items
 */
class DashboardAdapter : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    // Local representation of the user list
    var users : List<User> = listOf()

    val onClickCallback: MutableLiveData<User> = MutableLiveData()

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DashboardAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.userNameTextView.text = user.name
        holder.ageTextView.text = user.ageString

        // Callback for RecyclerView item click
        holder.clickListener(View.OnClickListener {
            onClickCallback.value = user
        })

        // Load Image
        Picasso.get()
                .load(user.photo)
                .into(holder.photoImageView)

    }

    override fun getItemCount() = users.size

    fun setUserList(users: List<User>) {
        this.users = users
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.recyclerview_item_name_textview)
        val ageTextView: TextView = itemView.findViewById(R.id.recyclerview_item_age_textview)
        val photoImageView: ImageView = itemView.findViewById(R.id.recyclerview_item_photo_imageview)

        fun clickListener(listener: View.OnClickListener) {
            itemView.setOnClickListener(listener)
        }
    }

}
