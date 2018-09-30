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

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.kachscovsky.boris.userdashboard.Navigator
import com.kachscovsky.boris.userdashboard.main.MainComponent
import com.kachscovsky.boris.userdashboard.repository.Resource
import com.kachscovsky.boris.userdashboard.repository.User
import com.kachscovsky.boris.userdashboard.repository.UserRepository
import com.kachscovsky.boris.userdashboard.utils.AgeCalculator
import com.kachscovsky.boris.userdashboard.utils.StringUtils
import javax.inject.Inject

/**
 * [ViewModel] containing the View logic for the Dashboard View
 *
 * New data is only pulled from the network if there is no user data in the database, or if
 * a refresh is triggered manually.
 */
open class DashboardViewModel : ViewModel() {

    @Inject lateinit var navigator : Navigator
    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var stringUtils: StringUtils

    var view: DashboardView? = null

    /**
     * This list serves as an in-memory cache
     */
    var users: List<User>? = null

    companion object {
        const val NUM_ROWS = 2
    }

    fun inject(view: DashboardView, component: MainComponent) {
        this.view = view
        component.inject(this)
        onAttach()
    }

    fun onAttach() {
        view?.setupRecyclerView(NUM_ROWS)
        setupListeners()
        loadUsers()
    }

    private fun setupListeners() {
        view?.onClick(Observer { user -> navigator.goToDetailView(user!!) })

        // When refreshing, we want to ignore the database and attempt to get data from the network
        view?.onRefresh { loadUsersFromRepository(false) }
    }

    /**
     * Attempts to load users from the in-memory cache ([users]), and if not from the
     * [UserRepository]
     */
    fun loadUsers() {
        when {
            users != null -> {
                view?.hideLoadingSpinner()
                view?.dismissSnackbar()
                view?.updateUsers(users!!)
            }

            else -> loadUsersFromRepository(true)
        }
    }

    /**
     * Loads users from the [UserRepository] and handles results
     *
     * @param useDatabase whether the repository should check the database for users or refresh
     * directly from the server
     */
    open fun loadUsersFromRepository(useDatabase: Boolean) {
        userRepository.loadUsers(useDatabase)
                .observe(view!!, Observer<Resource<List<User>>> { resource ->
            when {
                resource?.status == Resource.Status.SUCCESS
                        && resource.data != null -> onSuccess(resource.data!!)

                resource?.status == Resource.Status.LOADING -> onLoading()

                else -> onError(resource?.message)
            }
        })
    }

    private fun onSuccess(users: List<User>) {
        view?.hideLoadingSpinner()
        view?.dismissSnackbar()
        users.calculateAndFormatAges()
        // Save in-memory cache
        this.users = users
        view?.updateUsers(users)
    }

    private fun onLoading() {
        view?.showLoadingSpinner()
    }

    private fun onError(message: String?) {
        view?.hideLoadingSpinner()
        view?.showSnackbar(stringUtils.getErrorMessage()) {
            view?.dismissSnackbar()
            loadUsersFromRepository(false)
        }

        view?.logError("Error loading users: " + (message ?: ""))
    }

    /**
     * Creates a string resource from the UNIX timestamp of the users' date of birth
     */
    private fun List<User>?.calculateAndFormatAges() {
        this?.map {
            it.ageString = stringUtils.getAgeString(AgeCalculator.calculateAge(it.birthday.raw))
        }
    }

    override fun onCleared() {
        view = null
    }

    interface DashboardView : LifecycleOwner {
        fun setupRecyclerView(rows: Int)
        fun updateUsers(users: List<User>)
        fun hideLoadingSpinner()
        fun showLoadingSpinner()
        fun onClick(observer: Observer<User>)
        fun showSnackbar(message: String, onRetry: () -> Unit)
        fun dismissSnackbar()
        fun onRefresh(callback: () -> Unit)
        fun logError(message: String)
    }

}
