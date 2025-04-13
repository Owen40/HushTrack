package com.example.hushtrack

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.example.hushtrack.ReportLogic.Report
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

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
    @SuppressLint("SuspiciousIndentation")
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
            val userType = doc.getString("userType")?.lowercase() ?: "client"
            Log.d("FirebaseAuthManager", "Retrieved userType for UID $uid: $userType")
            userType
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Error getting user type: ${e.message}")
            "client"
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

//    Get User Details
    suspend fun getUserDetails(uid: String): Pair<String, String>? {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            val username = doc.getString("username") ?: ""
            val phone = doc.getString("phone") ?: ""
            Pair(username, phone)
         } catch (e: Exception) {
             null
         }
    }

//    Get User Info
    suspend fun getUserInfo(uid: String): Map<String, String>? {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            mapOf(
                "name" to (doc.getString("fullname") ?: ""),
                "email" to (auth.currentUser?.email ?: "")
            )
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Error fetching user info: ${e.message}")
            null
        }
    }

//    Update User Profile
    suspend fun updateUserProfile(uid: String, newUsername: String, newPhone: String): Boolean {
        return try {
            db.collection("suers").document(uid).update(
                mapOf(
                    "username" to newUsername,
                    "phone" to newPhone
                )
            ).await()
            Log.d("FirebaseAuthManager", "Profile updated Successfully for UID: $uid")
            true
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Error updating profile: ${e.message}")
            false
        }
    }

//    Upload Audio Functionality
    suspend fun uploadAudioFile(uid: String, audioFile: File): String {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference.child("audio_reports/$uid/${audioFile.name}")
            val uploadTask = storageRef.putFile(Uri.fromFile(audioFile)).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Error uploading audio: ${e.message}")
            null
            toString()
        }
    }

//    Upload Report Functionality
    suspend fun uploadReport(uid: String, noiseType: String, location: String, description: String, audioUrl: String): Boolean {
        return try {
            val report = hashMapOf(
                "uid" to uid,
                "noiseType" to noiseType,
                "location" to location,
                "description" to description,
                "audioUrl" to audioUrl,
                "timestamp" to Timestamp.now(),
                "status" to "pending Review"
            )
            FirebaseFirestore.getInstance().collection("Reports").add(report).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Error uploading Report: ${e.message}")
            false
        }
    }


//    Listen to Reports
    fun listenToAllReports() {
        val reports = mutableListOf<Report>()
        FirebaseFirestore.getInstance().collection("Reports")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null ) return@addSnapshotListener
                reports.addAll(snapshot.documents.mapNotNull { doc ->
                    val report = doc.toObject(Report::class.java)
                    report?.copy(id = doc.id)
                })
            }
    }

//    Logout Functionality
    fun logoutUser() {
        auth.signOut()
    }
}