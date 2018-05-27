package com.example.gabri.licenta1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Created by gabri on 1/7/2018.
 */

public class NewEntryActivity extends AppCompatActivity {
    TextView barcodeinfo;
    TextView nobarcodeinfo;
    TextView informationId;
    Button next;
    EditText firstname;
    EditText lastname;
    EditText phone;
    EditText mail;
    ImageView Qr;

    Participants participant;
    String key1 ;
    String qrCodegenerate ;
    FirebaseDatabase database;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entry_activity);
        setViews();
        // setview , setare toate elementele de design


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
                    barcodeinfo.setText("Barcode : " + barcode.displayValue);
                    String stringbarcode = barcode.displayValue.toString();
                    search(stringbarcode);

                } else {
                    barcodeinfo.setText("BarCode not found!");
                    next.setEnabled(false);
                    clearinfo();
                }
            }
        }
    }

    void search(final String stringbarcode) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Participants/1");
//    Participants pers = new Participants("","","","","","","","","","","","","");
//    myRef.setValue(pers);
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("Participants").orderByChild("barCode").equalTo(stringbarcode);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getChildren() != null && dataSnapshot.getValue() != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        participant = postSnapshot.getValue(Participants.class);
                        key1 = postSnapshot.getKey();
                        Log.d ("key", "  "+  key1 );
                        Log.d("search", stringbarcode + "    Value is: " + participant);
                        setinfo() ;
                        qrCodegenerate = "ky" + key1.toString() + "bc" + stringbarcode + "acc" ;
                        generateQr(qrCodegenerate);
                    }
                } else {
                    Log.d("ceva", "nu exista in baza de date ");
                    nobarcodeinfo.setVisibility(View.VISIBLE);
                    nobarcodeinfo.setText("Nu este un bilet valid !");
                    next.setEnabled(false);
                    clearinfo();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }
void updateinfo (){

     String newfirstname  = firstname.getText().toString();
    String newlastname  = lastname.getText().toString();
    String newphone  = phone.getText().toString();
    String newmail  = mail.getText().toString();

    database = FirebaseDatabase.getInstance();
      DatabaseReference myRef = database.getReference("Participants/" + key1);
      myRef.child("phone").setValue(newphone);
    myRef.child("mail").setValue(newmail);
    myRef.child("lastName").setValue(newlastname);
    myRef.child("firstName").setValue(newfirstname);

}
 void generateQr (String qrString){

     String text=qrString ; // Whatever you need to encode in the QR code
     MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

     try {
         BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
         BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
         Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

         Qr.setVisibility(View.VISIBLE);
         Qr.setImageBitmap(bitmap);

     } catch (WriterException e) {
         e.printStackTrace();
     }
 }


    private void setViews() {
        TextView userEdit = (TextView) findViewById(R.id.step1);
        Button barcodescan = (Button) findViewById(R.id.barcodescan);

        barcodeinfo = (TextView) findViewById(R.id.barcode);
        nobarcodeinfo = (TextView) findViewById(R.id.nobarcode);
        next = (Button) findViewById(R.id.next);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        phone = (EditText) findViewById(R.id.phone);
        mail = (EditText) findViewById(R.id.mail);
        Qr = (ImageView) findViewById(R.id.QR) ;




        barcodescan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nobarcodeinfo.setVisibility(View.GONE);
                scanBarcode(v);
                // apeleaza scanBarcode care apeleaza efectic ScannBarcodeActivity, unde se realizeaza scanarea efectiva
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateinfo();
                Intent intent = new Intent(getBaseContext(), NewEntryActivity1.class);
                intent.putExtra("key", key1);
                intent.putExtra("qrCodeGenerated" , qrCodegenerate);
                startActivity(intent);

            }
        });

    }
    void setinfo (){

        next.setEnabled(true);
        firstname.setVisibility(View.VISIBLE);
        lastname.setVisibility(View.VISIBLE);
        mail.setVisibility(View.VISIBLE);
        phone.setVisibility(View.VISIBLE);
        firstname.setText(participant.getFirstName() );
        lastname.setText(participant.getLastName() );
        mail.setText(participant.getMail());
        phone.setText(participant.getPhone());
    }
    void clearinfo (){
        firstname.setVisibility(View.INVISIBLE);
        lastname.setVisibility(View.INVISIBLE);
        mail.setVisibility(View.INVISIBLE);
        phone.setVisibility(View.INVISIBLE);
        Qr.setVisibility(View.INVISIBLE);
        firstname.setText("");
        lastname.setText("");
        mail.setText("");
        phone.setText("");
       Qr.setImageBitmap(null);

    }

}





