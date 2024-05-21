package fr.isen.gomez.untilfailure


import android.app.Application
import com.github.mikephil.charting.utils.Utils
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialiser Firebase ici
        FirebaseApp.initializeApp(this)
        Utils.init(this)
    }
}
