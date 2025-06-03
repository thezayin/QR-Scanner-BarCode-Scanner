package com.thezayin.qrscanner.ui.onboarding

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val imageResId: Int, val titleResId: Int, val subtitleResId: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        imageResId = R.drawable.vid_qr,
        titleResId = R.string.qr_code_scanner,
        subtitleResId = R.string.qr_code_details
    ), OnboardingPage(
        imageResId = R.drawable.vid_bar,
        titleResId = R.string.barcode_scanner,
        subtitleResId = R.string.barcode_details
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLanguage: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) { pageIndex ->
            OnboardingPageContent(page = onboardingPages[pageIndex], imageLoader = imageLoader)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.sdp)
                .padding(bottom = 24.sdp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage < onboardingPages.size - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onNavigateToLanguage()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.sdp),
                shape = RoundedCornerShape(16.sdp)
            ) {
                Text(
                    text = "Continue",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.ssp
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.sdp)
                )
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage, imageLoader: ImageLoader
) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.sdp, max = 600.sdp)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = page.imageResId,
                contentDescription = null,
                imageLoader = imageLoader,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillBounds
            )
        }
        Spacer(modifier = Modifier.height(10.sdp))
        Text(
            text = stringResource(id = page.titleResId),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.sdp))

        Text(
            text = stringResource(id = page.subtitleResId),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.sdp))
    }
}