/*
 * *
 *  * Created by Nethaji on 11/9/21 1:34 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 11/9/21 1:34 PM
 *
 */

package com.nis.neum.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nis.neum.data.network.api.response.ResServices
import com.nis.neum.data.repository.ServiceRepository
import com.nis.neum.di.OnError
import com.nis.neum.di.OnSuccess
import com.nis.neum.di.utility.Resource
import kotlinx.coroutines.launch

class ServiceViewModel(private val repository: ServiceRepository) : ViewModel() {

    private val _responseLiveData: MutableLiveData<Resource<ResServices>> = MutableLiveData()
    val responseLiveData: LiveData<Resource<ResServices>> get() = _responseLiveData

    //  fun getServices(block: (response: ResServices) -> Unit) {  // Higher order function can also be used
    fun getServices(
        onSuccess: OnSuccess<ResServices>,
        onError: OnError<Any>
    ) {
        viewModelScope.launch {
            repository.getServices(onSuccess, onError)
        }
    }

    fun getServices() {
        viewModelScope.launch {
            _responseLiveData.value = Resource.Loading
            _responseLiveData.value = repository.getService()
        }
    }

}