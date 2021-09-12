/*
 * *
 *  * Created by Nethaji on 12/9/21 11:54 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 12/9/21 11:54 AM
 *
 */

package com.nis.neum.ui.view.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import com.nis.neum.R
import com.nis.neum.data.network.api.response.ResError
import com.nis.neum.data.network.api.response.Services
import com.nis.neum.data.viewmodel.ServiceViewModel
import com.nis.neum.databinding.FragmentServiceBinding
import com.nis.neum.di.*
import com.nis.neum.di.utility.Resource
import com.nis.neum.ui.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServiceFragment : Fragment(), LifecycleObserver {

    private val serviceViewModel by viewModel<ServiceViewModel>()
    private lateinit var binding: FragmentServiceBinding
    private val serviceList = mutableListOf<Services>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            rvService.adapter = MyServiceAdapter { isAdded, service ->
                if (isAdded)
                    serviceList.add(service)
                else
                    serviceList.remove(service)
            }

            btnPickDateAndTime.setOnClickListener {
//                if (serviceList.isEmpty()) {
                serviceList.clear()
                serviceList.addAll(
                    (binding.rvService.adapter as MyServiceAdapter).getSelectedServiceList()
                        .filter { it.isAdded })
                if (serviceList.isEmpty()) {
                    context?.toast("Please add atleast one service to book")
                    return@setOnClickListener
                }
                activity?.toast("You have selected ${serviceList.size} services")
            }
        }

        //  getServices()  //  Both Works
        serviceViewModel.getServices()
        listenForData()
    }

    private fun getServices() {
        (activity as MainActivity).blockInput()
        serviceViewModel.getServices({ resService ->
            resService.data?.let {
                binding.ivSpace.loadImage(it.imageUrl.toString())
                binding.tvAppliance.text = it.categoryName
                binding.tvApplianceDesc.text = it.description
                if (!it.services.isNullOrEmpty()) {
                    (binding.rvService.adapter as MyServiceAdapter).addServiceList(it.services as List<Services>)
                } else {
                    activity?.toast(getString(R.string.label_no_space_or_service))
                }
            }
            (activity as MainActivity).unblockInput()
        }, {
            (activity as MainActivity).unblockInput()
            if (it is String)
                activity?.toast(it.toString())
            else
                activity?.toast(errorResponseMessage(it as ResError))
        }
        )
    }

    private fun listenForData() {
        serviceViewModel.responseLiveData.observe(viewLifecycleOwner, { resService ->
            when (resService) {
                is Resource.Loading -> (activity as MainActivity).blockInput()
                is Resource.Success -> {
                    resService.value.data?.let {
                        binding.ivSpace.loadImage(it.imageUrl.toString())
                        binding.tvAppliance.text = it.categoryName
                        binding.tvApplianceDesc.text = it.description
                        if (!it.services.isNullOrEmpty())
                            (binding.rvService.adapter as MyServiceAdapter).addServiceList(it.services as List<Services>)
                        else
                            context?.toast(getString(R.string.label_no_space_or_service))
                    }
                    (activity as MainActivity).unblockInput()
                    serviceViewModel.responseLiveData.removeObservers(this)
                }
                is Resource.Failure -> {
                    (activity as MainActivity).unblockInput()
//                    this toast resService.errorCode.toString() + resService.errorBody.toString()
                    activity?.showAlert(
                        resService.errorCode.toString() + resService.errorBody.toString(),
                        "Try opening the app again"
                    )
                }
            }
        })
    }

}
