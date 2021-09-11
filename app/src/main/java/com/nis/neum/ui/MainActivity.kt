package com.nis.neum.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.nis.neum.R
import com.nis.neum.data.network.api.response.ResError
import com.nis.neum.data.network.api.response.Services
import com.nis.neum.data.viewmodel.ServiceViewModel
import com.nis.neum.di.*
import com.nis.neum.di.utility.Resource
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.nis.neum.databinding.ActivityMainBinding as MainActivityBinding

class MainActivity : AppCompatActivity() {
    private val serviceViewModel by viewModel<ServiceViewModel>()
    private var binding: MainActivityBinding? = null
    private val serviceList = mutableListOf<Services>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)

        binding?.apply {
            setSupportActionBar(tbService)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                    (binding?.rvService?.adapter as MyServiceAdapter).getSelectedServiceList()
                        .filter { it.isAdded })
                if (serviceList.isEmpty()) {
                    toast("Please add atleast one service to book")
                    return@setOnClickListener
                }
                toast("You have selected ${serviceList.size} services")
            }
        }

        //  getServices()  //  Both Works
        serviceViewModel.getServices()
        listenForData()
    }

    private fun getServices() {
        blockInput(binding?.pbServices)
        serviceViewModel.getServices({ resService ->
            resService.data?.let {
                binding?.ivSpace?.loadImage(it.imageUrl.toString())
                binding?.tvAppliance?.text = it.categoryName
                binding?.tvApplianceDesc?.text = it.description
                if (!it.services.isNullOrEmpty()) {
                    (binding?.rvService?.adapter as MyServiceAdapter).addServiceList(it.services as List<Services>)
                } else {
                    toast(getString(R.string.label_no_space_or_service))
                }
            }
            this unblockInput binding?.pbServices
        }, {
            this unblockInput binding?.pbServices
            if (it is String)
                this toast it.toString()
            else
                this toast errorResponseMessage(it as ResError)
        }
        )
    }

    private fun listenForData() {
        serviceViewModel.responseLiveData.observe(this, { resService ->
            when (resService) {
                is Resource.Loading -> blockInput(binding?.pbServices)
                is Resource.Success -> {
                    resService.value.data?.let {
                        binding?.ivSpace?.loadImage(it.imageUrl.toString())
                        binding?.tvAppliance?.text = it.categoryName
                        binding?.tvApplianceDesc?.text = it.description
                        if (!it.services.isNullOrEmpty())
                            (binding?.rvService?.adapter as MyServiceAdapter).addServiceList(it.services as List<Services>)
                        else
                            toast(getString(R.string.label_no_space_or_service))
                    }
                    this unblockInput binding?.pbServices
                    serviceViewModel.responseLiveData.removeObservers(this)
                }
                is Resource.Failure -> {
                    this unblockInput binding?.pbServices
//                    this toast resService.errorCode.toString() + resService.errorBody.toString()
                    showAlert(
                        resService.errorCode.toString() + resService.errorBody.toString(),
                        "Try opening the app again"
                    )
                }
            }
        })
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
