package cm22.ua.pharmanow.fragments;


import android.app.Notification;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cm22.ua.pharmanow.datamodel.Product;
import cm22.ua.pharmanow.R;


public class CreateProduct extends Fragment {

    EditText productName;
    Button productSubmit;
    Spinner pharmaSpinner;
    EditText priceText;


    DatabaseReference databaseProducts;

    public CreateProduct() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        super.onCreate(savedInstanceState);



        View root = inflater.inflate(R.layout.create_product, container, false);

        productName = root.findViewById(R.id.productName);
        priceText = root.findViewById(R.id.editTextPrice);
        productSubmit = root.findViewById(R.id.btnSubmit);
        pharmaSpinner = root.findViewById(R.id.spinnerPharm);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");
        databaseProducts.keepSynced(true);

        productSubmit.setOnClickListener(v -> {
            boolean added = addProduct();
            if(added){
                Toast.makeText(getActivity(), "Product Added ", Toast.LENGTH_LONG).show();

                // Create an explicit intent for an Activity in your app
                Intent intent = new Intent(getActivity(), FirstFragment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(requireContext(), "M_CH_ID");

                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        //.setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_baseline_store_24)
                        //.setTicker("Hearty365")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                        .setContentTitle("Product added")
                        .setContentText("New Item added to the products list.")
                        .setDefaults(Notification.DEFAULT_VIBRATE);
                        //.setContentInfo("Info");

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
// notificationId is a unique int for each notification that you must define
                notificationManager.notify(1, notificationBuilder.build());
            }else{
                Toast.makeText(getActivity(), "Enter a product name", Toast.LENGTH_LONG).show();
            }

        });

        return root;
    }


    private boolean addProduct(){
        String name = productName.getText().toString().trim();
        String pharmacy = pharmaSpinner.getSelectedItem().toString();
        String price = priceText.getText().toString().trim();

        if(price.contains(",")){
            price = price.replace(",",".");
        }

        if(!TextUtils.isEmpty(name)){
            String id = databaseProducts.push().getKey();
            Product product = new Product(id, name, pharmacy,price);
            assert id != null;
            databaseProducts.child(id).setValue(product);


            productName.setText("");
            priceText.setText("");

            return true;

        }else{

            return false;
        }
    }


}