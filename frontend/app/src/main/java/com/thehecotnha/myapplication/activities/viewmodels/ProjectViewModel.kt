package com.thehecotnha.myapplication.activities.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.repository.ProjectRepository
import com.thehecotnha.myapplication.repository.UserRepository
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {

    private val projectRepo = ProjectRepository()
    private val userRepo = UserRepository()

    val _project = MutableLiveData<List<Project>?>()

    val _projectTask = MutableLiveData<List<com.thehecotnha.myapplication.models.Task>?>()


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
                val result = value.toObjects(com.thehecotnha.myapplication.models.Task::class.java)
                Toast.makeText(context, "Found ${result.size} tasks", Toast.LENGTH_SHORT).show()
                _projectTask.postValue(result)
            } else {
                _projectTask.postValue(null)
                Log.e("PROJECT_VIEW_MODEL", "Error getting tasks: ${error?.message}")

            }

        }
    }

}