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
import com.thehecotnha.myapplication.models.TeamMember
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.repository.NotificationRepository
import com.thehecotnha.myapplication.utils.enums.TeamRole
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

    val _teamMember = MutableLiveData<List<TeamMember>>()

    val _assignee = MutableLiveData<TeamMember>()

    val _project = MutableLiveData< Response<Project>>()
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
    fun removeUserFromProject(project: Project, userId: String) = viewModelScope.launch {
        _taskState.value = Response.Loading
        val projectId = project.id!!
        val projectName = project.title
        try {
            // Get current user's name
            val currentUserData = userRepo.getUserData()
            val senderName = if (currentUserData is Response.Success) {
                currentUserData.data?.username ?: "Someone"
            } else {
                "Someone"
            }
            project.teams.remove(userId)
            val state = projectRepo.updateProject(project)
            when (state) {
                is Response.Success -> Log.i("PROJECT_VIEW_MODEL", "User removed from project successfully")
                is Response.Failure -> Log.e("PROJECT_VIEW_MODEL", "Failed to remove user from project")
                else -> {}
            }
            // Remove user from project
            projectRepo.getTeamMembers(projectId).get().await().let { snapshot ->
                val teamMembers = snapshot.toObjects(TeamMember::class.java)
                if (teamMembers.isNotEmpty()) {
                    for (member in teamMembers) {
                        if (member.userId == userId) {
                            projectRepo.removeTeamMember(member)
                            notificationRepo.notifyUserRemovedFromProject(
                                userId = userId,
                                projectName = projectName,
                                senderName = senderName
                            )
                            break
                        }
                    }
                }
            }
            _taskState.value = Response.Success(null)
        } catch (e: Exception) {
            _taskState.value = Response.Failure(e)
        }
    }

    fun createProject(project: Project, teams: MutableList<TeamMember>) = viewModelScope.launch {
        _project.value = Response.Loading

        val res = projectRepo.createProject(project)
        _project.value = res
        when (res){
            is Response.Success -> {
                Log.i("PROJECT_VIEW_MODEL", "Project created successfully")
                addTeamToProject(res.data!!, project.title, teams)
            }
            is Response.Failure -> {
                Log.e("PROJECT_VIEW_MODEL", "Failed to create project")
            }

            Response.Idle -> {}
            Response.Loading -> {}
        }
    }

    private fun addTeamToProject(project: Project, projectName: String, teams: MutableList<TeamMember>) = viewModelScope.launch {
        val currentUserData = userRepo.getUserData()
        val senderName = if (currentUserData is Response.Success) {
            currentUserData.data?.username ?: "Someone"
        } else {
            "Someone"
        }
        if (teams.isNotEmpty()) {
            teams.forEach { member ->
                val email = member.name
                Log.i("PROJECT_VIEW_MODEL", "Adding user with email: $email to project")
                userRepo.getUserByField("email", email).get().await().let { querySnap ->
                    if (querySnap.documents.isNotEmpty()) {
                        val user = querySnap.documents[0].toObject(User::class.java)
                        if (user != null) {
                            member.userId = user.uid
                            project.teams.add(user.uid)
                            notificationRepo.notifyUserAddedToProject(
                                userId = user.uid,
                                projectName = projectName,
                                senderName = senderName
                            )
                        }
                    } else {
                        teams.remove(member)
                    }
                }
            }
        }
        projectRepo.updateProject(project)
        teams.add(TeamMember("", senderName, userRepo.currentUser().uid, "", TeamRole.ADMIN.name))
        projectRepo.addTeam(project.id!!, teams)
    }


    fun getAllProjectTasks() {
        projectRepo._allTasks.addSnapshotListener { value, error ->
                if (value != null) {
                    val result = value.toObjects(Task::class.java)
                    Log.i("PROJECT_VIEW_MODEL", "Fetched ${result.size} tasks")
                    _allTasks.postValue(result)
                } else {
                    _allTasks.postValue(emptyList())
                    Log.e("PROJECT_VIEW_MODEL", "Error getting tasks: ${error?.message}")

                }

            }
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
//                        if (memberId != userRepo.currentUser().userId) {
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

        fun getTeamMember(projectId: String) {
            projectRepo.getTeamMembers(projectId).addSnapshotListener { value, error ->
                if (value != null) {
                    val teamMembers = value.toObjects(TeamMember::class.java)
                    Log.i("PROJECT_VIEW_MODEL", "Fetched ${teamMembers.size} team members for project $projectId")
                    _teamMember.postValue(teamMembers)
                } else {
                    Log.e(
                        "PROJECT_VIEW_MODEL",
                        "Error getting team members: ${error?.message}"
                    )
                    _teamMember.postValue(emptyList())
                }

            }
        }
        fun getAssignee(task: Task) {
            var assignee: TeamMember = TeamMember("", "No name", "", task.projectId)

            projectRepo.getTeamMembers(task.projectId).addSnapshotListener { value, error ->
                if (value != null && task.assignedTo.isNotEmpty()) {

                    Log.i(
                        "PROJECT_VIEW_MODEL",
                        "Looking for assignee with ID: ${task.assignedTo[0]}"
                    )

                    val teamMembers = value.toObjects(TeamMember::class.java)

                    teamMembers.find { it.userId == task.assignedTo[0] }?.let {
                        _assignee.postValue(it)
                        Log.i("PROJECT_VIEW_MODEL", "Assignee found: ${it.name}")
                    } ?: Log.e(
                        "PROJECT_VIEW_MODEL",
                        "Assignee with ID ${task.assignedTo[0]} not found in team members"
                    )

                } else {
                    _assignee.postValue(assignee)
                }
            }
        }
        fun getUserRoleInProject(projectId: Project): String {
            val currentUserId = userRepo.currentUser().uid
            if (projectId.ownerId == currentUserId) {
                return TeamRole.ADMIN.name
            }
            var result = TeamRole.MEMBER.name
            projectRepo.getTeamMembers(projectId.id!!).addSnapshotListener { value, error ->
                if (value != null) {
                    val teamMembers = value.toObjects(TeamMember::class.java)
                    for (member in teamMembers) {
                        if (member.userId == currentUserId) {
                            result =  member.role
                            return@addSnapshotListener
                        }
                    }
                } else{
                    Log.e(
                        "PROJECT_VIEW_MODEL",
                        "Error getting user role in project: ${error?.message}"
                    )
                }
            }
            return result
        }
    }
