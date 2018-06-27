package com.example.gabri.licenta1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.ToDoubleBiFunction;

public class CheckActivity extends AppCompatActivity {

    Participants participant;
    TextView qrCoderesult;
    String key;
    ImageView imageId;
    Button chechIn;
    Button problem;
    Button checkOut;

    TextView firstName;
    TextView lastName;
    TextView sex;
    TextView age;
    TextView phone;
    TextView mail;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        setViews();


    }

    public void scanBarcode(View v) {
        Intent intent = new Intent(this, ScannBarcodeActivity.class);
        startActivityForResult(intent, 0);

        // se deschide un nou intet, ScannBarcodeActivity ce scanneza codul de bare si proceseaza efectiv informatia
        //termina activitatea, un fel de retunr 0, s-a efectuat cu succes
    }


//    dupa ce se termina activitatea ScannBarcodeActivity , cu ajutorului intent.putExtra se retin informatii din intentul precedent
//    in extra a fost pus setResult(CommonStatusCodes.SUCCESS, intent); ce deternimna daca s-a gasit un code de bare
//    iar daca s-a gasit acesta o sa fe afisat pe ecran cava in data nu exista nici o informatie atunci se fa afisa un text ca nu s-a gasit un barcode


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (requestCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    qrCoderesult.setText("Qr Code: " + barcode.displayValue);
                    String stringqrcode = barcode.displayValue.toString();
                    search(stringqrcode);


                } else {
                    qrCoderesult.setText("BarCode not found!");
//                    next.setEnabled(false);
//                    clearinfo();
                }
            }
        }
    }

    void search(final String stringqrcode) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Participants/1");
//    Participants pers = new Participants("","","","","","","","","","","","","");
//    myRef.setValue(pers);
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("Participants").orderByChild("qrCodegenerated").equalTo(stringqrcode);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getChildren() != null && dataSnapshot.getValue() != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        participant = postSnapshot.getValue(Participants.class);
                        key = postSnapshot.getKey();
                        Log.d("key", "  " + key);
                        Log.d("search qrcode", stringqrcode + "    Value is: " + participant);
                        saveimg();
                        setinfo();
                        choise(participant);


                    }
                } else {
                    Log.d("ceva", "nu exista in baza de date ");
//                    nobarcodeinfo.setVisibility(View.VISIBLE);
//                    nobarcodeinfo.setText("Nu este un bilet valid !");
//                    next.setEnabled(false);
//                    clearinfo();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    void setinfo() {


        firstName.setVisibility(View.VISIBLE);
        lastName.setVisibility(View.VISIBLE);
        age.setVisibility(View.VISIBLE);
        sex.setVisibility(View.VISIBLE);
        firstName.setText(participant.getFirstName());
        lastName.setText(participant.getLastName());
        sex.setText(participant.getSex());
        age.setText(participant.getAge());
        phone.setVisibility(View.VISIBLE);
        phone.setText(participant.getPhone());
        mail.setVisibility(View.VISIBLE);
        mail.setText(participant.getMail());
    }

    void saveimg() {
//        gs://licenta-73791.appspot.com/1.jpg


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Log.d("uri", uri.toString());
                Glide.with(getApplicationContext())
                        .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                        .load(uri)
                        .into(imageId);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


    }

    void choise(Participants participant) {
        problem.setEnabled(true);
//        problem.setBackgroundColor(getResources().getColor(R.color.button));
        problem.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybutton));
        problem.setTextColor(getResources().getColor(R.color.buttontext));
        chechIn.setEnabled(true);
//        chechIn.setBackgroundColor(getResources().getColor(R.color.button));
        chechIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybutton));
        chechIn.setTextColor(getResources().getColor(R.color.buttontext));
        checkOut.setEnabled(false);
//        checkOut.setBackgroundColor(getResources().getColor(R.color.buttonnotenable));
        checkOut.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybuttonnotenable));
        checkOut.setTextColor(getResources().getColor(R.color.buttonnotenabletext));

        if ("1".equals(participant.getCheckIn())) {
            chechIn.setEnabled(false);
//            chechIn.setBackgroundColor(getResources().getColor(R.color.buttonnotenable));
            chechIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybuttonnotenable));
            chechIn.setTextColor(getResources().getColor(R.color.buttonnotenabletext));
            checkOut.setEnabled(true);
//            checkOut.setBackgroundColor(getResources().getColor(R.color.button));
            checkOut.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybutton));
            checkOut.setTextColor(getResources().getColor(R.color.buttontext));
        }
        if ("1".equals(participant.getCheckOut())) {
            chechIn.setEnabled(true);
//            chechIn.setBackgroundColor(getResources().getColor(R.color.button));
            chechIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybutton));
            chechIn.setTextColor(getResources().getColor(R.color.buttontext));
            checkOut.setEnabled(false);
//            checkOut.setBackgroundColor(getResources().getColor(R.color.buttonnotenable));
            checkOut.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybuttonnotenable));
            checkOut.setTextColor(getResources().getColor(R.color.buttonnotenabletext));
        }


    }


    void checkIn() {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Participants/" + key);

        myRef.child("checkIn").setValue("1");
        myRef.child("checkOut").setValue("0");
        myRef.child("checkInData").setValue(timeStamp);


    }

    void checkOut() {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Participants/" + key);

        myRef.child("checkIn").setValue("0");
        myRef.child("checkOut").setValue("1");
        myRef.child("checkOutData").setValue(timeStamp);

    }


    void report() {
        checkOut.setEnabled(true);
//        checkOut.setBackgroundColor(getResources().getColor(R.color.button));
        checkOut.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybutton));
        checkOut.setTextColor(getResources().getColor(R.color.buttontext));
        chechIn.setEnabled(true);
//        chechIn.setBackgroundColor(getResources().getColor(R.color.button));
        chechIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybutton));
        chechIn.setTextColor(getResources().getColor(R.color.buttontext));

        String problema = participant.getProblem();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Participants/" + key);
        myRef.child("problem").setValue("1");

    }
void clearinfo(){
    checkOut.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybuttonnotenable));
    chechIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybuttonnotenable));
    problem.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybuttonnotenable));
    chechIn.setEnabled(false);
    checkOut.setEnabled(false);
    problem.setEnabled(false);
    firstName.setVisibility(View.VISIBLE);
    lastName.setVisibility(View.VISIBLE);
    age.setVisibility(View.VISIBLE);
    sex.setVisibility(View.VISIBLE);
    phone.setVisibility(View.VISIBLE);
    mail.setVisibility(View.VISIBLE);
    firstName.setText("");
    lastName.setText("");
    age.setText("");
    sex.setText("");
    phone.setText("");
    mail.setText("");
    imageId.setImageBitmap(null);
}

    void setViews() {
        Button qrCodeScan = (Button) findViewById(R.id.qrCodeScan);
        qrCoderesult = (TextView) findViewById(R.id.qrCode);
        firstName = (TextView) findViewById(R.id.firstName);
        lastName = (TextView) findViewById(R.id.lastName);
        sex = (TextView) findViewById(R.id.sex);
        age = (TextView) findViewById(R.id.age);
        phone = (TextView) findViewById(R.id.phone);
        mail = (TextView) findViewById(R.id.mail);

        imageId = (ImageView) findViewById(R.id.imageId);
        chechIn = (Button) findViewById(R.id.checkin);
        problem = (Button) findViewById(R.id.report);
        checkOut = (Button) findViewById(R.id.checkout);
        chechIn.setEnabled(false);
//        chechIn.setBackgroundColor(getResources().getColor(R.color.buttonnotenable));
        chechIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybuttonnotenable));
        chechIn.setTextColor(getResources().getColor(R.color.buttonnotenabletext));
        checkOut.setEnabled(false);
//        checkOut.setBackgroundColor(getResources().getColor(R.color.buttonnotenable));
        checkOut.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybuttonnotenable));
        checkOut.setTextColor(getResources().getColor(R.color.buttonnotenabletext));
        problem.setEnabled(false);
//        problem.setBackgroundColor(getResources().getColor(R.color.buttonnotenable));
        problem.setBackgroundDrawable(getResources().getDrawable(R.drawable.mybuttonnotenable));
        problem.setTextColor(getResources().getColor(R.color.buttonnotenabletext));

        qrCodeScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearinfo();
                scanBarcode(v);
                // apeleaza scanBarcode care apeleaza efectic ScannBarcodeActivity, unde se realizeaza scanarea efectiva
            }
        });
        chechIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkIn();

            }
        });

        problem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                report();
            }
        });
        checkOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkOut();
            }
        });

    }
}
