package dev.tekofx.biblioteques.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.navigation.NavigateDestinations
import dev.tekofx.biblioteques.ui.ColumnContainer
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.components.input.TextIconButton
import dev.tekofx.biblioteques.ui.theme.Typography

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TutorialScreen(
    navHostController: NavHostController
) {

    var page by remember { mutableIntStateOf(0) }


    val lastPage = 3
    Column {

        when (page) {
            0 -> Screen1()
            1 -> Screen2()
            2 -> Screen3()
            3 -> Screen4()
        }

        // Spacer with weight to push the next elements to the bottom
        Spacer(modifier = Modifier.weight(1f))

        Buttons(
            page = page,
            lastPage = lastPage,
            onFinishClicked = { navHostController.navigate(NavigateDestinations.WELCOME_SCREEN) },
            decreasePage = { page -= 1 },
            increasePage = { page += 1 }
        )


    }
}


@Composable
fun Screen1() {
    ColumnContainer {
        Text(
            text = "Welcome to ${stringResource(R.string.app_name)}",
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
fun Screen2() {
    ColumnContainer {
        Text(
            text = "Features",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = Typography.headlineLarge,
        )

        val bullet = "\u2022"
        val messages = listOf(
            "See libraries of Barcelona province",
            "Filter libraries by Open/Close, name or municipality",
            "See Information about a library",
            "Search books in Aladi with a beautiful UI",
        )
        val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
        Text(
            style = Typography.bodyLarge,
            text = buildAnnotatedString {
                messages.forEach {
                    withStyle(style = paragraphStyle) {
                        append(bullet)
                        append("\t\t")
                        append(it)
                    }
                }
            }
        )


    }
}

@Composable
fun Screen3() {
    ColumnContainer {
        Text(
            text = "About this app",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = Typography.headlineLarge
        )
        Text("This app is not official")
        Text("This app is Open Source, check out the code here")
    }
}

@Composable
fun Screen4() {
    ColumnContainer {
        Text(
            text = "Permissions",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = Typography.headlineLarge,
        )

        Text("This app will need location access if you want to find libraries near you")
    }
}


@Composable
fun Buttons(
    page: Int,
    lastPage: Int,
    onFinishClicked: () -> Unit,
    increasePage: () -> Unit,
    decreasePage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(
            space = 0.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {

        AnimatedVisibility(
            visible = page != 0,
            enter = scaleIn() + expandHorizontally(),
            exit = scaleOut() + shrinkHorizontally()
        ) {
            TextIconButton(
                text = "Enrere",
                icon = IconResource.fromImageVector(Icons.AutoMirrored.Outlined.KeyboardArrowLeft),
                enabled = page > 0,
                onClick = decreasePage
            )
        }
        AnimatedVisibility(
            visible = page != 0,
            enter = scaleIn() + expandHorizontally(),
            exit = scaleOut() + shrinkHorizontally()
        ) {
            Spacer(modifier = Modifier.width(40.dp))
        }


        AnimatedContent(
            targetState = page,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                    animationSpec = tween(
                        300
                    )
                )
            },
            label = ""
        ) { targetState ->
            if (targetState == lastPage) {
                TextIconButton(
                    text = "Acabar",
                    icon = IconResource.fromImageVector(Icons.Outlined.Check),
                    onClick = onFinishClicked
                )
            } else {
                TextIconButton(
                    text = "Endavant",
                    icon = IconResource.fromImageVector(Icons.AutoMirrored.Outlined.KeyboardArrowRight),
                    onClick = increasePage
                )
            }
        }

    }
}