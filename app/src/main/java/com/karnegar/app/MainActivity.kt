package com.karnegar.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karnegar.app.ui.screens.AddEditTaskScreen
import com.karnegar.app.ui.screens.SettingsScreen
import com.karnegar.app.ui.screens.TaskListScreen
import com.karnegar.app.ui.theme.KarNegarTheme
import com.karnegar.app.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()

        setContent {
            val viewModel: TaskViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            KarNegarTheme(themeMode = themeMode) {
                KarNegarNavGraph(viewModel)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }
}

@Composable
fun KarNegarNavGraph(viewModel: TaskViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            TaskListScreen(
                viewModel = viewModel,
                onAddTask = { navController.navigate("edit/0") },
                onEditTask = { id -> navController.navigate("edit/$id") },
                onOpenSettings = { navController.navigate("settings") }
            )
        }
        composable("edit/{taskId}") { entry ->
            val taskId = entry.arguments?.getString("taskId")?.toLongOrNull() ?: 0L
            AddEditTaskScreen(
                viewModel = viewModel,
                taskId = taskId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
