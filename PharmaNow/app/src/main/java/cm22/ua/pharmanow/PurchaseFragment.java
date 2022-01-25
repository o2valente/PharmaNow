package cm22.ua.pharmanow;


import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PurchaseFragment extends Fragment {

    DatabaseReference databasePurchases;
    PurchaseAdapter adapter;
    //private FirebaseAuth auth;


    public PurchaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_purchases,
                container, false);
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setHasOptionsMenu(true);

        // Lookup the recyclerview in activity layout
        RecyclerView rvProducts = rootView.findViewById(R.id.rvPurchases);

        String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        databasePurchases = FirebaseDatabase.getInstance().getReference("Purchases");
        databasePurchases.keepSynced(true);

        List<Purchase> purchases = new ArrayList<>();


        databasePurchases.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dsp : snapshot.getChildren()) {
                    if(Objects.requireNonNull(dsp.child("user").getValue()).toString().equals(userEmail))
                        purchases.add(dsp.getValue(Purchase.class));
                }
                // Create adapter passing in the sample user data
                adapter = new PurchaseAdapter(purchases);
                // Attach the adapter to the recyclerview to populate items
                rvProducts.setAdapter(adapter);
                // Set layout manager to position the items
                rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Could not read data!", Toast.LENGTH_SHORT).show();
            }
        });



        return rootView;
    }


}