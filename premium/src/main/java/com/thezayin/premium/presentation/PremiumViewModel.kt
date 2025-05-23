package com.thezayin.premium.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.framework.utils.Response
import com.thezayin.premium.domain.usecase.CheckPremiumStatusUseCase
import com.thezayin.premium.domain.usecase.GetSubscriptionPriceUseCase
import com.thezayin.premium.domain.usecase.IsUserSubscribedUseCase
import com.thezayin.premium.domain.usecase.SubscribeToWeeklyPlanUseCase
import com.thezayin.premium.domain.usecase.SubscribeToYearlyPlanUseCase
import com.thezayin.premium.presentation.event.PremiumEvent
import com.thezayin.premium.presentation.state.PremiumState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PremiumViewModel(
    private val subscribeToYearlyPlanUseCase: SubscribeToYearlyPlanUseCase,
    private val subscribeToWeeklyPlanUseCase: SubscribeToWeeklyPlanUseCase,
    private val checkPremiumStatusUseCase: CheckPremiumStatusUseCase,
    private val isUserSubscribedUseCase: IsUserSubscribedUseCase,
    private val getSubscriptionPriceUseCase: GetSubscriptionPriceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PremiumState())
    val state: StateFlow<PremiumState> = _state

    fun onEvent(event: PremiumEvent) {
        when (event) {
            is PremiumEvent.LoadPremiumStatus -> loadPremiumStatus()
            is PremiumEvent.SubscribeToYearly -> subscribeToYearlyPlan()
            is PremiumEvent.SubscribeToWeekly -> subscribeToWeeklyPlan()
            is PremiumEvent.CheckSubscriptionStatus -> checkSubscriptionStatus()
            is PremiumEvent.LoadSubscriptionPrices -> loadSubscriptionPrices()
            is PremiumEvent.SetPackageName -> setPackageName(event.packageName)
        }
    }

    private fun loadPremiumStatus() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            checkPremiumStatusUseCase.execute().collect { response ->
                when (response) {
                    is Response.Success -> {
                        _state.value = _state.value.copy(
                            isPremium = response.data, isLoading = false
                        )
                    }

                    is Response.Error -> {
                        _state.value = _state.value.copy(
                            error = response.e, isLoading = false
                        )
                    }

                    Response.Loading -> TODO()
                }
            }
        }
    }

    private fun subscribeToYearlyPlan() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            subscribeToYearlyPlanUseCase.execute().collect { response ->
                when (response) {
                    is Response.Success -> {
                        _state.value = _state.value.copy(isSubscribed = true, isLoading = false)
                        onEvent(PremiumEvent.SetPackageName("Yearly Plan"))
                    }

                    is Response.Error -> {
                        _state.value = _state.value.copy(error = response.e, isLoading = false)
                    }

                    Response.Loading -> TODO()
                }
            }
        }
    }

    private fun subscribeToWeeklyPlan() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            subscribeToWeeklyPlanUseCase.execute().collect { response ->
                when (response) {
                    is Response.Success -> {
                        _state.value = _state.value.copy(isSubscribed = true, isLoading = false)
                        onEvent(PremiumEvent.SetPackageName("Weekly Plan"))
                    }

                    is Response.Error -> {
                        _state.value = _state.value.copy(error = response.e, isLoading = false)
                    }

                    Response.Loading -> TODO()
                }
            }
        }
    }

    private fun checkSubscriptionStatus() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            isUserSubscribedUseCase.execute().collect { response ->
                when (response) {
                    is Response.Success -> {
                        _state.value =
                            _state.value.copy(isSubscribed = response.data, isLoading = false)
                    }

                    is Response.Error -> {
                        _state.value = _state.value.copy(error = response.e, isLoading = false)
                    }

                    Response.Loading -> TODO()
                }
            }
        }
    }

    private fun loadSubscriptionPrices() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            getSubscriptionPriceUseCase.execute("yearly.sub.removeads").collect { response ->
                when (response) {
                    is Response.Success -> {
                        _state.value = _state.value.copy(
                            yearlySubscriptionPrice = response.data, isLoading = false
                        )
                    }

                    is Response.Error -> {
                        _state.value = _state.value.copy(error = response.e, isLoading = false)
                    }

                    Response.Loading -> TODO()
                }
            }

            getSubscriptionPriceUseCase.execute("weekly.sub.removeads").collect { response ->
                when (response) {
                    is Response.Success -> {
                        _state.value = _state.value.copy(
                            weeklySubscriptionPrice = response.data, isLoading = false
                        )
                    }

                    is Response.Error -> {
                        _state.value = _state.value.copy(error = response.e, isLoading = false)
                    }

                    Response.Loading -> TODO()
                }
            }
        }
    }

    private fun setPackageName(packageName: String) {
        _state.value = _state.value.copy(packageName = packageName)
    }
}
