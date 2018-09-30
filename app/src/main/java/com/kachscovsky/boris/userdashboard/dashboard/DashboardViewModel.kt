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

import android.arch.lifecycle.*
import com.kachscovsky.boris.userdashboard.Navigator
import com.kachscovsky.boris.userdashboard.main.MainComponent
import com.kachscovsky.boris.userdashboard.repository.Resource
import com.kachscovsky.boris.userdashboard.repository.User
import com.kachscovsky.boris.userdashboard.repository.UserRepository
import com.kachscovsky.boris.userdashboard.utils.AgeCalculator
import com.kachscovsky.boris.userdashboard.utils.Action
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

    val setupRecyclerView: Action<Int> = Action()
    val showLoadingSpinner: Action<Void> = Action()
    val hideLoadingSpinner: Action<Void> = Action()
    val dismissSnackbar: Action<Void> = Action()
    val updateUsers: Action<List<User>> = Action()
    val showSnackbar: Action<String> = Action()
    val logError: Action<String> = Action()

    private var lifecycleOwner: LifecycleOwner? = null

    /**
     * This list serves as an in-memory cache
     */
    var users: List<User>? = null

    companion object {
        const val NUM_ROWS = 2
    }

    fun inject(lifecycleOwner: LifecycleOwner, component: MainComponent) {
        this.lifecycleOwner = lifecycleOwner
        component.inject(this)
        onAttach()
    }

    private fun onAttach() {
        setupRecyclerView.value = NUM_ROWS
        loadUsers()
    }

    /**
     * Attempts to load users from the in-memory cache ([users]), and if not from the
     * [UserRepository]
     */
    fun loadUsers() {
        when {
            users != null -> {
                hideLoadingSpinner.call()
                dismissSnackbar.call()
                updateUsers.value = users!!
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
                .observe(lifecycleOwner!!, Observer<Resource<List<User>>> { resource ->
            when {
                resource?.status == Resource.Status.SUCCESS
                        && resource.data != null -> onSuccess(resource.data!!)

                resource?.status == Resource.Status.LOADING -> onLoading()

                else -> onError(resource?.message)
            }
        })
    }

    private fun onSuccess(users: List<User>) {
        hideLoadingSpinner.call()
        dismissSnackbar.call()
        users.calculateAndFormatAges()
        // Save in-memory cache
        this.users = users
        updateUsers.value = users
    }

    private fun onLoading() {
        showLoadingSpinner.call()
    }

    private fun onError(message: String?) {
        hideLoadingSpinner.call()
        showSnackbar.value = stringUtils.getErrorMessage()
        logError.value = "Error loading users: " + (message ?: "")
    }

    fun onClick(user: User) {
        navigator.goToDetailView(user)
    }

    fun onRefresh() {
        dismissSnackbar.call()
        loadUsersFromRepository(false)
    }

    /**
     * The lifecycleOwner must be set to null in order to prevent memory leaks
     */
    override fun onCleared() {
        lifecycleOwner = null
    }

    /**
     * Creates a string resource from the UNIX timestamp of the users' date of birth
     */
    private fun List<User>?.calculateAndFormatAges() {
        this?.map {user ->
            user.ageString = stringUtils.getAgeString(AgeCalculator.calculateAge(user.birthday.raw))
        }
    }
}
