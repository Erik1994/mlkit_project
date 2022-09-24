package com.friendschat.textdetection.ui.di

import com.friendschat.textdetection.ui.features.camera.CameraViewModel
import com.friendschat.textdetection.ui.features.text.TextViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    viewModel {
        CameraViewModel()
    }

    viewModel {
        TextViewModel()
    }
}