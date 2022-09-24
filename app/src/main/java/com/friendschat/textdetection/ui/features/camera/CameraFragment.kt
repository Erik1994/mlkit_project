package com.friendschat.textdetection.ui.features.camera

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.friendschat.textdetection.R
import com.friendschat.textdetection.databinding.FragmentCameraBinding
import com.friendschat.textdetection.ui.common.BaseFragment
import com.friendschat.textdetection.ui.extension.showSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraFragment : BaseFragment(R.layout.fragment_camera) {
    override val viewModel: CameraViewModel by viewModel()
    private var binding: FragmentCameraBinding? = null

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissions = arrayListOf<String>()
            permissions.entries.forEach {
                if (!it.value) {
                    deniedPermissions.add(it.key)
                }
                deniedPermissions.size.takeIf { size -> size > 0 }?.let {
                    showDialog()
                } ?: startCamera()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding = FragmentCameraBinding.inflate(layoutInflater, container, false)
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickLiteners()
    }

    private fun setClickLiteners() {
        binding?.apply {
            fabCamera.setOnClickListener {
                if (isPermissionsGranted()) {
                    startCamera()
                } else {
                    requestPermission()
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                // Camera provider is now guaranteed to be available
                val cameraProvider = cameraProviderFuture.get()
                // Set up the preview use case to display camera preview.
                val preview = buildPreviewUseCase()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview
                )
                binding?.let { it.fabCamera.visibility = View.GONE }
            } catch (e: Exception) {
                showSnackbar(e.message.orEmpty())
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun buildPreviewUseCase() = binding?.let {
        Preview.Builder().build().also { preview ->
            preview.setSurfaceProvider(
                it.previewView.surfaceProvider
            )
        }
    }

    private fun isPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() =
        requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))

}