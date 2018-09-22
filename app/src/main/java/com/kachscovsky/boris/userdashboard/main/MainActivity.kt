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

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.kachscovsky.boris.userdashboard.Navigator
import com.kachscovsky.boris.userdashboard.R
import com.kachscovsky.boris.userdashboard.dashboard.DashboardFragment
import com.kachscovsky.boris.userdashboard.detail.DetailFragment
import com.kachscovsky.boris.userdashboard.detail.DetailFragment.Companion.DETAIL_VIEW_USER_KEY
import com.kachscovsky.boris.userdashboard.repository.User

/**
 * [AppCompatActivity] used to hold the fragments of the other parts of the app. It is also the
 * [Navigator] used to navigate the user from fragment to fragment.
 *
 * All [ViewModel]s have this class as their [LifecycleObserver] so they are preserved even
 * through configuration changes might destroy the fragment.
 */
class MainActivity : AppCompatActivity(), MainViewModel.MainView, Navigator {

    lateinit var component: MainComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        component = DaggerMainComponent.builder()
                .mainModule(MainModule(this))
                .build()

        val mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.inject(component)
    }

    override fun goToDashboardView() {
        var dashboardFragment : Fragment? = supportFragmentManager
                .findFragmentByTag(DashboardFragment.TAG)

        if (dashboardFragment == null) {
            dashboardFragment = DashboardFragment()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_fragment_container,
                        dashboardFragment, DashboardFragment.TAG)
                .commit()
    }

    override fun goToDetailView(user: User) {
        var detailFragment : Fragment? = supportFragmentManager
                .findFragmentByTag(DetailFragment.TAG)

        if (detailFragment == null) {
            detailFragment = DetailFragment()
        }

        /**
         * The [User] must be given to the [DetailFragment] through the [Bundle]
         * to show a specific user
         */
        val bundle = Bundle()
        bundle.putParcelable(DETAIL_VIEW_USER_KEY, user)
        detailFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
                .add(R.id.main_activity_fragment_container, detailFragment, DetailFragment.TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
    }

    override fun goBack() {
        supportFragmentManager.popBackStack()
    }

}
