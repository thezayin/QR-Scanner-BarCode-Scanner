package com.thezayin.start_up.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.thezayin.start_up.onboarding.actions.OnboardingActions
import com.thezayin.start_up.onboarding.components.OnboardingContent
import org.koin.compose.koinInject

@Composable
fun OnboardingScreen(
    navigateToHome: () -> Unit,
    vm: OnboardingViewModel = koinInject()
) {
    val state = vm.state.collectAsState()

    if (state.value.isOnboardingCompleted) {
        navigateToHome()
        return
    }

    OnboardingContent(
        vm = vm,
        onboardPages = state.value.pages,
        currentPage = state.value.currentPage,
        onNextClicked = {
            if (state.value.currentPage < state.value.pages.size - 1) {
                vm.onAction(OnboardingActions.NextPage)
            } else {
                vm.onAction(OnboardingActions.CompleteOnboarding)
            }
        }
    )
}