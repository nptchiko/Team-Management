package com.thehecotnha.myapplication.activities.ui.project

import android.os.Bundle
import android.util.Log.e
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import com.thehecotnha.myapplication.databinding.FragmentNewProjectBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.thehecotnha.myapplication.activities.ui.adapters.TeamAdapter
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel
import com.thehecotnha.myapplication.databinding.FragmentEditProjectBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.Response
import com.thehecotnha.myapplication.utils.showAleartDialog
import com.thehecotnha.myapplication.utils.showProgressDialog
import com.thehecotnha.myapplication.utils.showSuccessDialog
import com.thehecotnha.myapplication.utils.toast
import java.util.Date

@Suppress("DEPRECATION")
class EditProjectFragment : Fragment() {

    private var _binding: FragmentEditProjectBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val b get() = _binding!!
    private  val projViewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }

    private lateinit var teamAdapter: TeamAdapter

    private var projectInfo : Project? = null

    private val teamMember = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Retrieve the project object from arguments
            projectInfo = it.getParcelable(EditProjectFragment.ARG_PROJECT) } // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProjectBinding.inflate(inflater, container, false)
        val root: View = b.root
        val datePicker =
            MaterialDatePicker.Builder<Date>.datePicker()
                .setTitleText("Select date")
                .build()


        // Set adapter for team members RecyclerView
        teamAdapter = TeamAdapter(projectInfo?.teams!!.toList())
        b.edtProjectTitle.setText(projectInfo?.title)
        b.edtProjectDescription.setText(projectInfo?.description)
        b.tvDueDate.setText(CalendarDate(projectInfo?.dueDate!!.toDate()).calendar)

        b.tvDueDate.setOnClickListener {
            datePicker.show(parentFragmentManager, "Change due date for project")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            b.tvDueDate.setText(datePicker.headerText)
        }

        b.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        b.btnSaveProject.setOnClickListener {
            val title = b.edtProjectTitle.text.toString().ifEmpty {
                b.edtProjectTitle.error = "Title is required"
                return@setOnClickListener
            }
            val description = b.edtProjectDescription.text.toString().ifEmpty {
                b.edtProjectDescription.error = "Description is required"
                return@setOnClickListener
            }
            val dueDate = datePicker.selection?.let { Date(it) } ?: run {
                Toast.makeText(requireContext(), "Due date is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedProject = projectInfo
            updatedProject?.title = title
            updatedProject?.description = description
            updatedProject?.dueDate = Timestamp(Date(datePicker.selection!!))

            val progressDialog = showProgressDialog(requireContext(), "Updating project...")
            projViewModel._taskState.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Failure -> {
                        progressDialog.dismiss()
                        showAleartDialog( requireContext(), "Error!!!", it.e?.message.toString())
                    }
                    is Response.Idle -> {}
                    is Response.Loading -> {
                        progressDialog.show()
                    }
                    is Response.Success -> {
                        progressDialog.dismiss()
                        showSuccessDialog(requireContext(), "Congratulation!!!","Project updated successfully!")
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
            projViewModel.updateProject(updatedProject ?: projectInfo!!)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PROJECT = "project_arg"
        @JvmStatic
        fun newInstance(project: Project) =
            EditProjectFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROJECT, project)
                }
            }
    }

}