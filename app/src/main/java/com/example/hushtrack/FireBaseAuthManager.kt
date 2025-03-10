package com.example.hushtrack

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FireBaseAuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

//    Register User
    suspend fun registerUser(email: String, password: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (e: Exception) {
            null
        }
    }

//    Store onboarding Data
    suspend fun saveUserData(uid: String, username: String, fullname: String, phone: String) {
        val user = hashMapOf(
            "username" to username,
            "fullname" to fullname,
            "phone" to phone,
            "userType" to "client"
        )
            db.collection("users").document(uid).set(user).await()
    }

//    Login User
    suspend fun loginUser(email: String, password: String): String? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (e: Exception) {
            null
        }
    }

//    Get User Type
    suspend fun getUserType(uid: String): String? {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.getString("UserType")
        } catch (e: Exception) {
            null
        }
    }

//    Get Username for Welcome Message
    suspend fun getUsername(uid: String): String? {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.getString("username")
        } catch (e: Exception) {
            null
        }
    }
}