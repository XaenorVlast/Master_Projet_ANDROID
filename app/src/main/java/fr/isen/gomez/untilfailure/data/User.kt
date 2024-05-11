package fr.isen.gomez.untilfailure.data


data class User(
    val nom: String,
    val prenom: String,
    val age: String,
    val objectif: String,
    val email: String,
    val password: String // Comme mentionné précédemment, évitez de stocker le mot de passe ici si possible.
)