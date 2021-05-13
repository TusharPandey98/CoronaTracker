package com.example.coronatracker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class RequestApiData(var context: Context)
{
    var myContext = this.context

    var activeCases = ""
    var recoveredCases = ""
    var fatalCases = ""
    var lastUpdate = ""
    var chosenCountry = ""

    fun requestData(country:String){
        //Coroutines scope -Main Thread
        val myScope = CoroutineScope(Dispatchers.Main)

        //IO thread
        myScope.launch {
            val responseArray:ArrayList<String> = ArrayList(5)

            withContext(Dispatchers.IO){
                val myCountry = country
                val apiURL =
                    ("https://covid-19-coronavirus-statistics.p.rapidapi.com/v1/stats"
                            + "?country=" + myCountry)
                val queue = Volley.newRequestQueue(myContext)

                val getRequest: StringRequest = object : StringRequest(
                    Method.GET,
                    apiURL,
                    Response.Listener { response ->
                        if (response.isNotEmpty()) {
                            try {
                                val jsonObject = JSONObject(response)
                                val data = jsonObject.getJSONObject("data")
                                val myArray = data.getJSONArray("covid19Stats")

                                Log.d("requestData", response)
                                for (i in 0 until myArray.length()) {
                                    val country: JSONObject = myArray.getJSONObject(i)

                                    activeCases = country.getString("confirmed")
                                    recoveredCases = country.getString("recovered")
                                    fatalCases = country.getString("deaths")
                                    lastUpdate = country.getString("lastUpdate")
                                    chosenCountry = country.getString("keyId")

                                    responseArray.add(activeCases)
                                    responseArray.add(recoveredCases)
                                    responseArray.add(fatalCases)
                                    responseArray.add(lastUpdate)
                                    responseArray.add(chosenCountry)

                                }
                                val intent = Intent(myContext, HomeActivity::class.java)
                                intent.putExtra("active", activeCases)
                                intent.putExtra("recovered", recoveredCases)
                                intent.putExtra("fatal", fatalCases)
                                intent.putExtra("lastUpdate", lastUpdate)
                                intent.putExtra("country", chosenCountry)
                                startActivity(myContext, intent, null)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            Log.d("requestData", "Empty")
                        }
                    },
                    Response.ErrorListener {
                        Log.d("requestData", "Error")
                        Log.d("requestData", it.toString())
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val params =
                            HashMap<String, String>()
                        params["x-rapidapi-key"] = BuildConfig.ApiKey
                        return params
                    }
                }
                queue.add(getRequest)
            }
        }
    }

}
