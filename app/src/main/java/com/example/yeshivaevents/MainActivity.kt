package com.example.yeshivaevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.yeshivaevents.data.EventDatabase
import com.example.yeshivaevents.network.EventApiService
import com.example.yeshivaevents.repository.EventsRepository
import com.example.yeshivaevents.ui.theme.YeshivaEventsTheme
import com.example.yeshivaevents.navigation.AppNavigation
import com.example.yeshivaevents.viewmodel.EventsViewModel
import com.example.yeshivaevents.viewmodel.EventsViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = EventDatabase.getDatabase(applicationContext)
        val dao = database.eventDao()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://serpapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(EventApiService::class.java)

        val repository = EventsRepository(api, dao)
        val factory = EventsViewModelFactory(repository)

        setContent {
            YeshivaEventsTheme {
                val navController = rememberNavController()
                val viewModel: EventsViewModel = viewModel(factory = factory)
                AppNavigation(navController = navController, viewModel = viewModel)
            }
        }
    }
}
