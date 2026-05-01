package samu.kiss.myapplication.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun MyScaffold(
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            MyTopAppBar()
        },
        content = { paddingValues ->
            content()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar() {
    TopAppBar(
        title = { Text(text = "My Application") }
    )
}