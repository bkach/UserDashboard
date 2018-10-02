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

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kachscovsky.boris.userdashboard.Navigator
import com.kachscovsky.boris.userdashboard.main.MainComponent
import com.kachscovsky.boris.userdashboard.repository.User
import com.kachscovsky.boris.userdashboard.utils.AgeCalculator
import com.kachscovsky.boris.userdashboard.utils.StringUtils
import javax.inject.Inject

/**
 * [ViewModel] which contains the View logic for the Detail View. Similar to the
 * [com.kachscovsky.boris.userdashboard.dashboard.DashboardViewModel]
 */
class DetailViewModel : ViewModel() {

    @Inject lateinit var navigator : Navigator
    @Inject lateinit var stringUtils: StringUtils
    val userLiveData: MutableLiveData<User> = MutableLiveData()

    companion object {
        const val MIN_VELOCITY: Int = 1000
    }

    fun inject(component: MainComponent) {
        component.inject(this)
    }

    fun setUser(user: User) {
        if (user.ageString.isNullOrEmpty()) {
            user.ageString = stringUtils.getAgeString(AgeCalculator.calculateAge(user.birthday.raw))
        }
        userLiveData.value = user
    }

    fun onGestureVelocityY(velocityY: Float) {
        if (velocityY > MIN_VELOCITY) {
            navigator.goBack()
        }
    }

}