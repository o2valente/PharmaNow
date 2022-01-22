package cm22.ua.pharmanow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ShoppingCartProductAdapter extends
        RecyclerView.Adapter<ShoppingCartProductAdapter.ViewHolder> {


    private List<Product> mProducts;
    private ArrayList<Product> cartProdcuts = new ArrayList<>();
    DatabaseReference databaseProducts;
    private String userId;

    // Pass in the contact array into the constructor
    public ShoppingCartProductAdapter(List<Product> products) {
        mProducts = products;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View productsView = inflater.inflate(R.layout.item_shoppingcart_product, parent, false);

        return new ViewHolder(productsView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //get user
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Initialize products
        databaseProducts = FirebaseDatabase.getInstance().getReference("ShoppingCart");

        //databaseProducts.child(id).setValue(shoppingCart);
        Product product = mProducts.get(position);

        // Set item views based on your views and data model
        TextView nameTextView = holder.nameTextView;
        nameTextView.setText(product.getProductName());
        TextView priceTextView = holder.priceTextView;
        priceTextView.setText(product.getPrice());
        Button button = holder.messageButton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseProducts.child(userId).child("productsList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dsp : snapshot.getChildren()){
                            System.out.println(dsp.getValue().toString());
                            if(dsp.child("productName").getValue().toString().equals(product.getProductName()) && dsp.child("productPharma").getValue().toString().equals(product.getProductPharma())){
                                dsp.getRef().removeValue();
                                mProducts.remove(product);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public Product getItem(int position) {
        return mProducts.get(position);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView priceTextView;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.item_sc_productName);
            priceTextView = (TextView) itemView.findViewById(R.id.item_sc_productPrice);
            messageButton = (Button) itemView.findViewById(R.id.sc_remove_button);

        }
    }
}
