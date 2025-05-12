package com.thezayin.start_up.onboarding.components

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.thezayin.values.R

@Composable
fun GifDisplay(
    gifResId: Int, // Takes GIF resource ID as a parameter
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ImageLoader setup based on API version to handle GIF decoding
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(coil.decode.ImageDecoderDecoder.Factory()) // For API 28 and above
            } else {
                add(coil.decode.GifDecoder.Factory()) // For lower APIs
            }
        }
        .build()

    // Display the GIF using AsyncImagePainter
    androidx.compose.foundation.Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = gifResId) // Use GIF resource ID here
                .apply(block = {
                    size(Size.ORIGINAL) // Preserve original size
                }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,  // To scale the image to fill the available space
        modifier = modifier.fillMaxSize(), // Make it fill the available space
    )
}
