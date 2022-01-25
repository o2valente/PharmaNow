package cm22.ua.pharmanow;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShoppingCartFragment extends Fragment implements OnRemoveCartProduct{

    DatabaseReference databaseProducts;
    DatabaseReference databasePurchases;
    private String userId;
    Button btnRemove;
    Button btnBuyAll;
    TextView totalCost;
    ShoppingCartProductAdapter adapter;
    //String purchaseId="";
    double totalCosTemp=0;


    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shopping_cart,
                container, false);

        setHasOptionsMenu(true);

        // Lookup the recyclerview in activity layout
        RecyclerView rvProducts = (RecyclerView) rootView.findViewById(R.id.rvShoppingCartProducts);

        //get user
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Initialize products
        databaseProducts = FirebaseDatabase.getInstance().getReference("ShoppingCart");
        databaseProducts.keepSynced(true);
        databasePurchases = FirebaseDatabase.getInstance().getReference("Purchases");
        databasePurchases.keepSynced(true);

        btnRemove = inflater.inflate(R.layout.item_shoppingcart_product, container, false).findViewById(R.id.sc_remove_button);

        totalCost = rootView.findViewById(R.id.sc_totalCost);

        btnBuyAll = rootView.findViewById(R.id.sc_BuyAll);

        List<Product> products = new ArrayList<>();
        //dummy product to be replaced by the header
        products.add(new Product("1","dummy","dummy","1"));

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

                if(!products.isEmpty()){
                    for(Product p : products)
                        if(!p.getProductId().equals("1")) totalCosTemp += Double.parseDouble(p.price);
                    totalCost.setText("Total: " + String.valueOf(totalCosTemp));
                }else{
                    totalCost.setText("No items in Shopping Cart");
                }


                // Create adapter passing in the sample user data
                adapter = new ShoppingCartProductAdapter(products, ShoppingCartFragment.this::onRemoveProduct);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("");
                builder.setMessage("Do you want to buy All Items ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseProducts.child(userId).child("productsList").addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dsp : snapshot.getChildren()){
                                    //System.out.println(dsp.getValue().toString());
                                    //purchaseId = purchaseId + dsp.child("productName").getValue().toString();
                                    dsp.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        String prodId = databaseProducts.push().getKey();
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        //purchaseId = id+purchaseId;
                        Purchase purchase = new Purchase(prodId, email, products, currentDate,totalCosTemp, false);
                        databasePurchases.child(prodId).setValue(purchase);

                        adapter.removeAll();
                        totalCost.setText("No items in Shopping Cart");

                        Toast.makeText(getActivity(),"Purchage successful", Toast.LENGTH_SHORT).show();    // stop chronometer here
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });



        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.product_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onRemoveProduct(double price) {
        System.out.println("Total = "+totalCosTemp+", "+price);
        totalCosTemp -= price;
        System.out.println(String.format("Total = %.2f" , totalCosTemp));
        if(totalCosTemp <= 0)
            totalCost.setText("No items in Shopping Cart");
        else
            totalCost.setText(String.format("Total: %.2f" , totalCosTemp));
    }
}
