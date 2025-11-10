package com.thehecotnha.myapplication.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.Task
import com.thehecotnha.myapplication.utils.Response
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.UUID

class ProjectRepository {

    private val TAG = "PROJECT_REPOSITORY"

    private val auth: FirebaseAuth = FirebaseModule.firebaseAuth
    private val projectRef = FirebaseModule.projectCollection

    val _allProjects by lazy {
        projectRef.orderBy("title")
    }
    private val liveFirebaseUser: MutableLiveData<FirebaseUser> by lazy {

        MutableLiveData()
    }

    fun currentUser(): FirebaseUser {
        return auth.currentUser!!
    }

    suspend fun createProject(project: Project): Response<Project> {
        return try {
            // ensure owner set
            project.ownerId = currentUser().uid
            project.teams.add(currentUser().uid)
            // create new doc with generated id
            val docRef = projectRef.document()
            project.id = docRef.id
            docRef.set(project)
                .addOnSuccessListener{
                    Log.d(TAG, "createProject: created id=${project.id}")
                }
                .await()

            Response.Success(project)
        } catch (e: Exception) {
            Log.e(TAG, "createProject: failed", e)
            Response.Failure(e)
        }
    }

    fun getUserProjects(userId: String): Query {
        return projectRef.whereArrayContains("teams", userId)
    }

    suspend fun updateProject(project: Project): Response<Project> {
        return try {
            val id = project.id ?: return Response.Failure(Exception("Project id is null"))
            projectRef.document(id).set(project)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "updateProject: updated id=$id")
                    } else {
                        Log.e(TAG, "updateProject: failed", task.exception)
                    }
                }.await()

            Response.Success(project)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    suspend fun deleteProject(projectId: String): Response<Boolean> {
        return try {
            projectRef.document(projectId).delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "deleteProject: deleted id=$projectId")
                    } else {
                        Log.e(TAG, "deleteProject: failed", task.exception)
                    }
                }.await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    suspend fun listProjectsForCurrentUser(): Response<List<Project>> {
        return try {
            val uid = currentUser().uid
            var resultList: List<Project> = emptyList()

            projectRef.whereEqualTo("ownerId", uid).get()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e(TAG, "listProjectsForCurrentUser: failed", task.exception)
                        return@addOnCompleteListener
                    }
                    val snap = task.result
                    resultList = snap?.documents?.mapNotNull { it.toObject(Project::class.java) } ?: emptyList()
                    Log.d(TAG, "listProjectsForCurrentUser: found ${resultList.size} projects")
                }.await()

            Response.Success(resultList)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

// ========== TASK RELATED METHODS CAN BEADDED HERE ==========
    suspend fun saveTask(task: Task) : Response<Void> {
        return try {
            task.id = UUID.randomUUID().toString()
            projectRef
                .document(task.projectId)
                .collection("tasksAffected")
                .document(task.id)
                .set(task)
                .addOnSuccessListener {
                    Log.d(TAG, "saveTask: saved task id=${task.id} for projectId=${task.projectId}")
                }
                .await()

            Response.Success(null)
        } catch (e: Exception) {
            Log.e(TAG, "saveTask: failed to save task id=${task.id} for projectId=${task.projectId}", e)
            Response.Failure(e)
        }
    }


     fun getTaskFilted(projectId: String, filterName: String): Query {
        val query = projectRef.document(projectId)
            .collection("tasksAffected")
        return query
/*        return if (filterName.isNotEmpty()) {
            query.whereEqualTo("state", filterName)
        } else {
            query
        }*/
    }
}