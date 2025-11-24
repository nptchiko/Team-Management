package com.thehecotnha.myapplication.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query

import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.models.Response
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

    // Dung de xac thuc nguoi dung
    private val auth : FirebaseAuth = FirebaseModule.firebaseAuth


    // Dung de call user tu firestore (firestore la service cua firebase de luu tru du lieu)
    private val userRef = FirebaseModule.userCollection


    //tracking du lieu nguoi dung real time
    private val liveFirebaseUser: MutableLiveData<FirebaseUser> by lazy {
        MutableLiveData()
    }


    fun currentUser(): FirebaseUser {
        return auth.currentUser!!
    }

    suspend fun signIn(email: String, password: String): Response<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        liveFirebaseUser.postValue(auth.currentUser)
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
                        liveFirebaseUser.postValue(auth.currentUser)

                        // lay id tu firebase auth
                        val fbUser = it.result?.user
                        user.uid = fbUser!!.uid

                        //push user to firebase
                        userRef.document(fbUser.uid).set(user)
                        Log.d(TAG, "signUp: new user with userId=${user.uid}")
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

    fun getUserByField(field: String, value: String) : Query {
        val query = userRef.whereEqualTo(field, value)
        return query
    }
    /** Get user profile data from Firestore **/
    suspend fun getUserData(): Response<User> {
        val uid: String = currentUser().uid
        Log.d(TAG, "Get User Profile Data for userId=$uid")

        var response : Response<User> = Response.Failure(Exception("No user data found"))

        userRef.document(uid).get()
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    response = Response.Failure(it.exception ?: Exception("Unknown error"))
                    null
                }

                val documentSnapshot = it.result
                val result = documentSnapshot.toObject(User::class.java)

                if (result != null) {
                    Log.d(TAG, "getUserData: successfully got user data for userId=$uid")
                    response = Response.Success(result)
                }
            }.await()

        return response
    }
    fun searchUsers(search: String) : Query {
        val query = userRef.orderBy("searchname")
            .startAt(search.trim().lowercase())
            .endAt(search.trim().lowercase() + "\uf8ff")
        return query
    }

    fun signOut() {
        auth.signOut()
        liveFirebaseUser.postValue(null)
    }
}