package cm22.ua.pharmanow;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

public class ScannerFragment extends Fragment {

    private CodeScanner mCodeScanner;
    TextView tv_TextView;
    DatabaseReference databasePurchase;
    Purchase purchase;
    ToDeliverAdapter adapter;
    List<Product> productsToDeliver = new ArrayList<Product>();

    public ScannerFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.scanner, container, false);
        // Lookup the recyclerview in activity layout
        RecyclerView rvToDeliver = (RecyclerView) root.findViewById(R.id.rvToDeliver);
        tv_TextView = root.findViewById(R.id.tv_textView);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        }else{

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                    getContext(), Manifest.permission.CAMERA)) {


            } else {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.CAMERA},
                        1);
            }

        }
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        databasePurchase = FirebaseDatabase.getInstance().getReference("Purchases");
        databasePurchase.keepSynced(true);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        databasePurchase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dsp : snapshot.getChildren()){
                                    if(dsp.child("id").getValue().toString().equals(result.getText())){
                                        purchase = dsp.getValue(Purchase.class);
                                        productsToDeliver = purchase.getProductsBought();
                                        if(!purchase.isConfirmed()){
                                            tv_TextView.setText("Purchase (" + purchase.getId() + ") confirmed for user " + purchase.getUser());
                                            confirmBuy(purchase.id);
                                        }else{
                                            tv_TextView.setText("Purchase (" + purchase.getId() + ") already confirmed for user " + purchase.getUser());
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // Create adapter passing in the sample user data
                        adapter = new ToDeliverAdapter(productsToDeliver);
                        // Attach the adapter to the recyclerview to populate items
                        rvToDeliver.setAdapter(adapter);
                        // Set layout manager to position the items
                        rvToDeliver.setLayoutManager(new LinearLayoutManager(getContext()));

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        return root;
    }

    private void confirmBuy(String id){
        DatabaseReference dbPurchase = FirebaseDatabase.getInstance().getReference("Purchases");
        dbPurchase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dsp : snapshot.getChildren()){
                    if(dsp.child("id").getValue().toString().equals(id)){
                        dbPurchase.child(id).child("confirmed").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            mCodeScanner.releaseResources();
        super.onPause();
    }
}
