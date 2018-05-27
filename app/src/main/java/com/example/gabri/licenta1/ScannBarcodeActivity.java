package com.example.gabri.licenta1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by gabri on 12/15/2017.
 */

public class ScannBarcodeActivity extends AppCompatActivity {
    SurfaceView cameraPreview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scann_barcode);
        cameraPreview = (SurfaceView) findViewById(R.id.preview);
        createCameraSource();
    }

    private void createCameraSource() {
        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build(); // constructor de cod de bara
        final CameraSource cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                // .setFacing(CameraSource.CAMERA_FACING_BACK)    // daca vrei camera frontala // ai grija la permisiuni
                .setRequestedPreviewSize(1600, 1024)
                //.setRequestedFps(15.0f)// numarul de  frame(de poze din film) ce va fi trimis procesorului pt a le prelucra
                .setAutoFocusEnabled(true).build();
        // acceseaza propietatile camerasource si o construieste


        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            // ce se intampla cu camera

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(ScannBarcodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
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

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcodes.valueAt(0)); // pune informatia pentru a fi putea transferata din intenul actual
                    //pune primul barcode gasit
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();
                }
            }
            // ce primeste bar code detectorul
            // eu am facut ca o data ce gaseste un cod de bara sa iasa din activitate
            // sa retina daca (barcodes.size() > 0 , adica daca a gasit un cod de bara si sa retina informatia
        });
    }
}
