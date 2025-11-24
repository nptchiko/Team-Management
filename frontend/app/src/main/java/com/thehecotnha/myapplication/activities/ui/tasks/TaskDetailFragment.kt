package com.thehecotnha.myapplication.activities.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.activities.ui.adapters.TeamAdapter
import com.thehecotnha.myapplication.activities.viewmodels.ProjectViewModel
import com.thehecotnha.myapplication.databinding.FragmentTaskDetailBinding
import com.thehecotnha.myapplication.models.CalendarDate
import com.thehecotnha.myapplication.models.Response
import com.thehecotnha.myapplication.models.Task
import com.thehecotnha.myapplication.models.TeamItem
import com.thehecotnha.myapplication.models.TeamMember
import com.thehecotnha.myapplication.utils.priorityName
import com.thehecotnha.myapplication.utils.showAleartDialog
import com.thehecotnha.myapplication.utils.showProgressDialog
import com.thehecotnha.myapplication.utils.showSuccessDialog
import java.util.Date

@Suppress("DEPRECATION")
class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val b get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(this).get(ProjectViewModel::class.java)
    }

    val assignee =  mutableListOf<TeamItem>()
    private var taskInfo: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable menu callbacks for this fragment
        setHasOptionsMenu(true)

        arguments?.let {
            // Retrieve the project object from arguments
            taskInfo = it.getParcelable(ARG_TASK)
        } // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        val root = b.root

        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select date")
                .build()

        var startDate = taskInfo?.startDate
        var endDate = taskInfo?.endDate

        b.stateTextView.hint = "${taskInfo?.state}"
        b.stateTextView.setAdapter(
            ArrayAdapter(
            requireContext(),
            R.layout.item_state,
            R.id.state_name,
            listOf("TODO", "IN PROGRESS", "DONE")
        ))

        b.priorityTextView.hint = "${taskInfo?.priority}" ?: priorityName.MEDIUM
        b.priorityTextView.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_state,
                R.id.state_name,
                listOf(priorityName.MEDIUM, priorityName.HIGH, priorityName.LOW)
            )
        )

        // add current assignee to the list
        viewModel._assignee.observe(viewLifecycleOwner) { currentAssignee: TeamMember ->
            assignee.clear()
            assignee.add(TeamItem(currentAssignee.name, currentAssignee.userId, currentAssignee.role))

            b.rvTeam.adapter = TeamAdapter(assignee) { teamItem ->
                val position = assignee.indexOf(teamItem)
                if (position != -1) {
                    assignee.removeAt(position)
                    (b.rvTeam.adapter as TeamAdapter).notifyItemRemoved(position)
                }

            }

        }
        viewModel.getAssignee(taskInfo!!)



        b.edtTaskTitle.setText(taskInfo?.title)
        b.edtTaskDescription.setText(taskInfo?.description)
        b.tvDueDate.text = CalendarDate(taskInfo?.endDate!!.toDate()).calendar
        b.tvStartDate.text = CalendarDate(taskInfo?.startDate!!.toDate()).calendar



        b.ivDueDate.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        b.ivStartDate.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }
        datePicker.addOnPositiveButtonClickListener { selection ->
            startDate = Timestamp(Date(selection.first))
            endDate = Timestamp(Date(selection.second))
            b.tvDueDate.text = CalendarDate(endDate.toDate()).calendar
            b.tvStartDate.text = CalendarDate(startDate.toDate()).calendar
        }

        // Handle toolbar navigation (back) icon click
        b.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Handle toolbar menu item clicks directly on the toolbar. This covers
        // clicks coming from the toolbar's `app:menu` attribute in layout.
        b.toolbar.setOnMenuItemClickListener { menuItem ->
            handleToolbarMenuClick(menuItem)
        }


       /* viewModel._teamMember.observe(viewLifecycleOwner) { team ->
            if (team != null) {
                teamOfProject = team
            }
        }
        viewModel.getTeamMember(project!!.id!!)
        b.btAddUser.setOnClickListener {
            val names = teamOfProject.map { it.name }.toTypedArray()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Assignee")
                .setSingleChoiceItems(names, -1) { dialog, which ->
                    val selectedMember = teamOfProject[which]
                    if (assignee.any { it.userId == selectedMember.userId }) {
                        toast(requireContext(), "Member already assigned")
                        return@setSingleChoiceItems
                    }
                    if (assignee.size >= 1) {
                        toast(requireContext(), "Only one assignee is allowed for a task")
                        return@setSingleChoiceItems
                    }
                    assignee.add(
                        TeamItem(
                            selectedMember.name,
                            selectedMember.userId,
                            selectedMember.role
                        )
                    )
                    teamAdapter.notifyItemInserted(assignee.size - 1)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()

            Toast.makeText(
                requireContext(),
                "Assigned to: ${assignee.joinToString { it.name }}",
                Toast.LENGTH_SHORT
            ).show()

        }*/
        b.btnChanges.setOnClickListener {
            val title = b.edtTaskTitle.text.toString().trim().ifEmpty {
                return@ifEmpty taskInfo!!.title
            }
            val description = b.edtTaskDescription.text.toString().trim().ifEmpty {
                return@ifEmpty taskInfo!!.description
            }
            val state = b.stateTextView.text.toString().trim().ifEmpty {
                return@ifEmpty taskInfo!!.state
            }

            val priority = b.priorityTextView.text.toString().trim().ifEmpty {
                return@ifEmpty taskInfo!!.priority
            }


            val updatedTask = taskInfo!!.copy(
                title = title,
                description = description,
                state = state,
                startDate = startDate,
                endDate = endDate,
                priority = priority
            )

            val progressDialog = showProgressDialog(requireContext(), "Updating task...")
            viewModel._taskState.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Response.Success -> {
                        progressDialog.dismiss()
                        showSuccessDialog(requireContext(), "Success", "Task updated successfully.")
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                    is Response.Failure -> {
                        progressDialog.dismiss()
                        showAleartDialog(requireContext(), "Oops!", response.e?.message ?: "Failed to update task.")
                    }
                    Response.Idle -> {}
                    Response.Loading -> {
                        progressDialog.show()
                    }
                }
            }
            viewModel.updateTask(
               updatedTask, taskInfo!!.assignedTo
            )
        }

        return root
    }

    fun handleToolbarMenuClick(item: MenuItem): Boolean {
        val progressDialog = showProgressDialog(requireContext(), "Deleting task...")
        return when (item.itemId){
            R.id.action_delete -> {

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("WARNING")
                    .setMessage("Are you sure you want to delete this task?")
                    .setNegativeButton("Decline") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Accept") { dialog, which ->

                        viewModel._taskState.observe(viewLifecycleOwner){
                            when (it) {
                                is Response.Success -> {
                                    progressDialog.dismiss()
                                    showSuccessDialog(requireContext(), "Success", "Task deleted successfully.")
                                    requireActivity().onBackPressedDispatcher.onBackPressed()
                                }
                                is Response.Failure -> {
                                    progressDialog.dismiss()
                                    showAleartDialog(requireContext(), "Oops!", it.e?.message ?: "Failed to delete task.")
                                }
                                Response.Idle -> {}
                                Response.Loading -> {
                                    progressDialog.show()
                                }
                            }
                        }
                        viewModel.deleteTask(
                            taskInfo!!.projectId,
                            taskInfo!!.id
                        )
                    }
                    .show()
                true
            }

          else -> {
              Toast.makeText(requireContext(), "Other action clicked with id=${item.itemId}", Toast.LENGTH_SHORT).show()
              super.onOptionsItemSelected(item)
          }
        }
    }

    companion object {
        private const val ARG_TASK = "task_arg"
        @JvmStatic
        fun newInstance(taskInfo: Task) =
            TaskDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, taskInfo)
                }
            }
    }
}