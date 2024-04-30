package com.example.jetpack_test.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jetpack_test.db.MainDb
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jetpack_test.data.WeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import androidx.navigation.compose.rememberNavController as rememberNavController

const val API_KEY = "b9be71d2e84d4f41813170758242704"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            this,
            MainDb::class.java,
            "test.db"
        ).build()
        val userDao = db.userDao()



        setContent {
            val currentDay = remember {
                mutableStateOf(
                    WeatherModel(
                        //Для начала заполним пустой объект WeatherModel, а после заполнится само в getWeatherByDay()
                        "",
                        "",
                        "0.0",
                        "",
                        "",
                        "0.0",
                        "0.0"
                    )
                )
            }


            val dialogState= remember{
                mutableStateOf(false)
            }
            if (dialogState.value){
                DialogSearch(dialogState, onSubmit = {
                    getData(it, this, currentDay)
                })
            }


            getData("Kazan", this, currentDay)

            // Создание NavHost с использованием NavController и начального маршрута
            val navController = rememberNavController()


            NavHost(
                navController = navController,
                startDestination = "SignInUpScreen"
            ) {

                composable("SignInUpScreen") {
                    SignInUpScreen(
                        userDao,
                        this@MainActivity,
                        navController = navController
                    )
                }

                composable("WeatherScreen") {
                    WeatherScreen(currentDay, onClickSync = {
                        getData("Kazan", this@MainActivity, currentDay)
                    }, onClickSearch = {
                        dialogState.value = true
                    })
                }
            }

        }
    }
}


//библиотека volley нужна для упрощения и ускорения работы с сетью в Android-приложениях,
// предоставляя разработчикам удобный API и автоматизируя многие рутинные задачи.
//Метод для получения response(JSONObject)
private fun getData(city: String, context: Context, currentDay: MutableState<WeatherModel>) {
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
            "&q=$city" +
            "&days=" +
            "3" +
            "&aqi=no&alerts=no"

    //Очередь запроса
    val queue = Volley.newRequestQueue(context)
    //Сам запрос
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val list = getWeatherByDay(response)
            currentDay.value = list
            Log.d("Mylog", "VolleyResponse: $response")
        },
        {
            Log.d("Mylog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}


private fun getWeatherByDay(response: String): WeatherModel {
    if (response.isEmpty()) return WeatherModel(
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )

    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    val item = days[0] as JSONObject


    return WeatherModel(
        city,
        mainObject.getJSONObject("current").getString("last_updated"),
        mainObject.getJSONObject("current").getString("temp_c"),
        mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
        mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
        item.getJSONObject("day").getString("maxtemp_c"),
        item.getJSONObject("day").getString("mintemp_c")

    )




}

