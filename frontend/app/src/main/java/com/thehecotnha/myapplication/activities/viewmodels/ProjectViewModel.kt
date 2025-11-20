package com.thehecotnha.myapplication.activities.viewmodels

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.Task
import com.thehecotnha.myapplication.repository.ProjectRepository
import com.thehecotnha.myapplication.repository.UserRepository
import com.thehecotnha.myapplication.models.Response
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.repository.NotificationRepository
import com.thehecotnha.myapplication.utils.removeTime
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class ProjectViewModel : ViewModel() {

    private val projectRepo = ProjectRepository()
    private val userRepo = UserRepository()

    private val notificationRepo = NotificationRepository()

    val _allProjects = MutableLiveData<List<Project>?>()

    val _projectTask = MutableLiveData<List<Task>?>()

    val _taskState = MutableLiveData<Response<Void>>(Response.Idle)

    val _allTasks = MutableLiveData<List<Task>>()

    val _teamProject = MutableLiveData<List<User>>()
    fun getUserProjects() {
        val userId = userRepo.currentUser().uid

        projectRepo.getUserProjects(userId).addSnapshotListener { value, error ->
            val result = value?.toObjects(Project::class.java)
            _allProjects.postValue(result)
        }
    }

// Add these methods to your existing ProjectViewModel class

    /**
     * Add user to project and send notification
     */
    fun addUserToProject(projectId: String, userId: String, projectName: String) = viewModelScope.launch {
        _taskState.value = Response.Loading

        try {
            // Get current user's name
            val currentUser = userRepo.currentUser()
            val currentUserData = userRepo.getUserData()
            val senderName = if (currentUserData is Response.Success) {
                currentUserData.data?.username ?: "Someone"
            } else {
                "Someone"
            }

            // Add user to project
            val projectRef = projectRepo.projectCollection.document(projectId)
            projectRef.get().await().let { snapshot ->
                val project = snapshot.toObject(Project::class.java)
                if (project != null) {
                    if (!project.teams.contains(userId)) {
                        project.teams.add(userId)
                        projectRef.set(project).await()

                        // Send notification
                        notificationRepo.notifyUserAddedToProject(
                            userId = userId,
                            projectName = projectName,
                            senderName = senderName
                        )
                    }
                }
            }

            _taskState.value = Response.Success(null)
        } catch (e: Exception) {
            _taskState.value = Response.Failure(e)
        }
    }

    /**
     * Remove user from project and send notification
     */
    fun removeUserFromProject(projectId: String, userId: String, projectName: String) = viewModelScope.launch {
        _taskState.value = Response.Loading

        try {
            // Get current user's name
            val currentUserData = userRepo.getUserData()
            val senderName = if (currentUserData is Response.Success) {
                currentUserData.data?.username ?: "Someone"
            } else {
                "Someone"
            }

            // Remove user from project
            val projectRef = projectRepo.projectCollection.document(projectId)
            projectRef.get().await().let { snapshot ->
                val project = snapshot.toObject(Project::class.java)
                if (project != null) {
                    if (project.teams.contains(userId)) {
                        project.teams.remove(userId)
                        projectRef.set(project).await()

                        // Send notification
                        notificationRepo.notifyUserRemovedFromProject(
                            userId = userId,
                            projectName = projectName,
                            senderName = senderName
                        )
                    }
                }
            }

            _taskState.value = Response.Success(null)
        } catch (e: Exception) {
            _taskState.value = Response.Failure(e)
        }
    }

    fun createProject(project: Project) = viewModelScope.launch {
        projectRepo.createProject(project)
    }

    fun getTasksByFilter(context: Context,
                         projectId: String,
                         name: String) {

        val filterName = when (name) {
            context.getString(R.string.state_todo) -> "TODO"
            context.getString(R.string.state_progress) -> "IN PROGRESS"
            context.getString(R.string.state_completed) -> "DONE"
            else -> ""
        }

        projectRepo.getTaskFilted(projectId, filterName)
            .addSnapshotListener { value, error ->
            if (value != null){
                val result = value.toObjects(Task::class.java)
                Toast.makeText(context, "Found ${result.size} tasks", Toast.LENGTH_SHORT).show()
                _projectTask.postValue(result)
            } else {
                _projectTask.postValue(emptyList())
                Log.e("PROJECT_VIEW_MODEL", "Error getting tasks: ${error?.message}")

            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun getTaskForDay(date: Date, includeDaysAfter: Boolean = false) {
        val userId = userRepo.currentUser().uid


        projectRepo._allTasks.addSnapshotListener { value, error ->

            if (value != null) {
                val resultList: List<Task> = value.toObjects(Task::class.java)
                val filterResult = resultList.stream()
                    //remove
                    .filter {
                        if (!includeDaysAfter) {
                            removeTime(it.endDate?.toDate() ?: return@filter false) ==
                                    removeTime(date)
                        } else {
                            removeTime(it.endDate?.toDate() ?: return@filter false) >=
                                    removeTime(date)
                        }
                    }
                    .filter {
                        if (it.assignedTo.isNotEmpty())
                            it.assignedTo[0] == userId
                        else
                            false

                    }
                    .toList()
                Log.d("PROJECT_VIEW_MODEL", "Found ${filterResult.size} tasks for the day")
                _projectTask.postValue(filterResult)
            } else {
                _projectTask.postValue(emptyList())
                Log.e("PROJECT_VIEW_MODEL", "Error getting tasks for day: ${error?.message}")
            }
        }
    }


    fun saveNewTask(task: Task) = viewModelScope.launch {
        _taskState.value = Response.Loading

        try {
            // Save task
            val result = projectRepo.saveTask(task)

            if (result is Response.Success) {
                // Get current user's name
                val currentUserData = userRepo.getUserData()
                val senderName = if (currentUserData is Response.Success) {
                    currentUserData.data?.username ?: "Someone"
                } else {
                    "Someone"
                }

                // Send notifications to all assigned users
                task.assignedTo.forEach { userId ->
                    notificationRepo.notifyUserAssignedToTask(
                        userId = userId,
                        taskName = task.title,
                        projectName = task.projectName,
                        senderName = senderName
                    )
                }
            }
            _taskState.value = result
        } catch (e: Exception) {
            _taskState.value = Response.Failure(e)
        }
    }
        fun updateTask(task: Task, originalAssignees: List<String>) = viewModelScope.launch {
            task.updatedBy = userRepo.currentUser().uid
            task.updatedAt = Timestamp.now()
            _taskState.value = Response.Loading

            try {
                // Update task
                projectRepo.updateTask(task)

                // Get current user's name
                val currentUserData = userRepo.getUserData()
                val senderName = if (currentUserData is Response.Success) {
                    currentUserData.data?.username ?: "Someone"
                } else {
                    "Someone"
                }
                // Find newly assigned users

                // Send notifications to new assignees

                val newAssignee = task.assignedTo[0]
                val isNewAssignee = originalAssignees[0] != newAssignee
                if (isNewAssignee){
                    notificationRepo.notifyUserAssignedToTask(
                        userId = newAssignee,
                        taskName = task.title,
                        projectName = task.projectName,
                        senderName = senderName
                    )
                }

                // Find removed users
//                val removedAssignees = originalAssignees.filter { !task.assignedTo.contains(it) }
//
//                // Send notifications to removed users
//                removedAssignees.forEach { userId ->
//                    notificationRepo.notifyUserRemovedFromTask(
//                        userId = userId,
//                        taskName = task.title,
//                        senderName = senderName
//                    )
//                }
                // Notify all members of the project's team about this task update
                projectRepo.projectCollection.document(task.projectId).get().await().let { projSnap ->
                    val project = projSnap.toObject(Project::class.java)
                    project?.teams?.forEach { memberId ->
//                        if (memberId != userRepo.currentUser().uid) {
                            notificationRepo.notifyUserUpdatedTask(
                                userId = memberId,
                                taskName = task.title,
                                projectName = task.projectName,
                                senderName = senderName
                            )
 //                       } TODO: Remember to uncomment
                    }
                }
               // Notify all members of the project's team about this task update

                _taskState.value = Response.Success(null)
            } catch (e: Exception) {
                _taskState.value = Response.Failure(e)
            }
        }

        fun updateProject(project: Project) = viewModelScope.launch {
            _taskState.value = Response.Loading
            _taskState.value = projectRepo.updateProject(project)
        }

        fun deleteTask(projectId: String, taskId: String) = viewModelScope.launch {
            _taskState.value = Response.Loading
            _taskState.value = projectRepo.deleteTask(projectId, taskId)
        }

        fun deleteProject(projectId: String) = viewModelScope.launch {
            _taskState.value = Response.Loading
            _taskState.value = projectRepo.deleteProject(projectId)
        }

        fun getAllUserTasks() {
            val userId = userRepo.currentUser().uid

            projectRepo._allTasks.whereArrayContains("assignedTo", userId)
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        val result = value.toObjects(Task::class.java)
                        _allTasks.postValue(result)
                    } else {
                        _allTasks.postValue(emptyList())
                        Log.e("PROJECT_VIEW_MODEL", "Error getting all tasks: ${error?.message}")
                    }
                }
        }

        fun getTeamFromProject(teamIdList: List<String>) {
            projectRepo.getTeamFromProject(teamIdList).addSnapshotListener { value, error ->
                if (value != null) {
                    val teamUsers = value.toObjects(User::class.java)
                    _teamProject.postValue(teamUsers)
                } else {
                    Log.e(
                        "PROJECT_VIEW_MODEL",
                        "Error getting team from project: ${error?.message}"
                    )
                    _teamProject.postValue(emptyList())
                }
            }
        }
    }
