/*
 * *
 *  * Created by Nethaji on 11/9/21 9:13 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 11/9/21 9:13 AM
 *
 */

package com.nis.neum.di

import com.nis.neum.data.network.api.service.ServiceApi
import com.nis.neum.data.network.http.HttpClientManager
import com.nis.neum.data.network.http.createApi
import com.nis.neum.data.repository.ServiceRepository
import com.nis.neum.data.viewmodel.ServiceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Networking modules here
 * Must be a singleton
 * Also, using the default overload methods
 **/
val NETWORKING_MODULE = module {
//    single { HttpClientManager.newInstance(androidContext()) }
    single { HttpClientManager.newInstance() }
    single { get<HttpClientManager>().createApi<ServiceApi>() }
}

/**
 * Repository modules here
 * Must be a singleton
 **/
val REPOSITORY_MODULE = module {
    single { ServiceRepository.create(get()) }
}

/**
 * ViewModel modules here
 **/
val VIEW_MODEL_MODULE = module {
    viewModel { ServiceViewModel(get()) }
}