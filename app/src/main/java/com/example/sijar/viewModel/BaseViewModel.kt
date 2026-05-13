package com.example.sijar.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sijar.api.utils.SessionManager

abstract class BaseViewModel (application: Application): AndroidViewModel(application) {

    protected val sessionManager = SessionManager.getInstance(application)
}