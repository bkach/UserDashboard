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

package com.kachscovsky.boris.userdashboard.utils

/**
 * Calculates the time from a given timestamp to today in years
 */
object AgeCalculator {

    fun calculateAge(timeStamp: Long) : Int {
        val now = System.currentTimeMillis() / 1000L
        val age = now - timeStamp
        val years = (age / 60.0 / 60.0 / 24.0 / 365.0)
        return years.toInt()
    }

}