package cm22.ua.pharmanow;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


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
        super.onCreate(savedInstanceState);



        View root = inflater.inflate(R.layout.create_product, container, false);

        productName = root.findViewById(R.id.productName);
        priceText = root.findViewById(R.id.editTextPrice);
        productSubmit = root.findViewById(R.id.btnSubmit);
        pharmaSpinner = root.findViewById(R.id.spinnerPharm);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");



        productSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        return root;
    }


    private void addProduct(){
        String name = productName.getText().toString().trim();
        String pharmacy = pharmaSpinner.getSelectedItem().toString();
        String price = priceText.getText().toString().trim();

        if(!TextUtils.isEmpty(name)){
            String id = databaseProducts.push().getKey();
            Product product = new Product(id, name, pharmacy,price);
            databaseProducts.child(id).setValue(product);

            Toast.makeText(getActivity(), "Product Added ", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "Enter a product name", Toast.LENGTH_LONG).show();
        }
    }

}