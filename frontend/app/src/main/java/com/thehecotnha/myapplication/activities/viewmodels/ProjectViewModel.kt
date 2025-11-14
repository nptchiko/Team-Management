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
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.repository.ProjectRepository
import com.thehecotnha.myapplication.repository.UserRepository
import com.thehecotnha.myapplication.models.Response
import com.thehecotnha.myapplication.utils.removeTime
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.log

class ProjectViewModel : ViewModel() {

    private val projectRepo = ProjectRepository()
    private val userRepo = UserRepository()

    val _project = MutableLiveData<List<Project>?>()

    val _projectTask = MutableLiveData<List<Task>?>()

    val _taskState = MutableLiveData<Response<Void>>(Response.Idle)

    val _allTasks = MutableLiveData<List<Task>>()

    fun getUserProjects() {
        val userId = userRepo.currentUser().uid

        projectRepo.getUserProjects(userId).addSnapshotListener { value, error ->
            val result = value?.toObjects(Project::class.java)
            _project.postValue(result)
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
                    .filter { it.assignedTo[0] == userId }
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
        _taskState.value = projectRepo.saveTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        task.updatedBy = userRepo.currentUser().uid
        _taskState.value = Response.Loading
        _taskState.value = projectRepo.updateTask(task)
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

        projectRepo._allTasks.whereArrayContains("assignedTo", userId).addSnapshotListener { value, error ->
            if (value != null) {
                val result = value.toObjects(Task::class.java)
                _allTasks.postValue(result)
            } else {
                _allTasks.postValue(emptyList())
                Log.e("PROJECT_VIEW_MODEL", "Error getting all tasks: ${error?.message}")
            }
        }
    }
}