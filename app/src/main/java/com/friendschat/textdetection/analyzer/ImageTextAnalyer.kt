package com.friendschat.textdetection.analyzer

import android.annotation.SuppressLint
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.friendschat.textdetection.dipatchers.AppDispatchers
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text.TextBlock
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ImageTextAnalyer(private val appDispatchers: AppDispatchers) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val scope = CoroutineScope(appDispatchers.ioDispatcher)
    private val resultset = hashSetOf<String>()
    private val singelPoolDispatcher = appDispatchers.defaultDispatcher.limitedParallelism(1)
    val textArray: Array<String>
        get() = resultset.toTypedArray()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage: Image? = image.image
        val inputImage = InputImage.fromMediaImage(mediaImage!!, image.imageInfo.rotationDegrees)
        scope.launch {
            val textBlock: List<TextBlock> = try {
                recognizer.process(inputImage).await().textBlocks
            } catch (t: Throwable) {
                listOf()
            }
            addToSet(textBlock)
            image.close()
        }
    }

    private suspend fun addToSet(textBockList: List<TextBlock>) {
        withContext(singelPoolDispatcher) {
            textBockList.forEach { textBlock ->
                textBlock.lines.forEach { line ->
                    val text = line.text
                    if (text.isNotEmpty()) {
                        resultset.add(text)
                    }
                }
            }
        }
    }

    fun onClear() {
        scope.launch {
            withContext(singelPoolDispatcher) {
                resultset.clear()
            }
        }
    }
}