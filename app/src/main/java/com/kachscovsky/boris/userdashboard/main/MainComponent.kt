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

import com.kachscovsky.boris.userdashboard.Navigator
import com.kachscovsky.boris.userdashboard.dashboard.DashboardAdapter
import com.kachscovsky.boris.userdashboard.dashboard.DashboardViewModel
import com.kachscovsky.boris.userdashboard.detail.DetailViewModel
import com.kachscovsky.boris.userdashboard.repository.UserRepository
import dagger.Component

/**
 * [Component] which specifies the classes which Dagger injects into
 */
@Component(modules = [MainModule::class])
interface MainComponent {

    fun inject(mainViewModel: MainViewModel)
    fun inject(navigator: Navigator)
    fun inject(dashboardViewModel: DashboardViewModel)
    fun inject(detailViewModel: DetailViewModel)
    fun inject(userRepository: UserRepository)
    fun inject(dashboardAdapter: DashboardAdapter)

}