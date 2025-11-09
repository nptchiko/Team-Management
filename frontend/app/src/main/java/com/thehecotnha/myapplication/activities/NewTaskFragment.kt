package com.thehecotnha.myapplication.activities

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.activities.ui.project.ProjectDetailFragment
import com.thehecotnha.myapplication.databinding.FragmentNewTaskBinding
import com.thehecotnha.myapplication.models.Project

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

        Toast.makeText(requireContext(), "Project id: ${project!!.id}", Toast.LENGTH_SHORT).show()


        return root
    }
}