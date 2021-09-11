/*
 * *
 *  * Created by Nethaji on 11/9/21 1:32 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 11/9/21 1:32 PM
 *
 */

package com.nis.neum.data.repository

import com.google.gson.Gson
import com.nis.neum.data.network.api.response.ResError
import com.nis.neum.data.network.api.response.ResServices
import com.nis.neum.data.network.api.service.ServiceApi
import com.nis.neum.di.OnError
import com.nis.neum.di.OnSuccess
import com.nis.neum.di.utility.ResponseReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceRepository(private val api: ServiceApi) : ResponseReceiver {

    suspend fun getServices(
        onSuccess: OnSuccess<ResServices>,
        onError: OnError<Any>
    ) {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getServices()
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.status)
                            withContext(Dispatchers.Main) { onSuccess(it) }
                        else
                            withContext(Dispatchers.Main) { onError(it.status) }
                    }
                } else {
                    /*withContext(Dispatchers.Main) {
                        onError(  //  Customised server error
                            Gson().fromJson(
                                response.errorBody()?.string(), ResError::class.java
                            )
                        )
                    }*/
                    withContext(Dispatchers.Main) { onError(response.message().toString()) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message.toString()) }
            }
        }
    }

    suspend fun getService() = callApi { api.getService() }

    companion object Factory {
        fun create(api: ServiceApi) = ServiceRepository(api)
    }
}