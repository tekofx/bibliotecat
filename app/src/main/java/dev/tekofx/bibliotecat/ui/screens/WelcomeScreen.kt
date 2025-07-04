package dev.tekofx.bibliotecat.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.bibliotecat.R
import dev.tekofx.bibliotecat.navigation.NavigateDestinations
import dev.tekofx.bibliotecat.ui.IconResource
import dev.tekofx.bibliotecat.ui.theme.Typography
import dev.tekofx.bibliotecat.ui.viewModels.preferences.PreferencesViewModel
import dev.tekofx.bibliotecat.utils.getAppInfo

@Composable
fun WelcomeScreen(
    navHostController: NavHostController,
    preferencesViewModel: PreferencesViewModel
) {
    val totalPages = 4
    val pagerState = rememberPagerState { totalPages }

    fun navigateToWelcomeScreen() {
        preferencesViewModel.setShowWelcomeScreen(false)
        navHostController.navigate(NavigateDestinations.LOADING_SCREEN)
    }


    Column(
        modifier = Modifier.padding(vertical = 50.dp)
    ) {
        Text(
            text = "Benvingut a ${stringResource(R.string.app_name)}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = Typography.headlineLarge,
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) { page ->
            when (page + 1) {
                1 -> Page1()
                2 -> Page2()
                3 -> Page3()
                4 -> Page4()
                else -> Page1() // Default fallback
            }
        }

        Stepper(pagerState)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularButtonWithProgress(
                pagerState = pagerState,
                onFinishClicked = ::navigateToWelcomeScreen,
            )
        }
    }
}

@Composable
fun Base(
    page: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 50.dp)
    ) {
        Text(
            text = "Benvingut a ${stringResource(R.string.app_name)}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = Typography.headlineLarge,
        )
        page()
    }
}


@Composable
fun CircularButtonWithProgress(
    pagerState: PagerState,
    onFinishClicked: () -> Unit,
) {
    var selectedPage by remember { mutableIntStateOf(0) }
    var clickedButtonOrBack by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPage) {
        if (clickedButtonOrBack) {
            pagerState.animateScrollToPage(selectedPage)
            clickedButtonOrBack = false
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (!clickedButtonOrBack) {
            selectedPage = pagerState.currentPage
        }
    }

    BackHandler {
        if (pagerState.currentPage > 0) {
            clickedButtonOrBack = true
            selectedPage--
        }
    }

    val progress = (pagerState.currentPage + 1) / pagerState.pageCount.toFloat()
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(easing = FastOutSlowInEasing),
        label = "progressAnimation",
    )

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progressAnimation },
            modifier = Modifier.size(60.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
        )
        IconButton(
            onClick = {
                if (pagerState.currentPage == pagerState.pageCount - 1) {
                    onFinishClicked()
                } else {
                    clickedButtonOrBack = true
                    selectedPage++
                }
            },
            modifier = Modifier
                .size(50.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
        ) {
            AnimatedContent(
                targetState = pagerState.currentPage,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ButtonContent"
            ) { targetPage: Int ->
                if (targetPage == pagerState.pageCount - 1) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.Outlined.Check, contentDescription = "Finish"
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Next"
                    )
                }
            }
        }


    }
}

@Composable
fun Stepper(pagerState: PagerState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pagerState.pageCount) {
            Dot(isSelected = pagerState.currentPage == i)
        }
    }
}

@Composable
fun Dot(isSelected: Boolean) {
    val scale by animateDpAsState(
        targetValue = if (isSelected) 15.dp else 10.dp,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "scale"
    )
    val color by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "color"
    )
    Box(
        modifier = Modifier
            .size(scale)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {}
}

@Composable
fun Page1() {
    val context = LocalContext.current
    val appInfo = getAppInfo(context)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 50.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (appInfo != null) {
            Image(
                IconResource.fromDrawableResource(appInfo.icon).asPainterResource(),
                contentDescription = "Image",
                modifier = Modifier
                    .size(200.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun Page2() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${stringResource(R.string.app_name)} us ajudarà a trobar bibliotecat de la Xarxa de bibliotecat Municipals de la Diputació de Barcelona",
            textAlign = TextAlign.Justify,
            style = Typography.titleMedium
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "També podràs veure informació de les bibliotecat, com ara el seu horari",
            textAlign = TextAlign.Justify,
            style = Typography.titleMedium
        )

        Icon(
            IconResource.fromDrawableResource(R.drawable.local_library)
                .asPainterResource(),
            contentDescription = "",
            modifier = Modifier.size(200.dp)
        )

    }
}

@Composable
fun Page3() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Cerca llibres i altres recursos a Aladí amb una interfície d'usuari bonica",
            textAlign = TextAlign.Justify,
            style = Typography.titleMedium
        )
        Icon(
            IconResource.fromDrawableResource(R.drawable.kid_star)
                .asPainterResource(),
            contentDescription = "",
            modifier = Modifier.size(200.dp)
        )
    }
}

@Composable
fun Page4() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Justify,
            text = "Aquesta és una app de codi obert, el codi és públic i qualsevol pot contribuir al seu desenvolupament",
            style = Typography.titleMedium
        )


        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "No és una app oficial de la Diputació de Barcelona",
            style = Typography.titleMedium
        )

        Icon(
            IconResource.fromDrawableResource(R.drawable.data_object)
                .asPainterResource(),
            contentDescription = "",
            modifier = Modifier.size(200.dp)
        )

    }
}


@Preview
@Composable
fun Page1Preview() {
    Surface {
        Base {
            Page1()

        }
    }
}


@Preview
@Composable
fun Page2Preview() {
    Surface {
        Base {
            Page2()

        }
    }
}

@Preview
@Composable
fun Page3Preview() {
    Surface {
        Base {
            Page3()
        }
    }
}

@Preview
@Composable
fun Page4Preview() {
    Surface {
        Base {
            Page4()
        }
    }
}

