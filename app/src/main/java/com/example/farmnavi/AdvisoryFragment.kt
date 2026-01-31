package com.example.farmnavi

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView // Import the TextView class
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class AdvisoryFragment : Fragment(R.layout.fragment_advisory) {

    private lateinit var tflite: Interpreter
    private lateinit var cropLabels: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        try {
            // Initialize interpreter using a more robust file loading method
            tflite = Interpreter(loadModelFile())

            // Load crop labels from assets
            cropLabels = requireContext().assets.open("crop_labels.txt").bufferedReader().readLines()

            // Log to confirm labels are loaded
            Log.d("AdvisoryFragment", "Loaded ${cropLabels.size} crop labels.")

        } catch (e: Exception) {
            Log.e("AdvisoryFragment", "Error loading model or labels: ${e.message}", e)
            Toast.makeText(requireContext(), "Error initializing model. Please restart the app.", Toast.LENGTH_LONG).show()
            return // Exit if initialization fails
        }

        val toolbar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.apply {
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        val nitrogenInput = view.findViewById<TextInputEditText>(R.id.input_nitrogen)
        val phosphorusInput = view.findViewById<TextInputEditText>(R.id.input_phosphorus)
        val potassiumInput = view.findViewById<TextInputEditText>(R.id.input_potassium)
        val temperatureInput = view.findViewById<TextInputEditText>(R.id.input_temperature)
        val humidityInput = view.findViewById<TextInputEditText>(R.id.input_humidity)
        val phInput = view.findViewById<TextInputEditText>(R.id.input_ph)
        val rainfallInput = view.findViewById<TextInputEditText>(R.id.input_rainfall)
        val buttonRecommend = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.button_recommendations)

        // Get a reference to the new TextView from the layout
        val textResult = view.findViewById<TextView>(R.id.text_result)

        buttonRecommend.setOnClickListener {
            try {
                // Get input values and convert to float
                val inputArray = floatArrayOf(
                    nitrogenInput.text.toString().toFloat(),
                    phosphorusInput.text.toString().toFloat(),
                    potassiumInput.text.toString().toFloat(),
                    temperatureInput.text.toString().toFloat(),
                    humidityInput.text.toString().toFloat(),
                    phInput.text.toString().toFloat(),
                    rainfallInput.text.toString().toFloat()
                )

                // Create a 1x7 input tensor
                val inputTensor = arrayOf(inputArray)

                // Create a 1xN output tensor (where N is the number of crop labels)
                val outputMap = Array(1) { FloatArray(cropLabels.size) }

                // Run inference
                tflite.run(inputTensor, outputMap)

                // Find the index with the highest probability
                val maxIndex = outputMap[0].indices.maxByOrNull { outputMap[0][it] } ?: -1

                val predictedCrop = if (maxIndex in cropLabels.indices) {
                    cropLabels[maxIndex]
                } else {
                    "Unknown"
                }

                Log.d("AdvisoryFragment", "Predicted crop: $predictedCrop with index: $maxIndex")
                Toast.makeText(requireActivity(), "Recommended Crop: $predictedCrop", Toast.LENGTH_SHORT).show()

                // Update the TextView with the result instead of a Toast
                textResult.text = "Recommended Crop: $predictedCrop"

            } catch (e: Exception) {
                Log.e("AdvisoryFragment", "Error during inference: ${e.message}", e)
                Toast.makeText(requireContext(), "Invalid input or processing error. Please check your values.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = requireContext().assets.openFd("crop_model.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Close the interpreter to release resources
        tflite.close()
    }
}
