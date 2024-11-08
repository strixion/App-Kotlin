package com.example.app
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.app.databinding.ActivityCameraFragmentBinding
import java.io.File
import android.media.MediaScannerConnection
import java.text.SimpleDateFormat
import java.util.Locale
import android.net.Uri
class CameraFragment : Fragment(R.layout.activity_camera_fragment) {

    private var _binding: ActivityCameraFragmentBinding? = null
    private val binding get() = _binding!!

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private var imageCapture: ImageCapture? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityCameraFragmentBinding.bind(view)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }

        binding.button3.setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()

            preview.setSurfaceProvider(binding.previewView.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraFragment", "Ошибка запуска камеры: ${e.message}", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "captured_image.png"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    MediaScannerConnection.scanFile(
                        requireContext(),
                        arrayOf(photoFile.absolutePath),
                        arrayOf("image/png"),
                        null
                    )
                    Toast.makeText(requireContext(), "Фотография сохранена", Toast.LENGTH_SHORT).show()

                    // Запускаем распознавание текста
                    processImageForText(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "Ошибка при сохранении фото: ${exception.message}", exception)
                    Toast.makeText(requireContext(), "Ошибка при съемке", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }




    private fun processImageForText(photoFile: File) {
        val image = InputImage.fromFilePath(requireContext(), Uri.fromFile(photoFile))

        // Создаем распознаватель текста с поддержкой русского языка
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val recognizedText = visionText.text
                showTextInFragment(recognizedText)
            }
            .addOnFailureListener { e ->
                Log.e("CameraFragment", "Ошибка распознавания текста: ${e.message}", e)
                Toast.makeText(requireContext(), "Ошибка распознавания текста", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showTextInFragment(text: String) {
        val bundle = Bundle()
        bundle.putString("recognizedText", text)

        val textFragment = TextDisplayFragment()
        textFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, textFragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Требуется разрешение на камеру", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
