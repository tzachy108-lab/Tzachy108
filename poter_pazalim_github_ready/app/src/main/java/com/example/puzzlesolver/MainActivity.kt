package com.example.puzzlesolver

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.ORB
import org.opencv.imgproc.Imgproc
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var btnPuzzle: Button
    private lateinit var btnPiece: Button
    private lateinit var btnMatch: Button
    private lateinit var ivPuzzle: ImageView
    private lateinit var ivPiece: ImageView
    private lateinit var tvResult: TextView

    private var puzzleBitmap: Bitmap? = null
    private var pieceBitmap: Bitmap? = null

    private val pickImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bm = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            if (requestCodeSelecting == SELECT_PUZZLE) {
                puzzleBitmap = bm
                ivPuzzle.setImageBitmap(bm)
            } else {
                pieceBitmap = bm
                ivPiece.setImageBitmap(bm)
            }
        }
    }

    private var requestCodeSelecting = 0
    private val SELECT_PUZZLE = 1
    private val SELECT_PIECE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPuzzle = findViewById(R.id.btnPuzzle)
        btnPiece = findViewById(R.id.btnPiece)
        btnMatch = findViewById(R.id.btnMatch)
        ivPuzzle = findViewById(R.id.ivPuzzle)
        ivPiece = findViewById(R.id.ivPiece)
        tvResult = findViewById(R.id.tvResult)

        if (!OpenCVLoader.initDebug()) {
            tvResult.text = "OpenCV failed to initialize."
        } else {
            tvResult.text = "OpenCV loaded."
        }

        btnPuzzle.setOnClickListener {
            requestCodeSelecting = SELECT_PUZZLE
            pickImageFromGallery.launch("image/*")
        }
        btnPiece.setOnClickListener {
            requestCodeSelecting = SELECT_PIECE
            pickImageFromGallery.launch("image/*")
        }
        btnMatch.setOnClickListener {
            if (puzzleBitmap == null || pieceBitmap == null) {
                tvResult.text = "Select both puzzle and piece."
            } else {
                tvResult.text = matchPieceToPuzzle(puzzleBitmap!!, pieceBitmap!!)
            }
        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val needed = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.CAMERA)
        }
        if (needed.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 100)
        }
    }

    private fun matchPieceToPuzzle(puzzleBmp: Bitmap, pieceBmp: Bitmap): String {
        try {
            val matPuzzle = Mat()
            val matPiece = Mat()
            Utils.bitmapToMat(puzzleBmp, matPuzzle)
            Utils.bitmapToMat(pieceBmp, matPiece)
            Imgproc.cvtColor(matPuzzle, matPuzzle, Imgproc.COLOR_BGR2GRAY)
            Imgproc.cvtColor(matPiece, matPiece, Imgproc.COLOR_BGR2GRAY)

            val orb = ORB.create()
            val kp1 = org.opencv.core.MatOfKeyPoint()
            val kp2 = org.opencv.core.MatOfKeyPoint()
            val desc1 = Mat()
            val desc2 = Mat()
            orb.detectAndCompute(matPuzzle, Mat(), kp1, desc1)
            orb.detectAndCompute(matPiece, Mat(), kp2, desc2)

            if (desc1.empty() || desc2.empty()) return "No strong features found."

            val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)
            val matches = java.util.ArrayList<org.opencv.core.MatOfDMatch>()
            matcher.knnMatch(desc2, desc1, matches, 2)

            var good = 0
            for (m in matches) {
                val am = m.toArray()
                if (am.size >= 2) {
                    val m1 = am[0]
                    val m2 = am[1]
                    if (m1.distance < 0.75 * m2.distance) good++
                }
            }

            val total = matches.size
            val score = if (total > 0) (good.toDouble() / total * 100.0).roundToInt() else 0
            return "Match score: $score%  (good: $good / $total)"
        } catch (e: Exception) {
            return "Error: ${e.localizedMessage}"
        }
    }
}
