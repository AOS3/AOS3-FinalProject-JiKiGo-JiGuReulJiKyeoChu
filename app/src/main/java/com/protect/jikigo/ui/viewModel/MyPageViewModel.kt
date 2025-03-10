package com.protect.jikigo.ui.viewModel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.protect.jikigo.data.model.UserInfo
import com.protect.jikigo.data.repo.MyPageRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepo: MyPageRepo
) : ViewModel() {
    val totalSteps = MutableLiveData<String>()

    private val _profile = MutableLiveData<UserInfo>()
    val profile: LiveData<UserInfo> get() = _profile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun loadProfile(userid: String) {
        myPageRepo.getProfile(userid) { profile, id ->
            _profile.value = profile!!
            loading(false)
        }
    }

    fun loading(boolean: Boolean) {
        _isLoading.value = boolean
    }
}