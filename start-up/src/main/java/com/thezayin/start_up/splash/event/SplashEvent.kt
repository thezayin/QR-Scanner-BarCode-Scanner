package com.thezayin.start_up.splash.event

sealed class SplashEvent {
    data object LoadSplash : SplashEvent()
    data object NavigateNext : SplashEvent()
}
