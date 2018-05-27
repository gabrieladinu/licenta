package com.example.gabri.licenta1;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.security.Permission;

public  class MainActivity extends RunTimePermission {
public static final int REQUEST_PERMISSION=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        setViews();
        // seteaza view-ul , adica elementele de design butoane, texviews

        requestAppPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                //todo alte permisiuni
                },
                R.string.messagePermission,REQUEST_PERMISSION);
    }

    //exista clasa Runtimepermission unde s-au se face in spate ce permisiuni ai nevoie
    // aici pui cu virgula ce permisiuni ai nevoie ,
    // nu uitat ca trebuie sa le pui si aici si in manifest

    @Override
    public void onPermissionsGranted(int requestCode) {
        //Do anything when permisson granted
        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
    }
// TODO: 2/11/2018  porti sa stergi tost.ul de mai sus
//    sa nu iti apara mereu dupa ce ai instalat aplicatia pentru prim data , daca mai intri o data sa iti zica ca permisiunea a fost data

    private void setViews() {
//        TextView userEdit = (TextView) findViewById(R.id.titleChoise);
        Button newEntry = (Button) findViewById(R.id.newEntry);
        Button userScann = (Button) findViewById(R.id.checkEntry);

        newEntry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NewEntryActivity.class);
                startActivity(intent);

            }
        });

        userScann.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               //TODO
            }
        });
    }
}