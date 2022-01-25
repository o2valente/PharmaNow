package cm22.ua.pharmanow.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.ScanMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cm22.ua.pharmanow.datamodel.Product;
import cm22.ua.pharmanow.datamodel.Purchase;
import cm22.ua.pharmanow.R;
import cm22.ua.pharmanow.adapters.ToDeliverAdapter;

public class ScannerFragment extends Fragment {

    private CodeScanner mCodeScanner;
    TextView tv_TextView;
    DatabaseReference databasePurchase;
    Purchase purchase;
    ToDeliverAdapter adapter;
    List<Product> productsToDeliver = new ArrayList<>();

    public ScannerFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        Objects.requireNonNull(activity).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        View root = inflater.inflate(R.layout.scanner, container, false);
        // Lookup the recyclerview in activity layout
        RecyclerView rvToDeliver = root.findViewById(R.id.rvToDeliver);
        tv_TextView = root.findViewById(R.id.tv_textView);
        if (!(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                    requireContext(), Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions((Activity) requireContext(),
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

        mCodeScanner.setDecodeCallback(result -> activity.runOnUiThread(() -> {
            databasePurchase.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dsp : snapshot.getChildren()) {
                        if (Objects.requireNonNull(dsp.child("id").getValue()).toString().equals(result.getText())) {
                            purchase = dsp.getValue(Purchase.class);
                            productsToDeliver = Objects.requireNonNull(purchase).getProductsBought();
                            if (!purchase.isConfirmed()) {
                                tv_TextView.setText("Purchase (" + purchase.getId() + ") confirmed for user " + purchase.getUser());
                                confirmBuy(purchase.getId());
                            } else {
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

        }));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());

        return root;
    }

    private void confirmBuy(String id){
        DatabaseReference dbPurchase = FirebaseDatabase.getInstance().getReference("Purchases");
        dbPurchase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dsp : snapshot.getChildren()){
                    if(Objects.requireNonNull(dsp.child("id").getValue()).toString().equals(id)){
                        dbPurchase.child(id).child("confirmed").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            mCodeScanner.releaseResources();
        super.onPause();
    }
}
