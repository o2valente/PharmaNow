package cm22.ua.pharmanow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        View productsView = inflater.inflate(R.layout.item_product, parent, false);

        ViewHolder viewHolder = new ViewHolder(productsView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Initialize products
        databaseProducts = FirebaseDatabase.getInstance().getReference("ShoppingCart");
        //get user
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //databaseProducts.child(id).setValue(shoppingCart);
        Product product = mProducts.get(position);

        // Set item views based on your views and data model
        TextView nameTextView = holder.nameTextView;
        nameTextView.setText(product.getProductName());
        TextView pharmaTextView = holder.pharmatextView;
        pharmaTextView.setText(product.getProductPharma());
        TextView priceTextView = holder.priceTextView;
        priceTextView.setText(product.getPrice());
        Button button = holder.messageButton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProdcuts.add(product);
                String id = databaseProducts.push().getKey();
                databaseProducts.child(userId).child("productsList").child(id).setValue(product);
                //databaseProducts.child(id).setValue(shoppingCart);
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
        public TextView pharmatextView;
        public TextView priceTextView;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.item_productName);
            pharmatextView = (TextView) itemView.findViewById(R.id.item_productPharma);
            priceTextView = (TextView) itemView.findViewById(R.id.item_productPrice);
            messageButton = (Button) itemView.findViewById(R.id.item_buy_button);

        }
    }
}
