package com.example.gabri.licenta1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

/**
 * Created by gabri on 1/21/2018.
 */

public class ScannIdCardActivity extends AppCompatActivity {
    SurfaceView cameraPreview;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scann_idcard);
        cameraPreview = (SurfaceView) findViewById(R.id.preview);
        createCameraSource();
    }

    private void createCameraSource() {

//        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();


        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)  // true este pt video , false este pt a detecta intr-o poza statica
                .setMode(FaceDetector.FAST_MODE)   // pt a cauta mai rapid
                .build();

      // contructorul de facedetector

        if (!faceDetector.isOperational()) {
           // se poate intampla cand deschizi prima data aplicatia sa nu fie inca descarcat api.ul de de"tectare
            Log.w("","Face detector dependencies are not yet available.");
        }


        final CameraSource cameraSource = new CameraSource.Builder(getApplicationContext(), faceDetector)
                // .setFacing(CameraSource.CAMERA_FACING_BACK)    // daca vrei camera frontala // ai grija la permisiuni
                .setRequestedPreviewSize(1600, 1024)
                //.setRequestedFps(15.0f)// numarul de  frame(de poze din film) ce va fi trimis procesorului pt a le prelucra
                .setAutoFocusEnabled(true).build();
        // acceseaza propietatile camerasource si o construieste


        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            // ce se intampla cu camera

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(ScannIdCardActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                  return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder()); // camera propiruzisa a telefonului
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }
            //override method ce trebuuie pt ca arata cate frame o sa fie pntru imagie

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop(); // camera propiu-zisa a telefonului
            }
        });





        faceDetector.setProcessor(new Detector.Processor<Face>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Face> detections) {
                final SparseArray<Face> face = detections.getDetectedItems();

                if (face.size() > 0) {

                    Intent intent = new Intent();
                     intent.putExtra("face", "yes"); // pune informatia pentru a fi putea transferata din intenul actual
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();
                }

            }

        });
    }
}
