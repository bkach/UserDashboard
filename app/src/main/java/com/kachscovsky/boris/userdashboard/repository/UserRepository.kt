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

package com.kachscovsky.boris.userdashboard.repository

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.security.auth.callback.Callback

/**
 * Repository class which contains the logic for accessing user data
 *
 * Data is cached here using the Room library.
 */
open class UserRepository {

    companion object {
        const val NUM_USERS_TO_REQUEST = 500
    }

    @Inject lateinit var uiNamesService: UINamesService
    @Inject lateinit var userDao: UserDao
    @Inject lateinit var lifecycleOwner: LifecycleOwner

    val data: MutableLiveData<Resource<List<User>>> = MutableLiveData()
    private lateinit var databaseObserver: Observer<List<User>>

    open fun loadUsers(useDatabase: Boolean) : LiveData<Resource<List<User>>>{
        data.value = Resource.loading(null)

        // Load Results from database
        if (useDatabase) {
            val loadLiveData = userDao.load()


            // Subscribe to database updates - but unsubscribe once loaded in order to allow
            // the view to be updated from the network
            databaseObserver = Observer { users ->
                if (users == null || users.isEmpty()) {
                        loadUsersFromNetwork()
                    } else {
                        data.value = Resource.success(users)
                    }
                    unsubscribeObserver(loadLiveData)
                }
            loadLiveData.observe(lifecycleOwner, databaseObserver)
        } else {
            loadUsersFromNetwork()
        }

        return data
    }

    private fun unsubscribeObserver(loadLiveData: LiveData<List<User>>) {
        loadLiveData.removeObserver(databaseObserver)
    }

    private fun loadUsersFromNetwork() {
        uiNamesService.getUsers(NUM_USERS_TO_REQUEST).enqueue(object : Callback, retrofit2.Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) =
                    if (!response.isSuccessful || response.body() == null) {
                        data.value = Resource.error("Unsuccessful response", null)
                    } else {
                        // Asynchronously replace the values in the database
                        Executors.newSingleThreadExecutor().execute {
                            userDao.deleteAll()
                            userDao.save(response.body()!!)
                        }
                        data.value = Resource.success(response.body()!!)
                    }

            override fun onFailure(call: Call<List<User>>, t: Throwable?) {
                data.value = Resource.error(t?.message!!, null)
            }
        })
    }

}