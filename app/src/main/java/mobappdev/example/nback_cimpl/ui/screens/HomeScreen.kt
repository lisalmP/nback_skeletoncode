package mobappdev.example.nback_cimpl.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.theme.BlueGrey80
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

/**
 * This is the Home screen composable
 *
 * Currently this screen shows the saved highscore
 * It also contains a button which can be used to show that the C-integration works
 * Furthermore it contains two buttons that you can use to start a game
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */
@Composable
fun HomeScreen(
    vm: GameViewModel,
    navController: NavController
) {
    val configuration = LocalConfiguration.current

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LandscapeHomeScreen(vm = vm, navController = navController)
    } else {
        PortraitHomeScreen(vm = vm, navController = navController)
    }
}
@Composable
fun PortraitHomeScreen(
    vm: GameViewModel,
    navController: NavController
) {
    val highscore by vm.highscore.collectAsState()  // Highscore is its own StateFlow
    //val gameState by vm.gameState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    //val scope = rememberCoroutineScope()

    (vm::initializerOff)()
    (vm::resetCurrentEventValue)()
    (vm::resetEventValue)()



    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(32.dp),
                text = "Welcome to N-Back!\uD83E\uDDE0",
                style = MaterialTheme.typography.headlineLarge
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        text = "High-Score = $highscore".uppercase(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "Current settings are:\nNumber of events: 10\nN-back: 2\nTime: 2s",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge.copy(color = BlueGrey80)
                    )
                }
            }
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Choose your game:".uppercase(),
                style = MaterialTheme.typography.headlineMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    (vm::setGameType)(GameType.Audio)
                    navController.navigate("sound")
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.sound_on),
                        contentDescription = "Sound",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
                Button(
                    onClick = {
                        vm.setGameType(GameType.Visual)
                        navController.navigate("image")
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.visual),
                        contentDescription = "Visual",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
            }
        }
    }
}


@Composable
fun LandscapeHomeScreen(
    vm: GameViewModel,
    navController: NavController
) {
    val highscore by vm.highscore.collectAsState()  // Highscore is its own StateFlow
    //val gameState by vm.gameState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    //val scope = rememberCoroutineScope()

    (vm::initializerOff)()
    (vm::resetCurrentEventValue)()
    (vm::resetEventValue)()



    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Welcome to N-Back!\uD83E\uDDE0",
                style = MaterialTheme.typography.headlineSmall
            )

                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        text = "High-Score = $highscore".uppercase(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "Current settings are:\nNumber of events: 10\nN-back: 2\nTime: 2s",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall.copy(color = BlueGrey80)
                    )
                }

            Text(
                modifier = Modifier.padding(16.dp),
                text = "Choose your game:".uppercase(),
                style = MaterialTheme.typography.headlineSmall
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement
                    .SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    (vm::setGameType)(GameType.Audio)
                    navController.navigate("sound")
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.sound_on),
                        contentDescription = "Sound",
                        modifier = Modifier
                            .height(38.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
                Button(
                    onClick = {
                        vm.setGameType(GameType.Visual)
                        navController.navigate("image")
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.visual),
                        contentDescription = "Visual",
                        modifier = Modifier
                            .height(38.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
            }
        }
    }
}
/*
@Preview()
@Composable
fun LandscapePreview() {
    // Since I am injecting a VM into my homescreen that depends on Application context, the preview doesn't work.
    Surface(
        modifier = Modifier
    ){
        LandscapeHomeScreen(vm = FakeVM(), rememberNavController())
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    // Since I am injecting a VM into my homescreen that depends on Application context, the preview doesn't work.
    Surface(){
        HomeScreen(FakeVM(), rememberNavController())
    }
}*/