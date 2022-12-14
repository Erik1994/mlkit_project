package com.friendschat.textdetection.ui.common

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.friendschat.textdetection.ui.extension.collectLifeCycleFlow
import com.friendschat.textdetection.ui.navigation.NavigationCommand

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {
    protected abstract val viewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigation()
    }

    private fun observeNavigation() {
        collectLifeCycleFlow(viewModel.navigationFlow) {
            when (it) {
                is NavigationCommand.Back -> findNavController().navigateUp()
                is NavigationCommand.To -> findNavController().navigate(it.direction)
            }
        }
    }

    protected fun showDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission required")
            .setMessage("Please grant all permissions to use this app")
            .setNegativeButton("Cancel") { dialog: DialogInterface, _ ->
                dialog.dismiss()
                activity?.finish()
            }
            .setPositiveButton("Ok") { dialog: DialogInterface, _ ->
                dialog.dismiss()
                startCameraPermissionSettingsPage()
            }.show()
    }

    private fun startCameraPermissionSettingsPage() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}