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

package com.kachscovsky.boris.userdashboard.main

import android.arch.lifecycle.ViewModel
import com.kachscovsky.boris.userdashboard.Navigator
import javax.inject.Inject

/**
 * [ViewModel] which contains the View logic for the Main View
 */
class MainViewModel : ViewModel() {

    @Inject lateinit var navigator : Navigator
    var initialNavigation: Boolean = false

    fun inject(component: MainComponent) {
        component.inject(this)
        onAttach()
    }

    fun onAttach() {
        if (!initialNavigation) {
            navigator.goToDashboardView()
            initialNavigation = true
        }
    }
}