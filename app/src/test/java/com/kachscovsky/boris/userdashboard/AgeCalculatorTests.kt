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

import com.kachscovsky.boris.userdashboard.utils.AgeCalculator
import junit.framework.Assert.assertEquals
import org.junit.Test

class AgeCalculatorTests {

    @Test
    fun `When the timestamp is set for a year, the correct age should be year-1`() {
        val twentySixYearsAgoInUnixTime = (System.currentTimeMillis() / 1000L) - (40 * 60 * 60 * 24 * 360)
        assertEquals(39, AgeCalculator.calculateAge(twentySixYearsAgoInUnixTime))
    }

    @Test
    fun `When the timestamp is set for a unix time stamp, it should return the correct age`() {
        assertEquals(31, AgeCalculator.calculateAge(551062610L))
    }

}