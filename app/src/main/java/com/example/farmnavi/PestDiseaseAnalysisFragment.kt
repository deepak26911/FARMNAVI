package com.example.farmnavi

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class PestDiseaseAnalysisFragment : Fragment(R.layout.fragment_pest_disease_analysis) {

    private lateinit var tflite: Interpreter
    private lateinit var labels: List<String>
    private lateinit var capturedImageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var takePhotoButton: MaterialButton
    private lateinit var uploadButton: MaterialButton

    // ActivityResultLauncher for picking from gallery
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        // Resize bitmap to model input size
                        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, true)

                        capturedImageView.setImageBitmap(resizedBitmap)
                        runInference(resizedBitmap)
                    } catch (e: IOException) {
                        Log.e("PestDiseaseFragment", "Error loading image: ${e.message}", e)
                        resultTextView.text = "Error: Could not load image."
                    } catch (e: Exception) {
                        Log.e("PestDiseaseFragment", "General error: ${e.message}", e)
                        resultTextView.text = "Error: Could not process the image."
                    }
                }
            }
        }

    // ActivityResultLauncher for taking a photo
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val bitmap = data?.extras?.get("data") as? Bitmap
                if (bitmap != null) {
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, true)
                    capturedImageView.setImageBitmap(resizedBitmap)
                    runInference(resizedBitmap)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI references
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        capturedImageView = view.findViewById(R.id.capturedImageView)
        resultTextView = view.findViewById(R.id.resultTextView)
        takePhotoButton = view.findViewById(R.id.button_take_photo)
        uploadButton = view.findViewById(R.id.button_upload)

        // Toolbar back button
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        toolbar.setNavigationOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        try {
            // Load model and labels
            tflite = Interpreter(loadModelFile())
            labels = requireContext().assets.open("plant_disease_labels.txt").bufferedReader().readLines()
            Log.d("PestDiseaseFragment", "Model and labels loaded successfully")
        } catch (e: Exception) {
            Log.e("PestDiseaseFragment", "Error loading model/labels: ${e.message}", e)
            resultTextView.text = "Error: Model or labels not loaded."
            return
        }

        // Button click listeners
        takePhotoButton.setOnClickListener { startCamera() }
        uploadButton.setOnClickListener { startGallery() }
    }

    private fun loadModelFile(): ByteBuffer {
        val fileDescriptor = requireContext().assets.openFd("plant_disease_model_final.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun runInference(bitmap: Bitmap) {
        val byteBuffer = ByteBuffer.allocateDirect(4 * MODEL_INPUT_SIZE * MODEL_INPUT_SIZE * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(MODEL_INPUT_SIZE * MODEL_INPUT_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixel in intValues) {
            byteBuffer.putFloat(((pixel shr 16 and 0xFF) / 255.0f)) // R
            byteBuffer.putFloat(((pixel shr 8 and 0xFF) / 255.0f))  // G
            byteBuffer.putFloat(((pixel and 0xFF) / 255.0f))        // B
        }

        val outputArray = Array(1) { FloatArray(labels.size) }
        tflite.run(byteBuffer, outputArray)

        val predictions = outputArray[0]
        val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: -1
        val predictedLabel = if (maxIndex != -1) labels[maxIndex] else "Unknown"

        resultTextView.text = "Predicted Disease: $predictedLabel"
        Log.d("PestDiseaseFragment", "Predicted: $predictedLabel")
    }

    private fun startGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun startCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tflite.close()
    }

    companion object {
        // Adjust depending on your model input size (224 or 256)
        private const val MODEL_INPUT_SIZE = 224
    }
}
