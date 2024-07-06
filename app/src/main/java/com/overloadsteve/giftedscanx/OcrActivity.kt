package com.overloadsteve.giftedscanx

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import androidx.core.content.FileProvider
import android.net.Uri
import android.os.Environment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class OcrActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var captureImageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var snapButton: ImageButton
    private lateinit var detectButton: Button
    private lateinit var readButton: Button
    private lateinit var stopButton: Button
    private lateinit var clearbutton: Button
    private lateinit var backbutton: ImageButton

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val CAMERA_PERMISSION_CODE = 200
    }

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ocr_main)

        captureImageView = findViewById(R.id.idx)
        resultTextView = findViewById(R.id.idx1)
        snapButton = findViewById(R.id.idx3)
        detectButton = findViewById(R.id.idx2)
        readButton = findViewById(R.id.idx4)
        stopButton = findViewById(R.id.idx5)
        clearbutton = findViewById(R.id.clear_text)
        backbutton = findViewById(R.id.backButton)
        
        textToSpeech = TextToSpeech(this, this)

        clearbutton.setOnClickListener() {
            resultTextView.text = "Your Result will appear here"
        }

        detectButton.setOnClickListener {
            // Call the function to perform OCR
            if (imageFile != null) {
                detectText(FileProvider.getUriForFile(this, "com.overloadsteve.giftedscanx.fileprovider", imageFile!!))
            } else {
                showToast("Capture an image first.")
            }
        }

        backbutton.setOnClickListener{
            back()
        }

        snapButton.setOnClickListener {
            if (checkCameraPermission()) {
                captureImage()
            } else {
                requestCameraPermission()
            }
        }

        stopButton.setOnClickListener {
            stopTextToSpeech()
        }

        readButton.setOnClickListener {
            val text = resultTextView.text.toString()
            speakOut(text)
        }
    }

    private var imageFile: File? = null

    private fun back() {
        val intent = Intent(this@OcrActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun stopTextToSpeech() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
            showToast("Text-to-Speech stopped.")
        } else {
            showToast("Text-to-Speech is not currently speaking.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale.getDefault()
            val result = textToSpeech.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                showToast("Text-to-Speech language is not supported.")
            }
        } else {
            showToast("Text-to-Speech initialization failed.")
        }
    }


    private fun speakOut(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@OcrActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }

    private fun captureImage() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // Get the directory for the app's private pictures directory
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Create a file in the app's pictures directory
        imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)

        // Create the file URI using FileProvider
        val photoURI: Uri = FileProvider.getUriForFile(this, "com.overloadsteve.giftedscanx.fileprovider", imageFile!!)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                detectText(it)
                val captureImageView = findViewById<ImageView>(R.id.idx) // Find the ImageView by ID
                captureImageView.setImageURI(it) // Set the image in the ImageView
            }
        }
    }

    private fun detectText(imageUri: Uri) {
        val image = InputImage.fromFilePath(this, imageUri)

        val options = TextRecognizerOptions.Builder().build()

        val recognizer = TextRecognition.getClient(options)
        recognizer.process(image)
            .addOnSuccessListener { text ->
                val resultStringBuilder = StringBuilder()
                for (block in text.textBlocks) {
                    val blockText = block.text
                    resultStringBuilder.append(blockText).append("\n")
                }
                resultTextView.text = resultStringBuilder.toString()
            }
            .addOnFailureListener { e ->
                showToast("Failed to detect text from image: ${e.message}")
            }
    }

}
