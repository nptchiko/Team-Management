package com.thehecotnha.myapplication.activities.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.thehecotnha.myapplication.activities.NewTaskViewModel
import com.thehecotnha.myapplication.databinding.FragmentNewTaskBinding
import com.thehecotnha.myapplication.models.Project
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

    private val viewModel: NewTaskViewModel by viewModels()
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



        return root
    }
}