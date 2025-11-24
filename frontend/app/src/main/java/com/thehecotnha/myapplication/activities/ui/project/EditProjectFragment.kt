package com.thehecotnha.myapplication.activities.ui.project

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.thehecotnha.myapplication.activities.ui.adapters.TeamAdapter
import com.thehecotnha.myapplication.models.Project
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel
import com.thehecotnha.myapplication.databinding.DialogAddMemberBinding
import com.thehecotnha.myapplication.databinding.FragmentEditProjectBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.Response
import com.thehecotnha.myapplication.models.TeamItem
import com.thehecotnha.myapplication.models.TeamMember
import com.thehecotnha.myapplication.utils.enums.TeamRole
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

    private var teamMember = mutableListOf<TeamItem>()

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

        //b.rvTeam.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        // Set adapter for team members RecyclerView

        teamAdapter = TeamAdapter(teamMember) { teamItem ->
            val index = teamMember.indexOf(teamItem)
            if (index != -1) {
                teamMember.removeAt(index)
                teamAdapter.notifyItemRemoved(index)
            }
        }

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

        projViewModel._teamMember.observe(viewLifecycleOwner) { teamList ->

            if (teamList != null) {
                teamMember.clear()
                teamMember.addAll(teamList.map { it -> TeamItem(it.name, it.userId, it.role) })

                b.rvTeam.adapter = TeamAdapter(teamMember) { teamItem ->
                    val idx = teamMember.indexOf(teamItem)
                if (idx != -1) {

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remove Team Member")
                .setMessage("Are you sure you want to remove this member")
                .setMessage("All tasks belongs to this user will also be removed from the project!!!")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Delete") { dialog, _ ->
                    if (projViewModel.getUserRoleInProject(projectInfo!!) != TeamRole.ADMIN.name) {
                        toast(requireContext(), "Only admin can remove members")
                        return@setPositiveButton
                    }
                    projViewModel.removeUserFromProject(projectInfo!!, teamItem.userId )
                    teamMember.removeAt(idx)
                    b.rvTeam.adapter?.notifyItemRemoved(idx)
                    dialog.dismiss()
                }
                .show()
        }
                }
            }
        }
        projViewModel.getTeamMember(projectInfo!!.id!!)


        b.addUser.setOnClickListener {
            val dialog = Dialog(requireContext())
            val _binding = DialogAddMemberBinding.inflate(dialog.layoutInflater)
            dialog.setContentView(_binding.root)

            _binding.rvSuggestedPeople.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            _binding.spinnerRole.setAdapter(
                android.widget.ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Member", "Administrator")
                )
            )
            _binding.rvSuggestedPeople.adapter = teamAdapter

            _binding.btnAdd.setOnClickListener {
                val email = _binding.edtNameEmail.text.toString().trim().ifEmpty {
                    _binding.edtNameEmail.error = "Email cannot be empty"
                    return@setOnClickListener
                }
                val role = _binding.spinnerRole.text.toString().trim().ifEmpty {
                    _binding.spinnerRole.error = "Role cannot be empty"
                    return@setOnClickListener
                }
                teamMember.add(TeamItem(email, "", role))
                teamAdapter.notifyItemInserted(teamMember.size - 1)
            }


            _binding.btnCancel.setOnClickListener {
                _binding.edtNameEmail.setText("")
            }

            _binding.btnClose.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
            dialog.window?.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )

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