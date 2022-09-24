package com.friendschat.textdetection.ui.features.text

import com.friendschat.textdetection.R
import com.friendschat.textdetection.ui.common.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TextFramgnet: BaseFragment(R.layout.fragment_text) {
    override val viewModel: TextViewModel by viewModel()
}