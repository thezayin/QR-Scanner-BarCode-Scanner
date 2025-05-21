package com.thezayin.framework.ads.native_ad

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isInvisible
import androidx.databinding.ViewDataBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.values.databinding.LayoutAdmobNativeSimpleBinding

sealed class GoogleNativeAdStyle {
    data object Simple : GoogleNativeAdStyle()
}

@Composable
fun GoogleNativeAd(
    modifier: Modifier = Modifier,
    style: GoogleNativeAdStyle = GoogleNativeAdStyle.Simple,
    nativeAd: NativeAd? = null,
    placeholder: @Composable () -> Unit = {}
) {
    if (LocalInspectionMode.current) {
        placeholder()
        return
    }
    Box {
        if (nativeAd != null) Box(modifier) {
            when (style) {
                GoogleNativeAdStyle.Simple -> GoogleNativeSimple.Render(nativeAd)
            }
        } else {
            placeholder()
        }
    }
}

private data object GoogleNativeSimple : GoogleNativeAdLayout<LayoutAdmobNativeSimpleBinding>() {
    override fun inflate(inflater: LayoutInflater) =
        LayoutAdmobNativeSimpleBinding.inflate(inflater)

    override fun LayoutAdmobNativeSimpleBinding.bind(nativeAd: NativeAd) {
        nativeView.headlineView = adHeadline.apply { text = nativeAd.headline }
        nativeView.callToActionView = adCallToAction.apply {
            usingNullable(nativeAd.callToAction) {
                text = it
            }
        }

        nativeView.iconView = adIcon.apply {
            usingNullable(nativeAd.icon) { setImageDrawable(it.drawable) }
        }

        nativeView.setNativeAd(nativeAd)
    }
}

private sealed class GoogleNativeAdLayout<B : ViewDataBinding> {
    @Composable
    fun Render(instance: NativeAd) {
        var binding: B? by remember { mutableStateOf(null) }
        AndroidView(
            factory = { inflate(it.layoutInflator).apply { binding = this }.root },
            update = { binding?.bind(instance) })
    }

    abstract fun inflate(inflater: LayoutInflater): B
    abstract fun B.bind(nativeAd: NativeAd)
}

private inline fun <V : View, T> V.usingNullable(parameter: T?, action: V.(T) -> Unit) {
    isInvisible = parameter == null
    if (!isInvisible) action(parameter!!)
}

val Context.layoutInflator: LayoutInflater
    get() = LayoutInflater.from(this)