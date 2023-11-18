package mobappdev.example.nback_cimpl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
    navController: NavController
) {
    val highscore by vm.highscore.collectAsState()  // Highscore is its own StateFlow
    val gameState by vm.gameState.collectAsState()
    val score by vm.score.collectAsState()
    val scope = rememberCoroutineScope()
    var isButtonPressed = remember { mutableStateOf(false) }
    var gridSize = 3
    val gridItems = remember {
        Array(gridSize * gridSize) { index ->
            GridItem()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Score = $score",
                style = MaterialTheme.typography.headlineSmall
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (gameState.eventValue != -1) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Current eventValue is: ${gameState.eventValue}",
                            textAlign = TextAlign.Center
                        )
                    }

                    for (row in 0 until gridSize) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
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

                    Button(onClick = vm::startGame) {
                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = "Start Game",
                            //style = MaterialTheme.typography.displaySmall
                        )

                    }
                    Button(
                        onClick = {
                            vm.checkMatch()
                            isButtonPressed.value = false
                        }
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
                .padding(10.dp),
            contentAlignment = Alignment.Center
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
        HomeScreen(FakeVM(), rememberNavController())
    }
}