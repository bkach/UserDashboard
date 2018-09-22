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

package com.kachscovsky.boris.userdashboard.detail

import android.arch.lifecycle.ViewModel
import com.kachscovsky.boris.userdashboard.Navigator
import com.kachscovsky.boris.userdashboard.detail.DetailViewModel.DetailView
import com.kachscovsky.boris.userdashboard.main.MainComponent
import com.kachscovsky.boris.userdashboard.repository.User
import com.kachscovsky.boris.userdashboard.utils.AgeCalculator
import com.kachscovsky.boris.userdashboard.utils.StringUtils
import javax.inject.Inject

/**
 * [ViewModel] which contains the View logic for the Detail View. Similar to the
 * [com.kachscovsky.boris.userdashboard.dashboard.DashboardViewModel], this view communicates with the
 * corresponding fragment through the [DetailView].
 */
class DetailViewModel : ViewModel() {

    @Inject lateinit var navigator : Navigator
    @Inject lateinit var stringUtils: StringUtils
    lateinit var view: DetailView
    lateinit var user: User

    companion object {
        const val MIN_VELOCITY: Int = 1000
    }

    fun inject(view: DetailView, component: MainComponent) {
        this.view = view
        component.inject(this)
        onAttach()
    }

    fun onAttach() {
        user = view.getUser()

        setupGestureListener()

        view.setUserImage(user.photo)
        view.setUserName(user.name)
        view.setUserAge(stringUtils.getAgeString(AgeCalculator.calculateAge(user.birthday.raw)))
        view.setUserRegion(user.region)
    }

    private fun setupGestureListener() {
        view.onGesture {velocityY ->
            run {
                if (velocityY > MIN_VELOCITY) {
                    navigator.goBack()
                }
            }
        }
    }

    interface DetailView {
        fun getUser(): User
        fun onGesture(onGesture: (Float) -> Unit)
        fun setUserImage(url: String)
        fun setUserName(name: String)
        fun setUserAge(age: String)
        fun setUserRegion(region: String)
    }
}