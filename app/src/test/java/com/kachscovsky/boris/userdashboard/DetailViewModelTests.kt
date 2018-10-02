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
import com.kachscovsky.boris.userdashboard.MockUser.mockUser
import com.kachscovsky.boris.userdashboard.detail.DetailViewModel
import com.kachscovsky.boris.userdashboard.main.MainComponent
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTests {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock lateinit var navigator: Navigator
    @Mock lateinit var mainComponent: MainComponent
    @InjectMocks lateinit var detailViewModel: DetailViewModel

    @Before
    fun setup() {
        inject()
        detailViewModel.setUser(MockUser.mockUser)
    }

    fun inject() {
        detailViewModel.inject(mainComponent)
    }

    @Test
    fun `When attaches, set user fields with the provided User`() {
        val user = detailViewModel.userLiveData.value
        assertEquals(mockUser.photo, user?.photo)
        assertEquals(mockUser.name, user?.name)
        assertEquals( "0 years", user?.ageString)
        assertEquals( "Los Angeles", user?.region)
    }

    @Test
    fun `When swiping up slowly, do not trigger an exit from the view`() {
        detailViewModel.onGestureVelocityY(100f)
        verify(navigator, never()).goBack()
    }

    @Test
    fun `When swiping up quickly, trigger an exit from the view`() {
        detailViewModel.onGestureVelocityY(1001f)
        verify(navigator).goBack()
    }
}