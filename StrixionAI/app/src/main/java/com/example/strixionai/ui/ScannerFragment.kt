package com.example.strixionai.ui
import android.graphics.BitmapFactory
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.strixionai.R
import net.sourceforge.tess4j.Tesseract
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.android.Utils
import java.io.File
import org.opencv.core.Mat



class ScannerFragment : Fragment() {
    private lateinit var previewView: PreviewView
    private lateinit var scanButton: Button
    private lateinit var imageCapture: ImageCapture
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        previewView = view.findViewById(R.id.preview_view)
        scanButton = view.findViewById(R.id.scan_button)

        scanButton.setOnClickListener {
            if (checkCameraPermission()) {
                captureImage()
            } else {
                requestCameraPermission()
            }
        }

        if (checkCameraPermission()) {
            startCamera()
        } else {
            requestCameraPermission()
        }

        return view
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) { }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureImage() {
        val outputFile = File(requireContext().externalCacheDir, "captured_image.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
                    processImageAndShowDialog(bitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                }
            })
    }

    private fun processImageAndShowDialog(image: Bitmap) {
        val processedText = processImageAndGetCorrectedText(image)
        showCorrectedTextDialog(processedText)
    }

    private fun processImageAndGetCorrectedText(image: Bitmap): String {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        val inputImage = Mat()
        Utils.bitmapToMat(image, inputImage)
        val grayImage = Mat()
        Imgproc.cvtColor(inputImage, grayImage, Imgproc.COLOR_BGR2GRAY)
        val blurredImage = Mat()
        Imgproc.GaussianBlur(grayImage, blurredImage, Size(5.0, 5.0), 0.0)
        val binaryImage = Mat()
        Imgproc.adaptiveThreshold(blurredImage, binaryImage, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2.0)
        val edges = Mat()
        Imgproc.Canny(binaryImage, edges, 50.0, 150.0)
        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(edges, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        val largestContour = contours.maxByOrNull { Imgproc.contourArea(it) }
        val boundingRect = Imgproc.boundingRect(largestContour)
        val tess = Tesseract().apply { setDatapath("tessdata") }
        val textRegion = Mat(inputImage, boundingRect)
        val outputFile = File(requireContext().cacheDir, "output.jpg")
        Imgcodecs.imwrite(outputFile.absolutePath, textRegion)
        return tess.doOCR(outputFile).replace("\\s+".toRegex(), " ")
    }
    private fun showCorrectedTextDialog(correctedText: String) {
        val dialog = CorrectedTextDialog(correctedText)
        dialog.show(parentFragmentManager, "CorrectedTextDialog")
    }
    @Deprecated("")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            }
        }
    }
}

class CorrectedTextDialog(private val correctedText: String) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_corrected_text, container, false)
        val textView = view.findViewById<TextView>(R.id.corrected_text_view)
        val confirmButton = view.findViewById<Button>(R.id.confirm_button)
        val closeButton = view.findViewById<Button>(R.id.close_button)

        textView.text = correctedText
        confirmButton.setOnClickListener {  }
        closeButton.setOnClickListener {  }
        return view
    }


}