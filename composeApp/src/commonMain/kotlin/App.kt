@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import ui.JubilareOverview

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val navController = rememberNavController()
            NavHost(
                navController = navController, startDestination = "home"
            ) {
                composable(route = "home") {
                    HomeScreen(navController)
                }

                composable(route = "jubilare") {
                    JubilareOverview { navController.popBackStack() }
                }
            }
        }
    }
}

@Composable
@Preview
fun HomeScreen(navController: NavController) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text("Home")
        })
    }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("jubilare") }) {
                Text("Jubilare Übersicht")
            }
        }
    }
}
