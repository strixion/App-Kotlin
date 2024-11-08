package com.example.app
import android.widget.Button
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.core.content.ContextCompat
import com.example.app.databinding.ActivityCameraFragmentBinding

class CameraFragment : Fragment(R.layout.activity_camera_fragment) {

    private var _binding: ActivityCameraFragmentBinding? = null
    private val binding get() = _binding!!

    // Код запроса разрешения
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backtoMenu = view.findViewById<Button>(R.id.back_to_menu_camera)
        backtoMenu.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FirstFragment())
                .commit()
        }
        // Инициализация ViewBinding
        _binding = ActivityCameraFragmentBinding.bind(view)

        // Проверяем разрешение на использование камеры
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            // Разрешение предоставлено, запускаем камеру
            startCamera()
        } else {
            // Разрешение не предоставлено, запрашиваем его
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Получаем объект ProcessCameraProvider
            val cameraProvider = cameraProviderFuture.get()

            // Настройка камеры
            val preview = Preview.Builder()
                .build()

            // Устанавливаем PreviewView для отображения видеопотока
            preview.setSurfaceProvider(binding.previewView.surfaceProvider)

            // Выбираем камеру (текущая фронтальная камера)
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            try {
                // Отменяем все предыдущие камеры
                cameraProvider.unbindAll()

                // Биндим камеру для отображения видеопотока
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, preview
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Проверяем результат запроса разрешения
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, запускаем камеру
                startCamera()
            } else {
                // Разрешение не получено
                // Можно отобразить сообщение или уведомление
                // Например, показать пользователю уведомление о необходимости разрешений
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Очистка биндинга при уничтожении фрагмента
    }
}
