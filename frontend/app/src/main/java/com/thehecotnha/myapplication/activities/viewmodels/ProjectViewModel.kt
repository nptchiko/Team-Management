package com.thehecotnha.myapplication.activities.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.Task
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.repository.ProjectRepository
import com.thehecotnha.myapplication.repository.UserRepository
import com.thehecotnha.myapplication.models.Response
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {

    private val projectRepo = ProjectRepository()
    private val userRepo = UserRepository()

    val _project = MutableLiveData<List<Project>?>()

    val _projectTask = MutableLiveData<List<Task>>()

    val _taskState = MutableLiveData< Response<Void>>(Response.Idle)

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
                _projectTask.postValue(null)
                Log.e("PROJECT_VIEW_MODEL", "Error getting tasks: ${error?.message}")

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
}