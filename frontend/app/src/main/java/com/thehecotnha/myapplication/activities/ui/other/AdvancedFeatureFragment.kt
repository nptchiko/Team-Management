package com.thehecotnha.myapplication.activities.ui.other

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.thehecotnha.myapplication.databinding.FragmentAdvancedFeatureBinding
import com.thehecotnha.myapplication.services.PDFUploadingService
import java.text.SimpleDateFormat
import java.util.*

class AdvancedFeatureFragment : Fragment() {

    private var _binding: FragmentAdvancedFeatureBinding? = null
    private val binding get() = _binding!!

    private lateinit var pdfUploader: PDFUploadingService
    private var selectedPdfUri: Uri? = null
    private var selectedFileName: String = ""

    // PDF Picker Launcher
    private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handlePdfSelected(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvancedFeatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pdfUploader = PDFUploadingService()
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        // Toolbar navigation
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // PDF picker card click
        binding.cardPdfPicker.setOnClickListener {
            openPdfPicker()
        }

        // Remove file button
        binding.btnRemoveFile.setOnClickListener {
            clearSelectedFile()
        }

        // Summarize button
        binding.btnSummarize.setOnClickListener {
            startSummarization()
        }

        // Action buttons
        binding.btnCreateTask.setOnClickListener {
            createTaskFromSummary()
        }

        binding.btnCopy.setOnClickListener {
            copySummaryToClipboard()
        }

        binding.btnRegenerate.setOnClickListener {
            resetToUploadState()
        }

        binding.btnRetry.setOnClickListener {
            if (selectedPdfUri != null) {
                startSummarization()
            } else {
                resetToUploadState()
            }
        }
    }

    private fun setupObservers() {
        pdfUploader.pdfContentLiveData.observe(viewLifecycleOwner) { summary ->
            if (summary.isNotEmpty()) {
                showSummaryResult(summary)
            } else {
                showError("No summary generated")
            }
        }
    }

    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pdfPickerLauncher.launch(intent)
    }

    private fun handlePdfSelected(uri: Uri) {
        selectedPdfUri = uri

        // Get file name
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    selectedFileName = it.getString(nameIndex)
                }
            }
        }

        if (selectedFileName.isEmpty()) {
            selectedFileName = "document.pdf"
        }

        // Update UI
        binding.tvFileName.text = selectedFileName
        binding.tvFileTime.text = getCurrentTime()
        binding.layoutSelectedFile.visibility = View.VISIBLE
        binding.btnSummarize.isEnabled = true
    }

    private fun clearSelectedFile() {
        selectedPdfUri = null
        selectedFileName = ""
        binding.layoutSelectedFile.visibility = View.GONE
        binding.btnSummarize.isEnabled = false
    }

    private fun startSummarization() {
        val uri = selectedPdfUri ?: return

        // Show loading state
        showLoading()

        // Start upload and summarization
        try {
            pdfUploader.uploadPdfFromUri(
                requireContext(),
                uri,
                "Summarize this document"
            )
        } catch (e: Exception) {
            showError(e.message ?: "Failed to process PDF")
        }
    }

    private fun showLoading() {
        binding.layoutUploadSection.visibility = View.GONE
        binding.layoutLoading.visibility = View.VISIBLE
        binding.layoutResult.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    private fun showSummaryResult(summary: String) {
        binding.layoutUploadSection.visibility = View.GONE
        binding.layoutLoading.visibility = View.GONE
        binding.layoutResult.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE

        binding.tvSummaryContent.text = summary
    }

    private fun showError(message: String) {
        binding.layoutUploadSection.visibility = View.GONE
        binding.layoutLoading.visibility = View.GONE
        binding.layoutResult.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE

        binding.tvErrorMessage.text = message
    }

    private fun resetToUploadState() {
        clearSelectedFile()
        binding.layoutUploadSection.visibility = View.VISIBLE
        binding.layoutLoading.visibility = View.GONE
        binding.layoutResult.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    private fun createTaskFromSummary() {
        val summary = binding.tvSummaryContent.text.toString()

        // Create a bundle with the summary to pass to NewTaskFragment
        val bundle = Bundle().apply {
            putString("summary", summary)
            putString("fileName", selectedFileName)
        }

        Toast.makeText(
            requireContext(),
            "Creating task with summary...",
            Toast.LENGTH_SHORT
        ).show()

        // Navigate to NewTaskFragment (you'll need to implement this navigation)
        // Example:
        // val newTaskFragment = NewTaskFragment()
        // newTaskFragment.arguments = bundle
        // parentFragmentManager.beginTransaction()
        //     .replace(R.id.fragment_nav_activity_dashboard, newTaskFragment)
        //     .addToBackStack(null)
        //     .commit()
    }

    private fun copySummaryToClipboard() {
        val summary = binding.tvSummaryContent.text.toString()
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("PDF Summary", summary)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(
            requireContext(),
            "Summary copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AdvancedFeatureFragment()
    }
}