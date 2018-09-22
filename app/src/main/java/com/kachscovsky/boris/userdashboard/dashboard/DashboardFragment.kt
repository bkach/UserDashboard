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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kachscovsky.boris.userdashboard.R
import com.kachscovsky.boris.userdashboard.main.MainActivity
import com.kachscovsky.boris.userdashboard.repository.User
import kotlinx.android.synthetic.main.fragment_dashboard.*

/**
 * [Fragment] containing the User Dashboard.
 *
 * The view logic has been abstracted out to the [DashboardViewModel] which accesses this class
 * through callbacks to the [DashboardViewModel.DashboardView].
 */
class DashboardFragment: Fragment(), DashboardViewModel.DashboardView {

    lateinit var dashboardViewModel : DashboardViewModel
    private val dashboardAdapter = DashboardAdapter()

    var snackbar: Snackbar? = null

    companion object {
        const val TAG: String = "DASHBOARD_FRAGMENT"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        injectViewModel()
    }

    /**
     * Injects dependencies into [ViewModel], and sets the scope of the [DashboardViewModel] to be
     * that of the [MainActivity] in order to be retained for events which occur during Orientation
     * Changes
     */
    private fun injectViewModel() {
        dashboardViewModel = ViewModelProviders.of(activity!!).get(DashboardViewModel::class.java)
        dashboardViewModel.inject(this, (activity as MainActivity).component)
    }

    /**
     * [DashboardViewModel.DashboardView] Implementation
     */

    override fun setupRecyclerView(rows: Int) {
        val gridLayoutManager = GridLayoutManager(context, rows)

        dashboard_fragment_recycler_view.apply {
            layoutManager = gridLayoutManager
            adapter = dashboardAdapter
        }

    }

    override fun onClick(observer: Observer<User>) {
        dashboardAdapter.onClickCallback.observe(this, observer)
    }

    override fun updateUsers(users: List<User>) {
        dashboardAdapter.setUserList(users)
        dashboardAdapter.notifyDataSetChanged()
    }

    override fun onRefresh(callback: () -> Unit) {
        swipe_refresh_container.setOnRefreshListener {
            callback()
        }
    }

    override fun showLoadingSpinner() {
        swipe_refresh_container.isRefreshing = true
    }

    override fun hideLoadingSpinner() {
        swipe_refresh_container.isRefreshing = false
    }

    override fun showSnackbar(message: String, onRetry: () -> Unit) {
        snackbar = Snackbar.make(view!!, message, Snackbar.LENGTH_INDEFINITE)
        snackbar!!.setAction(activity?.resources?.getString(R.string.retry)) { onRetry() }
        snackbar!!.show()
    }

    override fun dismissSnackbar() {
        if (snackbar != null) {
            snackbar!!.dismiss()
        }
    }

    override fun logError(message: String) {
        Log.e(DashboardFragment::class.java.simpleName, message)
    }

}
