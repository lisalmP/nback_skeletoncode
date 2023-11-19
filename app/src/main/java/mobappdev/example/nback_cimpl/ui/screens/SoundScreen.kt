package mobappdev.example.nback_cimpl.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
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
fun SoundScreen(
    vm: GameViewModel,
    navController: NavController
) {
    val gameState by vm.gameState.collectAsState()
    val score by vm.score.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val buttonColor by vm.buttonColor.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    (vm::initializerOn)()


    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Score = $score",
                style = MaterialTheme.typography.headlineSmall
            )
            Text("Current event is: ${gameState.curentEventValue}/10")

            if(gameState.eventValue == -1){
                Button(onClick = { (vm::startGame)(context) }) {

                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = "Start Game",
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            } else {
                Text(text = "Press when you hear a match:")

                Button(
                    onClick = {
                        (vm::checkMatch)()
                    },
                    modifier = Modifier
                        .background(buttonColor, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                    //.background(buttonColor)

                ) {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = "Match!".uppercase(),
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }
        }
    }
    }



@Preview
@Composable
fun SoundScreenPreview() {
    // Since I am injecting a VM into my homescreen that depends on Application context, the preview doesn't work.
    Surface(){
        SoundScreen(FakeVM(), rememberNavController())
    }
}