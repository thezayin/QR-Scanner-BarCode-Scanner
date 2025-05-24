package com.thezayin.premium.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.framework.utils.Response
import com.thezayin.premium.domain.usecase.CheckPremiumStatusUseCase
import com.thezayin.premium.domain.usecase.GetSubscriptionPriceUseCase
import com.thezayin.premium.domain.usecase.IsUserSubscribedUseCase
import com.thezayin.premium.domain.usecase.SubscribeToWeeklyPlanUseCase
import com.thezayin.premium.domain.usecase.SubscribeToYearlyPlanUseCase
import com.thezayin.premium.presentation.event.PremiumEvent
import com.thezayin.premium.presentation.event.PremiumEvent.CheckSubscriptionStatus
import com.thezayin.premium.presentation.event.PremiumEvent.LoadPremiumStatus
import com.thezayin.premium.presentation.event.PremiumEvent.LoadSubscriptionPrices
import com.thezayin.premium.presentation.event.PremiumEvent.SubscribeToWeekly
import com.thezayin.premium.presentation.event.PremiumEvent.SubscribeToYearly
import com.thezayin.premium.presentation.state.PlanType
import com.thezayin.premium.presentation.state.PremiumState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val BASEPLAN_WEEKLY  = "s1w"
const val BASEPLAN_YEARLY  = "s1y"

class PremiumViewModel(
    private val checkPremium: CheckPremiumStatusUseCase,
    private val isSubscribed: IsUserSubscribedUseCase,
    private val getPrice: GetSubscriptionPriceUseCase,
    private val subscribeWeekly: SubscribeToWeeklyPlanUseCase,
    private val subscribeYearly: SubscribeToYearlyPlanUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PremiumState())
    val state: StateFlow<PremiumState> = _state.asStateFlow()

    fun onEvent(event: PremiumEvent) {
        when (event) {
            LoadSubscriptionPrices -> loadPrices()
            CheckSubscriptionStatus -> checkUserSubscribed()
            LoadPremiumStatus -> checkPremiumStatus()
            SubscribeToWeekly -> subscribe(PlanType.Weekly)
            SubscribeToYearly -> subscribe(PlanType.Yearly)
        }
    }

    private fun loadPrices() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        val weeklyRes = getPrice.execute(BASEPLAN_WEEKLY).first()
        val yearlyRes = getPrice.execute(BASEPLAN_YEARLY).first()
        _state.update {
            it.copy(
                weeklyPrice = (weeklyRes as? Response.Success)?.data,
                yearlyPrice = (yearlyRes as? Response.Success)?.data,
                isLoading = false
            )
        }
    }

    private fun checkUserSubscribed() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        val res = isSubscribed.execute().first()
        _state.update {
            when (res) {
                is Response.Success -> it.copy(isSubscribed = res.data, isLoading = false)
                is Response.Error -> it.copy(errorMessage = res.e, isLoading = false)
                else -> it.copy(isLoading = false)
            }
        }
    }

    private fun checkPremiumStatus() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        val res = checkPremium.execute().first()
        _state.update {
            when (res) {
                is Response.Success -> it.copy(isPremium = res.data, isLoading = false)
                is Response.Error -> it.copy(errorMessage = res.e, isLoading = false)
                else -> it.copy(isLoading = false)
            }
        }
    }

    private fun subscribe(plan: PlanType) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        val res = if (plan == PlanType.Weekly)
            subscribeWeekly.execute().first()
        else
            subscribeYearly.execute().first()

        _state.update {
            when (res) {
                is Response.Success -> it.copy(isSubscribed = true, isLoading = false)
                is Response.Error -> it.copy(errorMessage = res.e, isLoading = false)
                else -> it.copy(isLoading = false)
            }
        }
    }
}