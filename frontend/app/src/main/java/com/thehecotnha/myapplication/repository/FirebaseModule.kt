package com.thehecotnha.myapplication.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.thehecotnha.myapplication.utils.FBConstant

/**
 * A simple service locator for providing singleton instances of Firebase services.
 */
object FirebaseModule {

    // Lazy initialization ensures that the instance is created only when it's first accessed.
    val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val userCollection: CollectionReference by lazy {
        firestore.collection(FBConstant.COLLECTION_USER)
    }
    val projectCollection: CollectionReference by lazy {
        firestore.collection(FBConstant.COLLECTION_PROJECT)
    }
    val taskCollection: CollectionReference by lazy {
        firestore.collection(FBConstant.COLLECTION_TASK)
    }
    val notificationCollection: CollectionReference by lazy {
        firestore.collection(FBConstant.COLLECTION_NOTIFICATION)
    }
}
