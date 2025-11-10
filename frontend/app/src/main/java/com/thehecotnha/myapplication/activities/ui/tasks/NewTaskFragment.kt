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
            // Retrieve the project object from arguments
            project = it.getParcelable(ARG_PROJECT) }
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        val root = b.root



        b.ivDueDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder<Date>.datePicker()
                    .setTitleText("Select date")
                    .build()


        }



        return root
    }
}