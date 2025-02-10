package com.thezayin.framework.components

import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.values.databinding.LayoutAdmobNativeSimpleBinding

@Composable
fun GoogleNativeSimpleAd(
    modifier: Modifier = Modifier,
    nativeAd: NativeAd?
) {
    if (LocalInspectionMode.current || nativeAd == null) {
        return
    }

    val bindingHolder = remember { mutableStateOf<LayoutAdmobNativeSimpleBinding?>(null) }

    Box {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                val binding = LayoutAdmobNativeSimpleBinding.inflate(LayoutInflater.from(ctx))
                bindingHolder.value = binding
                binding.root
            },
            update = { _ ->
                bindingHolder.value?.let { binding ->
                    binding.nativeView.apply {
                        headlineView = binding.adHeadline.apply { text = nativeAd.headline }
                        callToActionView = binding.adCallToAction.apply {
                            nativeAd.callToAction?.let { text = it }
                        }
                        iconView = binding.adIcon.apply {
                            nativeAd.icon?.let { setImageDrawable(it.drawable) }
                        }
                        setNativeAd(nativeAd)
                    }
                }
            }
        )
    }
}