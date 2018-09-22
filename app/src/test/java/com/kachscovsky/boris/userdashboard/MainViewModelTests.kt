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

import com.kachscovsky.boris.userdashboard.main.MainViewModel
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit

class MainViewModelTests {

    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()

    @Mock lateinit var mockNavigator: Navigator
    @InjectMocks lateinit var mainViewModel: MainViewModel

    @Test
    fun `When attached, go to Dashboard View`() {
        mainViewModel.onAttach()
        verify(mockNavigator).goToDashboardView()
    }

    @Test
    fun `When attached, go to Dashboard View when initial navigation is false`() {
        mainViewModel.initialNavigation = false
        mainViewModel.onAttach()
        verify(mockNavigator).goToDashboardView()
    }

    @Test
    fun `When attached, do not go to Dashboard View when initial navigation is true`() {
        mainViewModel.initialNavigation = true
        mainViewModel.onAttach()
        verify(mockNavigator, never()).goToDashboardView()
    }

}