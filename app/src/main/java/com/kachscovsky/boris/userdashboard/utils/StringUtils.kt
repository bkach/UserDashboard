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

import android.content.res.Resources
import com.kachscovsky.boris.userdashboard.R

/**
 * Class used to facilitate getting strings from external resources (in this case strings.xml)
 */
open class StringUtils(private val resources: Resources) {

    open fun getAgeString(age: Int): String {
        return resources.getString(R.string.years, age)
    }

    open fun getErrorMessage(): String {
        return resources.getString(R.string.error_message)
    }

}