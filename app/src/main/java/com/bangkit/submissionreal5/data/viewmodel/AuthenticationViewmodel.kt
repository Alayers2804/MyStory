package com.bangkit.submissionreal5.data.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.submissionreal5.data.api.ApiConfig
import com.bangkit.submissionreal5.data.response.Login
import com.bangkit.submissionreal5.data.response.Register
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthenticationViewmodel(context: Context) : ViewModel() {

    val loading = MutableLiveData(View.GONE)
    val error = MutableLiveData("")
    val tempEmail = MutableLiveData("")
    val loginSuccess = MutableLiveData<Boolean>()

    val loginResult = MutableLiveData<Login>()
    val registrationResult = MutableLiveData<Register>()

    fun login(email: String, password: String){
        tempEmail.postValue(email)
        loading.postValue(View.VISIBLE)

        val client = ApiConfig.getApi().doLogin(email, password)

        client.enqueue(object: Callback<Login>{
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.isSuccessful){
                    loginResult.postValue(response.body())
                    loginSuccess.postValue(true)
                } else {
                    response.errorBody()?.let {
                        val errorRspn = JSONObject(it.string())
                        val errorMsg = errorRspn.getString("message")
                        error.postValue("LOGIN ERROR : $errorMsg")
                    }
                    loginSuccess.postValue(false)
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e("LoginError", "onFailure Call ${t.message}")
                error.postValue(t.message)
            }

        })
    }

    fun register(name: String, email: String, password: String){
        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApi().doRegister(name,email, password)

        client.enqueue(object : Callback<Register>{
            override fun onResponse(call: Call<Register>, response: Response<Register>) {
                if(response.isSuccessful){
                    registrationResult.postValue(response.body())
                } else {
                    response.errorBody()?.let {
                        val errorRspn = JSONObject(it.string())
                        val errorMsg = errorRspn.getString("message")
                        error.postValue("REGISTER ERROR : $errorMsg")
                    }
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<Register>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e("Register Error", "onFailure Call ${t.message}")
                error.postValue(t.message)
            }

        })

    }

}