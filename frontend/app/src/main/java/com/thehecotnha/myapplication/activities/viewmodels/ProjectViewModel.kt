package com.thehecotnha.myapplication.activities.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.repository.ProjectRepository
import com.thehecotnha.myapplication.repository.UserRepository
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {

    private val projectRepo = ProjectRepository()
    private val userRepo = UserRepository()

    val _project = MutableLiveData<List<Project>?>()


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

}