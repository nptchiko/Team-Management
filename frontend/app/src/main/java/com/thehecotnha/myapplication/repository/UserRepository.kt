package com.thehecotnha.myapplication.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.utils.Response
import kotlinx.coroutines.tasks.await
import java.lang.Exception


/**
 * Repository for handling user authentication and data operations with Firebase.
 *
 * This object acts as a singleton to provide a single source of truth for user-related data
 * and interactions with Firebase services.
 */
class UserRepository() {

    private val TAG = "USER_REPOSITORY"

    private val auth : FirebaseAuth = FirebaseModule.firebaseAuth


    // Dung de call user tu firebase
    private val userRef = FirebaseModule.userCollection

    private val firebaseUserLD: MutableLiveData<FirebaseUser> by lazy {
        MutableLiveData()
    }
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun signIn(email: String, password: String): Response<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        firebaseUserLD.postValue(auth.currentUser)
                        Log.d(TAG, "Sign in as ${auth.currentUser?.email}")
                    } else {
                        Log.e(TAG, "Sign in failed")
                    }
                }
                .await()

            Response.Success(authResult.user!!)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    suspend fun signUp(user: User): Response<User> {
        Log.d(TAG, "SIGN UP PROCESSING")
        return try {
            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        firebaseUserLD.postValue(auth.currentUser)

                        // lay id tu firebase auth
                        val fbUser = it.result?.user
                        user.uid = fbUser!!.uid

                        //push user to firebase
                        userRef.document(fbUser.uid).set(user)
                        Log.d(TAG, "signUp: new user with uid=${user.uid}")
                    } else {
                        Log.e(TAG, "signUp: failed")
                    }
                }.await()
            Response.Success(user)
        } catch (e: Exception) {
            // This will catch exceptions from both user creation and Firestore write
            Response.Failure(e)
        }
    }

    /**
     * Signs out the currently authenticated user.
     */
    fun signOut() {
        auth.signOut()
        firebaseUserLD.postValue(null)
    }
}