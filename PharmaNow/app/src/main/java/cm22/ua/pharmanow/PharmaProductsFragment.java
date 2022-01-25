package cm22.ua.pharmanow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PharmaProductsFragment extends Fragment {

    private TextView pharmaNameText;
    DatabaseReference databaseProducts;
    ProductAdapter adapter;

    public PharmaProductsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pharma_products,
                container, false);

        setHasOptionsMenu(true);

       // pharmaNameText = rootView.findViewById(R.id.pharmaName);

        Bundle bundle = this.getArguments();
        String pharma = bundle.getString("requestKey");



        System.out.println(pharma);

        //pharmaNameText.setText(pharma);
        // Lookup the recyclerview in activity layout
        RecyclerView rvProducts = (RecyclerView) rootView.findViewById(R.id.rvProducts);

        List<Product> products = new ArrayList<>();

        // Initialize contacts
        databaseProducts = FirebaseDatabase.getInstance().getReference();
        databaseProducts.keepSynced(true);

        databaseProducts.child("products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dsp : snapshot.getChildren()) {
                    if(dsp.child("productPharma").getValue().toString().equals(pharma)){
                        products.add(dsp.getValue(Product.class));
                    }
                    //System.out.println(dsp.child("productName").getValue());
                    /*String id = dsp.child("productId").getValue().toString();
                    String name = dsp.child("productName").getValue().toString();
                    String pharma = dsp.child("productPharma").getValue().toString();*/
                    //products.add(new Product(id, name, pharma));

                }
                // Create adapter passing in the sample user data
                adapter = new ProductAdapter(products);
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

}
