package cm22.ua.pharmanow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.io.Resources;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ProductAdapter extends
        RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    public static final int HEADER = 1;
    private static final int ITEM = 2;

    private List<Product> mProducts;
    private List<Product> productsFull;
    private ArrayList<Product> cartProdcuts = new ArrayList<>();
    DatabaseReference databaseProducts;
    private String userId;



    // Pass in the contact array into the constructor
    public ProductAdapter(List<Product> products) {
        mProducts = products;
        productsFull = new ArrayList<>(products);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Context context = parent.getContext();
        //LayoutInflater inflater = LayoutInflater.from(context);
        final View view;
        if (viewType == HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        }
        return new ViewHolder(view);

        //View productsView = inflater.inflate(R.layout.item_product, parent, false);

        //ViewHolder viewHolder = new ViewHolder(productsView);

        //return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (getItemViewType(position) == HEADER) {
            holder.headerTextView.setText("Product Name         Pharmacy                 Price");
        } else {
            //get user
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // Initialize products
            databaseProducts = FirebaseDatabase.getInstance().getReference("ShoppingCart");

            //databaseProducts.child(id).setValue(shoppingCart);

            Product product = mProducts.get(position);

            if(!product.getProductId().equals("1")){
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
                        button.setBackgroundColor(Color.GREEN);
                    }
                });
            }


        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public Product getItem(int position) {
        return mProducts.get(position);
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter =  new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                filteredList.addAll(productsFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Product p : productsFull){
                    if (p.getProductName().toLowerCase().contains(filterPattern)){
                        filteredList.add(p);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mProducts.clear();
            mProducts.addAll((List) results.values);
            if(mProducts.size()!=0)
                if(!mProducts.get(0).getProductId().equals("1")) mProducts.add(0,new Product("1","dummy","dummy","1"));
            notifyDataSetChanged();
        }
    };

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView pharmatextView;
        public TextView priceTextView;
        public Button messageButton;
        public TextView headerTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            headerTextView = (TextView) itemView.findViewById(R.id.item_product_header);
            nameTextView = (TextView) itemView.findViewById(R.id.item_productName);
            pharmatextView = (TextView) itemView.findViewById(R.id.item_productPharma);
            priceTextView = (TextView) itemView.findViewById(R.id.item_productPrice);
            messageButton = (Button) itemView.findViewById(R.id.item_buy_button);

        }
    }
}
