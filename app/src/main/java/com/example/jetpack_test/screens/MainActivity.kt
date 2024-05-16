package com.example.jetpack_test.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import kotlin.random.Random
import androidx.glance.GlanceId
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jetpack_test.db.MainDb
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jetpack_test.data.WeatherModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID
import androidx.navigation.compose.rememberNavController as rememberNavController

const val API_KEY = "b9be71d2e84d4f41813170758242704"

class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 123
    }
    private var locationCity= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            this,
            MainDb::class.java,
            "test.db"
        ).build()
        val userDao = db.userDao()




                // Проверка разрешений
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Запрос разрешений
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
            return
        }
// Получение геолокации и названия города
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f) { location ->
            val latitude = location.latitude
            val longitude = location.longitude
            val geocoder = Geocoder(this)
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val cityName = addresses[0].locality
                // Сохранение названия города в переменной locationCity
                locationCity = cityName
            } else {
                // Обработка случая, когда список addresses пустой или null
                Log.e("Error", "No address found")
            }
        }




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


            getData("Kazan",  this, currentDay)

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
                    WeatherScreen(currentDay, locationCity, onClickSync = {
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
class MyGlanceId(private val id: String) : GlanceId {
    fun getId(): String {
        return id
    }
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



