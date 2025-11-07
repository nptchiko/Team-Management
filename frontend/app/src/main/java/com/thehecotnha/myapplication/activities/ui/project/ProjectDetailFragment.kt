package com.thehecotnha.myapplication.activities.ui.project

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thehecotnha.myapplication.R
import com.thehecotnha.myapplication.databinding.FragmentProjectDetailBinding
import com.thehecotnha.myapplication.models.Project



@Suppress("DEPRECATION")
class ProjectDetailFragment : Fragment() {

    private var _binding: FragmentProjectDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: ProjectDetailViewModel by viewModels()
    private var project: Project? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Retrieve the project object from arguments
            project = it.getParcelable(ARG_PROJECT) } // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectDetailBinding.inflate(inflater, container, false)
        val root = binding.root


        return root
    }

    companion object {
        private const val ARG_PROJECT = "project_arg"

        @JvmStatic
        fun newInstance(project: Project) =
            ProjectDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROJECT, project)
                }
            }
    }
}