package mobappdev.example.nback_cimpl.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.modifier.modifierLocalConsumer
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
import mobappdev.example.nback_cimpl.ui.viewmodels.GameState
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import java.time.format.TextStyle

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
fun ImageScreen(
    vm: GameViewModel,
    //navController: NavController
) {
    val gameState by vm.gameState.collectAsState()
    val score by vm.score.collectAsState()
    val buttonColor by vm.buttonColor.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember{SnackbarHostState()}
    var gridSize = 3
    val gridItems = remember {
        Array(gridSize * gridSize) { index ->
            GridItem()
        }
    }

    (vm::initializerOn)()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState)}
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {

            Text(
                text = "Score = $score",
                style = MaterialTheme.typography.headlineSmall
            )

            Box(
                modifier = Modifier
                    //.fillMaxSize()
                    .aspectRatio(1f)
            ) {
                Column(
                    Modifier
                        .fillMaxHeight(),
                    horizontalAlignment =
                    Alignment.CenterHorizontally,
                    verticalArrangement =
                    Arrangement.SpaceAround

                ) {
                    Text("Current event is: ${gameState.curentEventValue}/10")
                    if (gameState.eventValue != -1) {

                        for (row in 0 until gridSize) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (col in 0 until gridSize) {
                                    val index = row * gridSize + col

                                    if (index + 1 == gameState.eventValue) {
                                        gridItems[index].ChangeColor(Color.Blue)

                                    } else {
                                        gridItems[index].ChangeColor(Color.DarkGray)
                                    }
                                    gridItems[index].DrawBox(index)
                                }
                            }
                        }


                    } else {
                        for (row in 0 until gridSize) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (col in 0 until gridSize) {
                                    val index = row * gridSize + col
                                    gridItems[index].ChangeColor(Color.DarkGray)
                                    gridItems[index].DrawBox(index)

                                }
                            }
                        }
                    }
                }
            }
            if(gameState.eventValue == -1){
                Button(onClick = { (vm::startGame)(context) } ) {

                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = "Start Game",
                        style = MaterialTheme.typography.displaySmall
                    )

                }
            } else {
                Text(text = "Press when you see a match:")

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


data class GridItem(
    var color: Color = Color.Blue,
    var isSelected: Boolean = false,
    var isMatched: Boolean = false
) {
    @Composable
    fun DrawBox(index: Int) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(10.dp)
                )

        ) {}
    }

    fun ChangeColor(color: Color){
        this.color = color
    }
}


@Preview
@Composable
fun ImageScreenPreview() {
    // Since I am injecting a VM into my homescreen that depends on Application context, the preview doesn't work.
    Surface(){
        ImageScreen(FakeVM()) //, rememberNavController())
    }
}