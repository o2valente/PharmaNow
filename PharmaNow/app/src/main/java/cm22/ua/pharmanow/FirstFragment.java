package cm22.ua.pharmanow;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firestore.v1.Value;

import java.util.ArrayList;
import java.util.List;

import io.grpc.okhttp.internal.framed.Header;


public class FirstFragment extends Fragment {

    DatabaseReference databaseProducts;
    ProductAdapter adapter;
    //private FirebaseAuth auth;


    public FirstFragment() {
        // Required empty public constructor
    }


   // @Override
    //public void onCreate(Bundle savedInstanceState) {
       // super.onCreate(savedInstanceState);
   // }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.placeholder1,
                container, false);

        setHasOptionsMenu(true);

        // Lookup the recyclerview in activity layout
        RecyclerView rvProducts = (RecyclerView) rootView.findViewById(R.id.rvProducts);

        // Initialize products
        databaseProducts = FirebaseDatabase.getInstance().getReference();
        databaseProducts.keepSynced(true);

        List<Product> products = new ArrayList<>();
        //dummy product to be replaced by the header
        products.add(new Product("1","dummy","dummy","1"));

        databaseProducts.child("products").addListenerForSingleValueEvent(new ValueEventListener() {
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




        /*databaseProducts.child("products").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getActivity(), "Could not read data!", Toast.LENGTH_SHORT).show();
                }else{
                    for(DataSnapshot dsp : task.getResult().getChildren()){
                        //System.out.println(dsp.child("productName").getValue());
                        String id = dsp.child("productId").getValue().toString();
                        String name = dsp.child("productName").getValue().toString();
                        String pharma = dsp.child("productPharma").getValue().toString();
                        products.add(new Product(id,name,pharma));
                    }

                }
            }

        });*/
        /*Product p = products.get(0);
        System.out.printf("TESTE: %s-%s-%s",p.productId,p.productName,p.productPharma);*/

        //products.add(new Product("1", "aspirina", "Farmácia Aveiro"));
        //System.out.print(products.size() + "Produtos");



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