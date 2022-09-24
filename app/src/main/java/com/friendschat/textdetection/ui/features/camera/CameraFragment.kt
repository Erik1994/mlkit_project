package com.friendschat.textdetection.ui.features.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.friendschat.textdetection.R
import com.friendschat.textdetection.analyzer.ImageTextAnalyer
import com.friendschat.textdetection.databinding.FragmentCameraBinding
import com.friendschat.textdetection.ui.common.BaseFragment
import com.friendschat.textdetection.ui.extension.showSnackbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.Executors

class CameraFragment : BaseFragment(R.layout.fragment_camera) {
    override val viewModel: CameraViewModel by viewModel()
    private var binding: FragmentCameraBinding? = null
    private val analyer: ImageTextAnalyer by inject()

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
        setupMenu()
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
                val imageAnalysis = buildImageAnalysisUseCase()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
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

    private fun buildImageAnalysisUseCase(): ImageAnalysis =
        ImageAnalysis.Builder()
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
            .build().also { it.setAnalyzer(Executors.newSingleThreadExecutor(), analyer) }

    private fun isPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() =
        requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                when (menuItem.itemId) {
                    R.id.apply -> {
                        findNavController().navigate(
                            CameraFragmentDirections.actionCameraFragmentToTextFramgnet(
                                analyer.textArray
                            )
                        )
                        analyer.onClear()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}