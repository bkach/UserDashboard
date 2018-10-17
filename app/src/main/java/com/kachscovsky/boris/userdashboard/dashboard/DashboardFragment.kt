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
import kotlinx.android.synthetic.main.fragment_dashboard.*

/**
 * [Fragment] containing the User Dashboard.
 *
 * The view logic has been abstracted out to the [DashboardViewModel]
 */
class DashboardFragment: Fragment() {

    private lateinit var dashboardViewModel : DashboardViewModel
    private val dashboardAdapter = DashboardAdapter()

    var snackbar: Snackbar? = null

    companion object {
        const val TAG: String = "DASHBOARD_FRAGMENT"
        const val NUM_COLUMNS = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        injectViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, NUM_COLUMNS)

        dashboard_fragment_recycler_view.apply {
            layoutManager = gridLayoutManager
            adapter = dashboardAdapter
        }
    }

    /**
     * Injects dependencies into [ViewModel], and sets the scope of the [DashboardViewModel] to be
     * that of the [MainActivity] in order to be retained for events which occur during Orientation
     * Changes
     */
    private fun injectViewModel() {
        dashboardViewModel = ViewModelProviders.of(activity!!).get(DashboardViewModel::class.java)
        dashboardViewModel.inject((activity as MainActivity).component)

        observeViewModelActions()
    }

    private fun observeViewModelActions() {
        observeOnClick()
        observeUpdateUsers()
        observeOnRefresh()
        observeShowLoadingSpinner()
        observeHideLoadingSpinner()
        observeShowSnackbar()
        observeDismissSnackbar()
        observeLogError()
    }

    private fun observeOnClick() {
        dashboardAdapter.onClickCallback.observe(this, Observer { user ->
            dashboardViewModel.onClick(user!!)
        })
    }

    private fun observeUpdateUsers() {
        dashboardViewModel.updateUsers.observe(this, Observer { users ->
            dashboardAdapter.setUserList(users!!)
            dashboardAdapter.notifyDataSetChanged()
        })
    }

    private fun observeOnRefresh() {
        swipe_refresh_container.setOnRefreshListener {
            dashboardViewModel.onRefresh()
        }
    }

    private fun observeShowLoadingSpinner() {
        dashboardViewModel.showLoadingSpinner.observe(this, Observer {
            swipe_refresh_container.isRefreshing = true
        })
    }

    private fun observeHideLoadingSpinner() {
        dashboardViewModel.hideLoadingSpinner.observe(this, Observer {
            swipe_refresh_container.isRefreshing = false
        })
    }

    private fun observeShowSnackbar() {
        dashboardViewModel.showSnackbar.observe(this, Observer { message ->
            snackbar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_INDEFINITE)
            snackbar!!.setAction(activity?.resources?.getString(R.string.retry)) { dashboardViewModel.onRefresh() }
            snackbar!!.show()
        })
    }

    private fun observeDismissSnackbar() {
        dashboardViewModel.dismissSnackbar.observe(this, Observer {
            if (snackbar != null) {
                snackbar!!.dismiss()
            }
        })
    }

    private fun observeLogError() {
        dashboardViewModel.logError.observe(this, Observer {message ->
            Log.e(DashboardFragment::class.java.simpleName, message)
        })
    }

}
