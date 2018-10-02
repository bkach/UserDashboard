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
import android.arch.lifecycle.*
import com.kachscovsky.boris.userdashboard.MockUser.mockUser
import com.kachscovsky.boris.userdashboard.dashboard.DashboardViewModel
import com.kachscovsky.boris.userdashboard.main.MainComponent
import com.kachscovsky.boris.userdashboard.repository.Resource
import com.kachscovsky.boris.userdashboard.repository.User
import com.kachscovsky.boris.userdashboard.repository.UserRepository
import com.kachscovsky.boris.userdashboard.utils.StringUtils
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.verification.VerificationMode

@RunWith(MockitoJUnitRunner::class)
class DashboardViewModelTests {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var navigator: Navigator
    @Mock lateinit var mockGetUsersLiveData: LiveData<Resource<List<User>>>
    @Suppress("unused")
    @Mock lateinit var stringUtils: StringUtils
    @Mock lateinit var mainComponent: MainComponent

    @Mock lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var lifecycle: LifecycleRegistry

    @Mock lateinit var voidObserver: Observer<Void>
    @Mock lateinit var usersObserver: Observer<List<User>>
    @Mock lateinit var stringObserver: Observer<String>

    @InjectMocks lateinit var dashboardViewModel: DashboardViewModel

    @Before
    fun setup() {
        Mockito.`when`(userRepository.loadUsers(any()))
                .thenReturn(mockGetUsersLiveData)

        lifecycle = LifecycleRegistry(lifecycleOwner)
        Mockito.`when`(lifecycleOwner.lifecycle)
                .thenReturn(lifecycle)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        inject()
    }

    @Test
    fun `When attached, the click listener should be set correctly`() {
        dashboardViewModel.onClick(mockUser)
        verify(navigator).goToDetailView(eq(mockUser))
    }

    @Test
    fun `When attached, the users should be loaded from the repository`() {
        verify(userRepository).loadUsers(eq(true))
    }

    @Test
    fun `When attached and users are loaded successfully, the loading spinner should be hidden`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())
        captor.firstValue.onChanged(Resource(Resource.Status.SUCCESS, listOf(mockUser), null))

        verifyHideLoadingSpinner()
    }

    @Test
    fun `When attached and users are loaded successfully, the snackbar should be dismissed`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())
        captor.firstValue.onChanged(Resource(Resource.Status.SUCCESS, listOf(mockUser), null))

        verifyDismissSnackbar()
    }

    @Test
    fun `When attached and users are loaded successfully, the view should update the users`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())

        val listOfUsers = listOf(mockUser)
        val resource: Resource<List<User>> = Resource(Resource.Status.SUCCESS, listOfUsers,null)
        captor.firstValue.onChanged(resource)

        verifyUpdateUsers()
    }

    @Test
    fun `When attached and users are loaded successfully, the local cache should be updated`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())

        val listOfUsers = listOf(mockUser)
        val resource: Resource<List<User>> = Resource(Resource.Status.SUCCESS, listOfUsers,null)
        captor.firstValue.onChanged(resource)

        assertEquals(listOfUsers, dashboardViewModel.users)
    }

    @Test
    fun `When attached and users are loaded successfully but have no data, do not update the users`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())

        val resource: Resource<List<User>> = Resource(Resource.Status.SUCCESS, null,null)
        captor.firstValue.onChanged(resource)

        verifyUpdateUsers(never())
    }

    @Test
    fun `When attached and the network is loading, show the loading spinner`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())

        val resource: Resource<List<User>> = Resource(Resource.Status.LOADING, null,null)
        captor.firstValue.onChanged(resource)

        verifyShowLoadingSpinner()
    }

    @Test
    fun `When attached and the network fails, hide the loading spinner`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())

        val resource: Resource<List<User>> = Resource(Resource.Status.ERROR, null,null)
        captor.firstValue.onChanged(resource)

        verifyHideLoadingSpinner()
    }

    @Test
    fun `When attached and the network fails, log the error correctly`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())

        val resource: Resource<List<User>> = Resource(Resource.Status.ERROR, null,"Something went wrong!")
        captor.firstValue.onChanged(resource)

        verifyHideLoadingSpinner()
        verifyLogError("Error loading users: Something went wrong!")
    }

    @Test
    fun `When attached and the network fails, show the snackbar`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())

        val resource: Resource<List<User>> = Resource(Resource.Status.ERROR, null,"Something went wrong!")
        captor.firstValue.onChanged(resource)

        verifyShowSnackbar(null)
    }

    @Test
    fun `When attached and the network fails and retry is pressed, dismiss the snackbar and load users`() {
        val captor = argumentCaptor<Observer<Resource<List<User>>>>()
        verify(mockGetUsersLiveData).observe(anyOrNull(), captor.capture())

        val resource: Resource<List<User>> = Resource(Resource.Status.ERROR, null,"Something went wrong!")
        captor.firstValue.onChanged(resource)

        verifyShowSnackbar(null)
        dashboardViewModel.onRefresh()
        verifyDismissSnackbar()

        // Grand total of two times, including the last time this was called
        verify(mockGetUsersLiveData, times(2)).observe(anyOrNull(), anyOrNull())
    }

    @Test
    fun `When attached and the in-memory cache is hit, update view`() {
        val listOfUsers = listOf(mockUser)

        dashboardViewModel.users = listOfUsers
        dashboardViewModel.loadUsers()

        verifyHideLoadingSpinner()
        verifyDismissSnackbar(times(2))
        verifyUpdateUsers()
    }

    @Test
    fun `When attached and the in-memory cache is missed, goToRepository`() {
        verify(mockGetUsersLiveData).observe(anyOrNull(), anyOrNull())
    }

    private fun inject() {
        dashboardViewModel.inject(lifecycleOwner, mainComponent)
    }

    private fun verifyHideLoadingSpinner() {
        dashboardViewModel.hideLoadingSpinner.observe(lifecycleOwner, voidObserver)
        verify(voidObserver).onChanged(anyOrNull())
    }

    private fun verifyShowLoadingSpinner() {
        dashboardViewModel.showLoadingSpinner.observe(lifecycleOwner, voidObserver)
        verify(voidObserver).onChanged(anyOrNull())
    }

    private fun verifyUpdateUsers(mode: VerificationMode = times(1)) {
        dashboardViewModel.updateUsers.observe(lifecycleOwner, usersObserver)
        verify(usersObserver, mode).onChanged(eq(listOf(mockUser)))
    }

    private fun verifyDismissSnackbar(mode: VerificationMode = times(1)) {
        dashboardViewModel.dismissSnackbar.observe(lifecycleOwner, voidObserver)
        verify(voidObserver, mode).onChanged(anyOrNull())
    }

    private fun verifyLogError(message: String) {
        dashboardViewModel.logError.observe(lifecycleOwner, stringObserver)
        verify(stringObserver).onChanged(eq(message))
    }

    private fun verifyShowSnackbar(message: String?) {
        dashboardViewModel.showSnackbar.observe(lifecycleOwner, stringObserver)
        verify(stringObserver).onChanged(eq(message))
    }

}