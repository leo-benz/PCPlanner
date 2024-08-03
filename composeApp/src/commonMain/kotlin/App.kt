@file:OptIn(KoinExperimentalAPI::class)

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import model.Jubilar
import viewmodel.JubilareViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

import pcplanner.composeapp.generated.resources.Res
import pcplanner.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            NavHost(
                navController = rememberNavController(),
                startDestination = "home"
            ) {
                composable(route = "home") {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
@Preview
fun HomeScreen() {
    val viewModel = koinViewModel<JubilareViewModel>()
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { viewModel.insertRandomJubilar() }) {
            Text("New Jubilar")
        }
            val greeting = remember { Greeting().greet() }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
                Text("Koin: ${viewModel.greet()}")
            }

        val jubilare by viewModel.getJubilare().collectAsState(initial = emptyList())
        jubilare.forEach {
            JubilarCard(jubilar = it)
        }
    }
}

@Composable
fun JubilarCard(jubilar: Jubilar) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Name: ${jubilar.firstName} ${jubilar.lastName}")
        Text("Birthdate: ${jubilar.birthdate}")
    }
}
