package com.abdhilabs.learn.workshopdscyk.viewmodel

import androidx.lifecycle.ViewModel
import com.abdhilabs.learn.workshopdscyk.Filters

class MainActivityViewModel : ViewModel() {

    var isSigningIn = false
    var filters:Filters = Filters.default
}