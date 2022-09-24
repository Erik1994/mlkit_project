package com.friendschat.textdetection.ui.features.text

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.friendschat.textdetection.R
import com.friendschat.textdetection.databinding.FragmentTextBinding
import com.friendschat.textdetection.ui.common.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TextFramgnet: BaseFragment(R.layout.fragment_text) {
    override val viewModel: TextViewModel by viewModel()
    private var binding: FragmentTextBinding? = null
    private val args: TextFramgnetArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding = FragmentTextBinding.inflate(layoutInflater, container, false)
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val teaxtArray = args.texts.toList().distinct()
        val result = teaxtArray.joinToString(
            separator = "\n"
        )
        binding?.text?.text = result
    }
}