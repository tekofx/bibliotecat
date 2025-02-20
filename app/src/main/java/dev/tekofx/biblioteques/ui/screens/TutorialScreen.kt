package dev.tekofx.biblioteques.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.ColumnContainer
import dev.tekofx.biblioteques.ui.components.input.TextIconButton
import dev.tekofx.biblioteques.ui.theme.Typography
import dev.tekofx.biblioteques.ui.viewModels.preferences.PreferencesViewModel
import dev.tekofx.biblioteques.utils.RequestLocationPermissionUsingRememberLauncherForActivityResult

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TutorialScreen(
    navHostController: NavHostController?,
    preferencesViewModel: PreferencesViewModel?
) {
    val totalPages = 3
    val pagerState = rememberPagerState { totalPages }

    fun navigateToWelcomeScreen() {
        preferencesViewModel?.saveShowTutorial(false)
        navHostController?.navigate(NavigateDestinations.WELCOME_SCREEN)
    }


    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            TextButton(
                onClick = { navigateToWelcomeScreen() }
            ) {
                Text("Skip")
            }
        }

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
        Spacer(modifier = Modifier.height(20.dp))
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
    ColumnContainer {
        Text(
            text = "Benvingut a ${stringResource(R.string.app_name)}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = Typography.headlineLarge,
        )

        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            modifier = Modifier.size(300.dp),
            contentDescription = ""
        )
    }
}


@Composable
fun Page2() {
    ColumnContainer {
        Text(
            text = "Característiques",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = Typography.headlineLarge,
        )

        val bullet = "\u2022"
        val messages = listOf(
            "Veure biblioteques y filtrar de la província de Barcelona",
            "Consulta informació sobre una biblioteca, com ara horari o ubicació",
            "Cerca llibres i altres recursos a Aladí amb una interfície d'usuari bonica",
        )


        messages.forEach {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "$bullet\t $it",
                textAlign = TextAlign.Justify,
                style = Typography.bodyLarge
            )
        }


    }
}

@Composable
fun Page3() {
    val context = LocalContext.current

    ColumnContainer {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Sobre aquesta aplicació",
            textAlign = TextAlign.Center,
            style = Typography.headlineLarge
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Aquesta és una app de codi obert, no és oficial de la Diputació de Barcelona"
        )

        TextIconButton(
            text = "Codí font",
            onClick = {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tekofx/Biblioteques"))
                context.startActivity(intent)
            },
            startIcon = IconResource.fromDrawableResource(R.drawable.github_mark)
        )

    }
}




@Preview
@Composable
fun Page1Preview() {
    Surface {
        Page1()
    }
}


@Preview
@Composable
fun Page2Preview() {
    Surface {
        Page2()
    }
}

@Preview
@Composable
fun Page3Preview() {
    Surface {
        Page3()
    }
}

