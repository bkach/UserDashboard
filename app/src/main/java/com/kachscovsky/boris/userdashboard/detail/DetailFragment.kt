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

package com.kachscovsky.boris.userdashboard.detail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.kachscovsky.boris.userdashboard.Navigator
import com.kachscovsky.boris.userdashboard.R
import com.kachscovsky.boris.userdashboard.main.MainActivity
import com.kachscovsky.boris.userdashboard.repository.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*
import javax.inject.Inject

/**
 * [Fragment] containing the detail view
 *
 * Like the Dashboard, this only functions as the view and for setting up callbacks. All view logic
 * takes place in the [DetailViewModel]
 */
class DetailFragment @Inject constructor(): Fragment(), DetailViewModel.DetailView {

    private lateinit var detailViewModel : DetailViewModel
    @Inject lateinit var navigator : Navigator

    val gestureCallback: MutableLiveData<Float> = MutableLiveData()

    companion object {
        const val DETAIL_VIEW_USER_KEY = "DETAIL_VIEW_USER_KEY"
        const val TAG: String = "DETAIL_FRAGMENT"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        injectViewModel()
    }

    private fun injectViewModel() {
        detailViewModel = ViewModelProviders.of(activity!!).get(DetailViewModel::class.java)
        detailViewModel.inject(this, (activity as MainActivity).component)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        setupGestureListener(view)
        return view
    }

    /**
     * The [GestureDetector.SimpleOnGestureListener] must override
     * [GestureDetector.SimpleOnGestureListener.onDown] in order to call on flig, which is the method
     * used to capture swipe gestures
     */
    private fun setupGestureListener(view: View) {
        val gesture = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener(){
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onFling(event1: MotionEvent?, event2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                gestureCallback.value = velocityY
                return super.onFling(event1, event2, velocityX, velocityY)
            }
        })

        view.setOnTouchListener { _, motionEvent ->
            gesture.onTouchEvent(motionEvent)
        }
    }

    /**
     * [DetailViewModel.DetailView] Implementation
     */

    override fun getUser(): User {
        return arguments?.getParcelable(DETAIL_VIEW_USER_KEY)!!
    }

    override fun onGesture(onGesture: (Float) -> Unit) {
        gestureCallback.observe(this, Observer<Float> { velocityY -> onGesture(velocityY!!) })
    }

    override fun setUserImage(url: String) {
        Picasso.get()
                .load(url)
                .into(detail_fragment_background_imageview)
    }

    override fun setUserName(name: String) {
        detail_fragment_name_textview.text = name
    }

    override fun setUserAge(age: String) {
        detail_fragment_age_textview.text = age
    }

    override fun setUserRegion(region: String) {
        detail_fragment_location_textview.text = region
    }

}
