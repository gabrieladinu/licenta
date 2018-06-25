package com.example.gabri.licenta1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class NewEntryActivity1 extends AppCompatActivity {

    TextView resultid;
    TextView sexid;
    TextView ageid;
    Button newIdScann;
    ImageView idImage;
    ImageView faceImage;
    private String mCurrentPhotoPath;
    Bitmap editedBitmap;
    String cnp;
    String sex;
    Button sumbit;
    int year = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
    String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
    String varsta;
    String key1;
    String qrCodegenerated;

    FirebaseStorage storage;
    StorageReference storageReference;

    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entry_activity_1);
        setViews();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.gabri.licenta1",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("calea" , mCurrentPhotoPath) ;
        return image;
    }


    private void setPic() throws Exception {
        // Get the dimensions of the View
        int targetW = idImage.getWidth();
        int targetH = idImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        textInfo(bitmap); /// functie scrisa de mine
//        facedetection(bitmap) ;
        try {
            scanFaces(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        idImage.setImageBitmap(bitmap);

    }

    private void scanFaces(Bitmap bitmap) throws Exception {
        FaceDetector detector = new FaceDetector.Builder(this)
//                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(false)  // true este pt video , false este pt a detecta intr-o poza statica
                .setMode(FaceDetector.FAST_MODE)   // pt a cauta mai rapid
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        if (detector.isOperational() && bitmap != null) {
            editedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), bitmap.getConfig());
            float scale = getResources().getDisplayMetrics().density;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(10, 255, 10));
            paint.setTextSize((int) (14 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3f);
            Canvas canvas = new Canvas(editedBitmap);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            Frame frame = new Frame.Builder().setBitmap(editedBitmap).build();
            SparseArray<Face> faces = detector.detect(frame);

            for (int index = 0; index < faces.size(); ++index) {
                Log.d("++", "++");
                com.google.android.gms.vision.face.Face face = faces.valueAt(index);
                canvas.drawRect(
                        face.getPosition().x - face.getWidth() * 3 / 10,
                        face.getPosition().y - face.getHeight() * 8 / 100,
                        face.getPosition().x + face.getWidth() + face.getWidth() * 2 / 10,
                        face.getPosition().y + face.getHeight() + face.getHeight() / 2, paint);


                int x = (int) face.getPosition().x - (int) face.getWidth() * 5 / 10;
                int y = (int) face.getPosition().y - (int) face.getHeight() * 1 / 10;
                int height = (int) face.getHeight() + (int) face.getHeight() * 50 / 100;
                int width = (int) face.getWidth() + (int) face.getWidth() * 1 / 10;

                // imaginea micuata

                Bitmap smallface = Bitmap.createBitmap(bitmap, x, y, height, width * 2, null, false);
                saveToInternalStorage(smallface);
                Log.d("buletin" , saveToInternalStorage(smallface)) ;
                if (smallface != null) {
                    faceImage.setImageBitmap(smallface);

                }
            }

            if (faces.size() == 0) {
                // nu s-a detectat fata
clearinfo();
            } else {
                setinfo();
            }
        } else {

        }
    }


    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    void setinfo() {
        idImage.setImageBitmap(editedBitmap);
        resultid.setVisibility(View.GONE);
        resultid.setText("Image not found");
        sexid.setVisibility(View.VISIBLE);
        ageid.setVisibility(View.VISIBLE);
        sexid.setText("Gen : " +sex);
        ageid.setText("Varsta : "+varsta);
        sumbit.setEnabled(true);
    }


    void clearinfo() {
        resultid.setVisibility(View.VISIBLE);
        idImage.setImageBitmap(null);
        faceImage.setImageBitmap(null);
    }

    private void textInfo(Bitmap bitmap) {

        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "Could not get the text", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> item = textRecognizer.detect(frame);
            StringBuilder information = new StringBuilder();
            StringBuilder info = new StringBuilder();
            for (int i = 0; i < item.size(); ++i) {

                TextBlock myitem = item.valueAt(i);
                Log.d("cuvat", myitem.getValue());
                String block_text = myitem.getValue().toString();


                if (block_text.toString().indexOf("CNP") >= 0) {
                    Log.d("***", "moor");
                    Log.d("111", block_text.toString());
                    cnp = block_text.toString().replaceAll("\\D+", "");
                    Log.d("cnp", cnp);

                    Character gen = cnp.charAt(0);
                    Log.d("sex", gen.toString());
                    if (gen.equals('2') || gen.equals('6')) {
                        Log.d("sex", "femeie ");
                        sex = "feminin";
                    }
                    if (gen.equals('1') || gen.equals('5')) {
                        Log.d("sex", "Barbat ");
                        sex = "masculin";
                    }
                    int bornyear = Integer.parseInt(cnp.substring(1, 3));
                    // sa nascut inainte de 2000 asica 19xx
                    if (gen.equals('2') || gen.equals('1')) {
                        bornyear = 1900 + bornyear;
                    }
                    if (gen.equals('5') || gen.equals('6')) {
                        bornyear = 2000 + bornyear;
                    }


                    varsta = Integer.toString(year - bornyear);
                    Log.d("varsta", varsta);


                    info.append(myitem.getValue());
                    info.append("  ");
                }

                if (block_text.toString().indexOf("Nume") >= 0) {
                    Log.d("***", "moor");
                    Log.d("111", block_text.toString());
                    info.append(myitem.getValue());
                    info.append("  ");
                }


            }


        }
    }


//    dupa ce se termina activitatea ScannBarcodeActivity , cu ajutorului intent.putExtra se retin informatii din intentul precedent
//    in extra a fost pus setResult(CommonStatusCodes.SUCCESS, intent); ce deternimna daca s-a gasit un code de bare
//    iar daca s-a gasit acesta o sa fe afisat pe ecran cava in data nu exista nici o informatie atunci se fa afisa un text ca nu s-a gasit un barcode


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            galleryAddPic();
            try {
                setPic();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



    void updateinfo (){


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Participants/" + key1);
        myRef.child("sex").setValue(sex);
        myRef.child("age").setValue(varsta);
        myRef.child("register").setValue(timeStamp);
        myRef.child("qrCodegenerated").setValue(qrCodegenerated);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference mountainsRef = storageRef.child(key1.toString()+".jpg");
        faceImage.setDrawingCacheEnabled(true);
        faceImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) faceImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    }

    private void setViews() {
        TextView userEdit = (TextView) findViewById(R.id.step2);
        resultid = (TextView) findViewById(R.id.resultid);
        sexid = (TextView) findViewById(R.id.sexid);
        ageid = (TextView) findViewById(R.id.ageid);


        newIdScann = (Button) findViewById(R.id.idscann);
        sumbit = (Button) findViewById(R.id.submit);

        idImage = (ImageView) findViewById(R.id.idImage);
        faceImage = (ImageView) findViewById(R.id.faceImage);

        Intent intent = getIntent();
        key1 = intent.getStringExtra("key");
        qrCodegenerated = intent.getStringExtra("qrCodeGenerated") ;


        newIdScann.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dispatchTakePictureIntent();

            }
        });
        sumbit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                updateinfo();
                Intent intent = new Intent(getBaseContext(), ThankYouActivity.class);
                startActivity(intent);
            }
        });


    }

}





