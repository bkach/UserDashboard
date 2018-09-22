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

package com.kachscovsky.boris.userdashboard

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.kachscovsky.boris.userdashboard.repository.*
import com.nhaarman.mockitokotlin2.*
import okhttp3.Request
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepositoryTests {

    @Rule
    @JvmField
    val mockitoRule = MockitoJUnit.rule()!!

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock lateinit var  uiNamesService: UINamesService
    @Mock lateinit var  userDao: UserDao
    @Mock lateinit var lifecycleOwner: LifecycleOwner
    @Mock lateinit var lifecycle: Lifecycle
    @InjectMocks lateinit var userRepository: UserRepository

    @Mock lateinit var userListLiveData: LiveData<List<User>>
    private val databaseObserverArgumentCaptor= argumentCaptor<Observer<List<User>>>()

    var retrofitCallback: Callback<List<User>>? = null

    @Before
    fun setup() {
        whenever(lifecycleOwner.lifecycle).thenReturn(lifecycle)

        doNothing().whenever(userListLiveData).observe(anyOrNull(),
                databaseObserverArgumentCaptor.capture())

        whenever(userDao.load()).thenReturn(userListLiveData)
        whenever(uiNamesService.getUsers(500)).thenReturn(MockCall())
    }

    @Test
    fun `When loading users, the userDao should load the data`() {
        userRepository.loadUsers(true)
        verify(userDao).load()
    }

    @Test
    fun `When loading users with a non-empty database, the live data should return a successful resource and unsubscribe database observer`() {
        val loadedUsers: LiveData<Resource<List<User>>> = userRepository.loadUsers(true)

        loadedUsers.observe(lifecycleOwner, Observer<Resource<List<User>>> { resource ->
            assert(resource?.status == Resource.Status.SUCCESS)
            assert(resource?.data?.get(0) == MockUser.mockUser)
        })

        val observer = databaseObserverArgumentCaptor.firstValue
        observer.onChanged(listOf(MockUser.mockUser))
        verify(userListLiveData).removeObserver(anyOrNull())
    }

    @Test
    fun `When loading users with an empty database, the data should be loaded from the network and unsubscribe database observer`() {
        userRepository.loadUsers(false)
        verify(uiNamesService).getUsers(500)
    }

    @Test
    fun `When loading users and useDatabase is false, the data should be loaded from the network`() {
        userRepository.loadUsers(true)

        val observer = databaseObserverArgumentCaptor.firstValue
        observer.onChanged(emptyList())

        verify(uiNamesService).getUsers(500)

        verify(userListLiveData).removeObserver(anyOrNull())
    }

    @Test
    fun `When successfully loading users from the network, the values should be returned to the LiveData and replaced in the DB`() {
        // Initially start out with an empty database to trigger network call
        val loadedUsers: LiveData<Resource<List<User>>> = userRepository.loadUsers(true)
        val observer = databaseObserverArgumentCaptor.firstValue
        observer.onChanged(emptyList())

        val response: Response<List<User>> = Response.success(listOf(MockUser.mockUser))
        retrofitCallback?.onResponse(MockCall(), response)

        loadedUsers.observe(lifecycleOwner, Observer<Resource<List<User>>> { resource ->
            assert(resource?.status == Resource.Status.SUCCESS)
            assert(resource?.data?.get(0) == MockUser.mockUser)
        })

        verify(userDao).deleteAll()
        verify(userDao).save(eq(response.body()!!))
    }

    @Test
    fun `When unsuccessfully loading users from the network, an error should be sent to LiveData`() {
        val loadedUsers: LiveData<Resource<List<User>>> = userRepository.loadUsers(true)
        val observer = databaseObserverArgumentCaptor.firstValue
        observer.onChanged(emptyList())

        val response: Response<List<User>> = Response.error(404, ResponseBody.create(null, "content"))

        loadedUsers.observe(lifecycleOwner, Observer<Resource<List<User>>> { resource ->
            assert(resource?.status == Resource.Status.ERROR)
            assert(resource?.message.equals("Unsuccessful response"))
        })

        retrofitCallback?.onResponse(MockCall(), response)
    }

    @Test
    fun `When loading fails, an error should be sent to LiveData`() {
        val loadedUsers: LiveData<Resource<List<User>>> = userRepository.loadUsers(true)
        val observer = databaseObserverArgumentCaptor.firstValue
        observer.onChanged(emptyList())

        val throwable: Throwable = Exception("Something went wrong")

        loadedUsers.observe(lifecycleOwner, Observer<Resource<List<User>>> { resource ->
            assert(resource?.status == Resource.Status.ERROR)
            assert(resource?.message.equals("Someething went wrong"))
        })

        retrofitCallback?.onFailure(MockCall(), throwable)
    }

    inner class MockCall: Call<List<User>> {
        override fun isExecuted(): Boolean {
            return true
        }

        override fun clone(): Call<List<User>> {
            return this
        }

        override fun isCanceled(): Boolean {
            return false
        }

        override fun cancel() { }

        override fun execute(): Response<List<User>> {
            return Response.success(null)
        }

        override fun request(): Request {
            return Request.Builder().build()
        }

        override fun enqueue(callback: Callback<List<User>>) {
            retrofitCallback = callback
        }

    }
}

