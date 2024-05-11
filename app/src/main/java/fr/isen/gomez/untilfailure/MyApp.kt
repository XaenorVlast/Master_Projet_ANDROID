package fr.isen.gomez.untilfailure


import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialiser Firebase ici
        FirebaseApp.initializeApp(this)
    }
}
