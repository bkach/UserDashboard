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

import com.kachscovsky.boris.userdashboard.MockUser.mockUser
import com.kachscovsky.boris.userdashboard.detail.DetailViewModel
import com.kachscovsky.boris.userdashboard.main.MainComponent
import com.kachscovsky.boris.userdashboard.utils.StringUtils
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit

class DetailViewModelTests {

    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()!!

    @Mock lateinit var view: DetailViewModel.DetailView
    @Mock lateinit var navigator: Navigator
    @Mock lateinit var mainComponent: MainComponent
    @Mock lateinit var stringUtils: StringUtils
    @InjectMocks lateinit var detailViewModel: DetailViewModel

    @Before
    fun setup() {
        whenever(view.getUser()).thenReturn(mockUser)
        setupAgeCaptor()
        inject()
    }

    fun inject() {
        detailViewModel.inject(view, mainComponent)
    }

    private fun setupAgeCaptor() {
        val ageCaptor = argumentCaptor<Int>()
        Mockito.`when`(stringUtils.getAgeString(ageCaptor.capture()))
                .thenReturn("${ageCaptor.capture()} years")
    }

    @Test
    fun `When attached, get the User from the view`() {
        verify(view).getUser()
    }

    @Test
    fun `When attaches, set user fields with the provided User`() {
        verify(view).setUserImage(eq(mockUser.photo))
        verify(view).setUserName(eq(mockUser.name))
        verify(view).setUserAge(eq("0 years"))
        verify(view).setUserRegion(eq("Los Angeles"))
    }

    @Test
    fun `When swiping up slowly, do not trigger an exit from the view`() {
        val captor = argumentCaptor<Function1<Float,Unit>>()
        verify(view).onGesture(captor.capture())
        captor.firstValue(100f)
        verify(navigator, never()).goBack()
    }

    @Test
    fun `When swiping up quickly, trigger an exit from the view`() {
        val captor = argumentCaptor<Function1<Float,Unit>>()
        verify(view).onGesture(captor.capture())
        captor.firstValue(1001f)
        verify(navigator).goBack()
    }


}