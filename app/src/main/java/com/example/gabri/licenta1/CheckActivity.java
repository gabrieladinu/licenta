package com.example.gabri.licenta1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CheckActivity extends AppCompatActivity {

    Participants participant;
    TextView qrCoderesult ;
    String key ;
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
                    qrCoderesult.setText("Qr Code : " + barcode.displayValue);
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
//                        setinfo();
//                        qrCodegenerate = "ky" + key1.toString() + "bc" + stringbarcode + "acc";
//                        generateQr(qrCodegenerate);
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


    void setViews() {
        Button qrCodeScan =  (Button)  findViewById(R.id.qrCodeScan);
        qrCoderesult = (TextView) findViewById(R.id.qrCode);

        qrCodeScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scanBarcode(v);
                // apeleaza scanBarcode care apeleaza efectic ScannBarcodeActivity, unde se realizeaza scanarea efectiva
            }
        });

    }
}
