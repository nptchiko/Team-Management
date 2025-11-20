package com.thehecotnha.myapplication.activities.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.Task
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.repository.ProjectRepository
import com.thehecotnha.myapplication.repository.UserRepository

class SearchViewModel(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var search: String = ""

    val _userList = MutableLiveData<List<User>?>()

    val _tasksList = MutableLiveData<List<Task>?>()

    val _projectList = MutableLiveData<List<Project>?>()

    /**
     * Search the tasks
     */
    fun searchTasks() {
        projectRepository.searchTasks(search).addSnapshotListener { value, _ ->

            if (value != null) {
                val result = value.toObjects(Task::class.java)

                this._tasksList.postValue(result)
            } else {
                this._tasksList.postValue(null)
            }

        }
    }

    /**
     * Search projects
     */
    fun searchProjects() {
        projectRepository.searchProjects(search).addSnapshotListener { value, _ ->

            if (value != null) {
                val result = value.toObjects(Project::class.java)

                this._projectList.postValue(result)
            } else {
                this._projectList.postValue(null)
            }

        }
    }

    /**
     * Search users
     */
    fun searchUsers() {
        userRepository.searchUsers(search).addSnapshotListener { value, _ ->

            if (value != null) {
                val result = value.toObjects(User::class.java)

                this._userList.postValue(result)
            } else {
                this._userList.postValue(null)
            }

        }
    }
}
