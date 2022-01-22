package cm22.ua.pharmanow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

public class ShoppingCartFragment extends Fragment {

    DatabaseReference databaseProducts;
    private String userId;
    Button btnRemove;
    Button btnBuyAll;
    TextView totalCost;


    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shopping_cart,
                container, false);

        // Lookup the recyclerview in activity layout
        RecyclerView rvProducts = (RecyclerView) rootView.findViewById(R.id.rvShoppingCartProducts);

        //get user
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Initialize products
        databaseProducts = FirebaseDatabase.getInstance().getReference("ShoppingCart");

        btnRemove = inflater.inflate(R.layout.item_shoppingcart_product, container, false).findViewById(R.id.sc_remove_button);

        totalCost = rootView.findViewById(R.id.sc_totalCost);

        btnBuyAll = rootView.findViewById(R.id.sc_BuyAll);

        List<Product> products = new ArrayList<>();

        databaseProducts.child(userId).child("productsList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dsp : snapshot.getChildren()) {
                    //System.out.println(dsp.child("productName").getValue());
                    /*String id = dsp.child("productId").getValue().toString();
                    String name = dsp.child("productName").getValue().toString();
                    String pharma = dsp.child("productPharma").getValue().toString();*/
                    //products.add(new Product(id, name, pharma));
                    products.add(dsp.getValue(Product.class));
                }



                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Estou aqui");
                        Fragment newFragment = new ShoppingCartFragment();
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.flContent, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });

                double totalCosTemp=0;
                for(Product p : products) totalCosTemp += Double.parseDouble(p.price);
                totalCost.setText("Total: " + String.valueOf(totalCosTemp));

                // Create adapter passing in the sample user data
                ShoppingCartProductAdapter adapter = new ShoppingCartProductAdapter(products);
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


        btnBuyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseProducts.child(userId).child("productsList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dsp : snapshot.getChildren()){
                            System.out.println(dsp.getValue().toString());
                            dsp.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });



        return rootView;
    }
}
