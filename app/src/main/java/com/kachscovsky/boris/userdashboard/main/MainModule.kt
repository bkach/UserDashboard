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

package com.kachscovsky.boris.userdashboard.main

import android.arch.lifecycle.LifecycleOwner
import android.arch.persistence.room.Room
import android.support.v4.app.FragmentManager
import com.kachscovsky.boris.userdashboard.Navigator
import com.kachscovsky.boris.userdashboard.repository.UINamesService
import com.kachscovsky.boris.userdashboard.repository.UserDao
import com.kachscovsky.boris.userdashboard.repository.UserDatabase
import com.kachscovsky.boris.userdashboard.repository.UserRepository
import com.kachscovsky.boris.userdashboard.utils.StringUtils
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * [Module] responsible for providing instances of injected classes
 */
@Module
class MainModule(private val mainActivity: MainActivity) {

    companion object {
        const val UI_NAMES_URL: String = "https://uinames.com/"
    }

    private val service: UINamesService by lazy {
        Retrofit.Builder()
                .baseUrl(UI_NAMES_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create<UINamesService>(UINamesService::class.java)
    }

    private val userRepository: UserRepository by lazy {
        val repository = UserRepository()
        mainActivity.component.inject(repository)
        repository
    }

    private val userDatabase: UserDatabase by lazy {
        Room.databaseBuilder(mainActivity, UserDatabase::class.java, "user-db").build()
    }

    @Provides
    fun providesStringUtils(): StringUtils {
        return StringUtils(mainActivity.resources)
    }

    @Provides
    fun providesNavigator(): Navigator {
        return mainActivity
    }

    @Provides
    fun providesLifecycleOwner(): LifecycleOwner {
        return mainActivity
    }

    @Provides
    fun providesFragmentManager(): FragmentManager {
        return mainActivity.supportFragmentManager
    }

    @Provides
    fun providesUiNamesService(): UINamesService {
        return service
    }

    @Provides
    fun providesUserRepository(): UserRepository {
        return userRepository
    }

    @Provides
    fun providesUserDao(): UserDao {
        return userDatabase.userDao()
    }

}