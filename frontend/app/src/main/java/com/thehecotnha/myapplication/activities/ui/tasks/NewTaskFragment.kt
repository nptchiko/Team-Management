package com.thehecotnha.myapplication.activities.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.thehecotnha.myapplication.activities.NewTaskViewModel
import com.thehecotnha.myapplication.activities.ui.adapters.TeamAdapter
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel
import com.thehecotnha.myapplication.databinding.FragmentNewTaskBinding
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.models.Task
import com.thehecotnha.myapplication.utils.Response
import com.thehecotnha.myapplication.utils.showAleartDialog
import com.thehecotnha.myapplication.utils.showProgressDialog
import com.thehecotnha.myapplication.utils.showSuccessDialog
import java.util.Date

@Suppress("DEPRECATION")
class NewTaskFragment : Fragment() {
    companion object {
        private const val ARG_PROJECT = "project_arg"

        @JvmStatic
        fun newInstance(project: Project) =
            NewTaskFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROJECT, project)
                }
            }
    }

    private lateinit var teamAdapter: TeamAdapter
    private val teamMember = mutableListOf<String>()

    private  val viewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }
    private var project: Project? = null
    private var _binding: FragmentNewTaskBinding? = null
    private val b get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            project = it.getParcelable(ARG_PROJECT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        val root = b.root

        teamAdapter = TeamAdapter(teamMember)

        b.rvTeam.adapter = teamAdapter
        b.tvTasksProjectName.text = project!!.title

        val datePicker =
            MaterialDatePicker.Builder<Date>.datePicker()
                .setTitleText("Select date")
                .build()

        b.ivDueDate.setOnClickListener {
            datePicker.show(parentFragmentManager, "Pick due date for task")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            b.tvDueDate.text = datePicker.headerText
        }


        b.btnSaveTask.setOnClickListener {
            val title = b.edtTaskTitle.text.toString().trim().ifEmpty {
                b.edtTaskTitle.error = "Title is required"
                return@setOnClickListener
            }

            val description = b.edtTaskDescription.text.toString().trim().ifEmpty {
                b.edtTaskDescription.error = "Description is required"
                return@setOnClickListener
            }

            val dueDate = datePicker.selection?.let { Date(it) } ?: run {
                Toast.makeText(requireContext(), "Due date is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val progressDialog = showProgressDialog(requireContext(),"Saving task...")

            viewModel._taskState.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Failure -> {
                        progressDialog.dismiss()
                        showAleartDialog(requireContext(), "Error", it.e?.message ?: "Failed to save task.")
                    }
                    is Response.Loading -> {
                        progressDialog.show()
                    }
                    is Response.Success -> {
                        progressDialog.dismiss()
                        showSuccessDialog(requireContext(), "Success", "Task saved successfully!")
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                    Response.Idle -> {}
                }

            }

            viewModel.saveNewTask(
                Task(
                    title = title,
                    description = description,
                    endDate = Timestamp(dueDate),
                    assignedTo = teamMember,
                    projectId = project!!.id!!,
                    projectName = project!!.title,
                    searchTitle = title.lowercase(),
                )
            )
        }

        b.btAddUser.setOnClickListener {
            teamMember.add("usernameHolder")
            teamAdapter.notifyItemInserted(teamMember.size-1)
        }

        return root
    }

}