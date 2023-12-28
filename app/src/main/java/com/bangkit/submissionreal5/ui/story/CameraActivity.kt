package com.bangkit.submissionreal5.ui.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.submissionreal5.R
import com.bangkit.submissionreal5.databinding.ActivityCameraBinding
import com.bangkit.submissionreal5.utility.FileUtility
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private var _binding: ActivityCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val imageFiles = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initGallery()
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        binding.btnShutter.setOnClickListener {
            takePhoto()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnSwitch.setOnClickListener {
            cameraSelector =
                if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().setTargetResolution(Size(480, 720)).build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview)
            } catch (e: Exception) {
                Log.e("Error", "Error starting camera: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val outputDirectory = getOutputDirectory()
        val photoFileName = "photo_${System.currentTimeMillis()}.jpg"
        val photoFile = File(outputDirectory, photoFileName)
        imageFiles.add(photoFile)

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Image saved successfully

                    val intent = Intent(this@CameraActivity, UploadStoryActivity::class.java)
                    intent.putExtra(UploadStoryActivity.EXTRA_PHOTO_RESULT, photoFile)
                    intent.putExtra(
                        UploadStoryActivity.EXTRA_CAMERA_MODE,
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    this@CameraActivity.finish()
                    startActivity(intent)

                }

                override fun onError(exception: ImageCaptureException) {
                    // Error saving image
                    val errorMsg = "Error capturing image: ${exception.message}"

                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }

                    Log.e("Error", errorMsg, exception)
                }
            }
        )
    }


    private fun initGallery() {
        openGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.i("TEST_GALERY", "Galeri berhasil dipilih dan akan mengarah ke new")
                val selectedImg: Uri = result.data?.data as Uri
                val myFile = FileUtility.uriToFile(selectedImg, this@CameraActivity)
                val intent = Intent(this@CameraActivity, UploadStoryActivity::class.java)
                intent.putExtra(UploadStoryActivity.EXTRA_PHOTO_RESULT, myFile)
                intent.putExtra(
                    UploadStoryActivity.EXTRA_CAMERA_MODE,
                    cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                )
                this@CameraActivity.finish()
                startActivity(intent)
            }
        }
    }




    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        openGalleryLauncher.launch(intent)
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { file ->
            File(file, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir ?: filesDir
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}