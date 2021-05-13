package com.example.coronatracker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var activeCases = ""
    var recoveredCases = ""
    var fatalCases = ""
    var lastUpdate = ""
    var chosenCountry = ""

    private lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Autocomplete Text
        val countryList = resources.getStringArray(R.array.countries)
        val arrayAdapter =
            ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,countryList)
        autoCompleteTextView.setAdapter(arrayAdapter)

        //Button Click listener
        button.setOnClickListener(View.OnClickListener {
            if (validInput() && isNetworkAvailable()){
                //loading dialog
                loadingDialog = LoadingDialog(activity = this)
                loadingDialog.startLoadingDialog()

                //request data from api
                val myCountry = autoCompleteTextView.text.toString().trim().capitalize(Locale.ROOT)

                val requestApiData = RequestApiData(this)
                requestApiData.requestData(myCountry)

            }else if (!isNetworkAvailable()){
                snackBarMessage("No Internet Connection")
            }else{
                snackBarMessage("Invalid Country")
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        return try{
            val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var networkInfo:NetworkInfo? = null
            networkInfo = manager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }catch (e:NullPointerException){
            false
        }
    }

    private fun validInput(): Boolean {
        val countriesList = resources.getStringArray(R.array.countries).asList()
        val myCountry = autoCompleteTextView.text.toString()

        val validCountry = countriesList.find { it == myCountry.trim().capitalize(Locale.ROOT) }
        return validCountry !=null
    }

    //Create Snackbar Message
    private fun snackBarMessage(message: String) {
        val snackbar = Snackbar.make(button, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(resources.getColor(R.color.lighter))
        snackbar.setAction("Try again", View.OnClickListener { })

        val snackbarView = snackbar.view
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(resources.getColor(R.color.white))
        textView.textSize = 16f
        snackbar.show()
    }
}