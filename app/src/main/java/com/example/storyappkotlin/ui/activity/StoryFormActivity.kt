package com.example.storyappkotlin.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.storyappkotlin.BuildConfig
import com.example.storyappkotlin.R
import com.example.storyappkotlin.data.remote.Result
import com.example.storyappkotlin.databinding.ActivityStoryFormBinding
import com.example.storyappkotlin.ui.viewmodel.StoryViewModel
import com.example.storyappkotlin.ui.viewmodel.ViewModelFactory
import com.example.storyappkotlin.utils.CustomLoadingDialog
import com.example.storyappkotlin.utils.SharedPreferenceUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class StoryFormActivity : AppCompatActivity() {

    private val TAG = StoryFormActivity::class.java.simpleName

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var binding: ActivityStoryFormBinding
    private lateinit var pref: SharedPreferenceUtil
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var launcherCamera: ActivityResultLauncher<Uri>
    private lateinit var launcherGallery: ActivityResultLauncher<PickVisualMediaRequest>
    private var currentImageUri: Uri? = null
    private var lat: Double? = null
    private var lon: Double? = null

    private val page = 1
    private val size = 10
    private val location = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPreferenceUtil(this)
        loadingDialog = CustomLoadingDialog(this)

        val toolbar = binding.appBar.toolbarTitleAppBar
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.story_form_appbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        launcherCamera =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                if (isSuccess) {
                    binding.ivPhoto.setImageURI(currentImageUri)
                } else {
                    currentImageUri = null
                }
            }

        launcherGallery =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val mimeType = contentResolver.getType(uri)
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        currentImageUri = uri
                        binding.ivPhoto.setImageURI(uri)
                    } else {
                        Toast.makeText(this, "Selected media is not an image", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
                }
            }

        binding.cbUseLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableMyLocation()
            } else {
                lat = null
                lon = null
            }
        }

        val factory = ViewModelFactory.getInstance(this)
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

        storyViewModel.getUploadStoryResult().observe(this) { result ->
            when (result) {
                is Result.Loading -> loadingDialog.show()
                is Result.Success -> {
                    loadingDialog.dismiss()
                    val message = result.data.message
                    Toast.makeText(this, getString(R.string.success) + " : $message", Toast.LENGTH_SHORT).show()
                    val token = "Bearer ${pref.getToken()}"
                    storyViewModel.getStories(this, token, page, size, location)
                    finish()
                }
                is Result.Error -> {
                    loadingDialog.dismiss()
                    Toast.makeText(this, getString(R.string.failed) + " : ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.etDescription.apply {
            minHeight = 300
            maxHeight = 300
            isVerticalScrollBarEnabled = true
        }

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnUpload.setOnClickListener { uploadStory() }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    binding.cbUseLocation.isChecked = true
                    Log.d(TAG, "Permission granted. Location: $lat, $lon")
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.cbUseLocation.isChecked = false
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest())
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let { launcherCamera.launch(it) }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    Log.d(TAG, "Location: $lat, $lon")
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                    binding.cbUseLocation.isChecked = false
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1001
            )
        }
    }

    private fun uploadStory() {
        val desc = binding.etDescription.text?.toString().orEmpty()
        val token = "Bearer ${pref.getToken()}"

        var file: File? = null
        if (currentImageUri != null) {
            file = try {
                getFileFromUri(this, currentImageUri!!)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        Log.d(TAG, "token = $token")
        Log.d(TAG, "desc = $desc")
        Log.d(TAG, "file = ${file?.path}")

        storyViewModel.uploadStory(this, token, desc, file, lat?.toFloat(), lon?.toFloat())
    }

    private fun getImageUri(context: Context): Uri {
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp = System.currentTimeMillis().toString()
        val imageFile = File(filesDir, "story_app_$timeStamp.jpg")

        imageFile.parentFile?.let { parent ->
            if (!parent.exists()) {
                val isCreated = parent.mkdirs()
                Log.d(TAG, "isCreateFileSuccess : $isCreated")
            }
        }

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            imageFile
        )
    }

    private fun getFileFromUri(context: Context, uri: Uri): File {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val outputDir = context.cacheDir
        val outputFile = File.createTempFile("compressed_", ".jpg", outputDir)

        var quality = 100
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream)

        while (outStream.toByteArray().size / 1024 > 1024 && quality > 10) {
            outStream.reset()
            quality -= 5
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream)
        }

        FileOutputStream(outputFile).use { fos ->
            fos.write(outStream.toByteArray())
            fos.flush()
        }

        return outputFile
    }
}