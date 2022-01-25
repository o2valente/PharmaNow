package cm22.ua.pharmanow.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cm22.ua.pharmanow.interfaces.OnRemoveCartProduct;
import cm22.ua.pharmanow.datamodel.Product;
import cm22.ua.pharmanow.R;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
@SuppressWarnings("ALL")
public class ShoppingCartProductAdapter extends
        RecyclerView.Adapter<ShoppingCartProductAdapter.ViewHolder> {


    private final List<Product> mProducts;
    private final List<Product> productsFull;
    private final ArrayList<Product> cartProdcuts = new ArrayList<>();
    DatabaseReference databaseProducts;
    private String userId;
    final OnRemoveCartProduct listener;
    public static final int HEADER = 1;
    private static final int ITEM = 2;

    // Pass in the contact array into the constructor
    public ShoppingCartProductAdapter(List<Product> products, OnRemoveCartProduct listener) {
        mProducts = products;
        productsFull = new ArrayList<>(products);
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;
        if (viewType == HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoppingcart_product, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == HEADER) {
            holder.headerTextView.setText("Product Name                 Price");
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
                TextView priceTextView = holder.priceTextView;
                priceTextView.setText(product.getPrice());
                Button button = holder.messageButton;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseProducts.child(userId).child("productsList").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dsp : snapshot.getChildren()) {
                                    System.out.println(dsp.getValue().toString());
                                    if (dsp.child("productName").getValue().toString().equals(product.getProductName()) && dsp.child("productPharma").getValue().toString().equals(product.getProductPharma())) {
                                        dsp.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        System.out.println("AQUI " + holder.getAdapterPosition());
                        Product p = mProducts.get(holder.getAdapterPosition());
                        System.out.println("Product= " + p.getPrice());
                        listener.onRemoveProduct(Double.parseDouble(p.getPrice()));
                        mProducts.remove(holder.getAdapterPosition());
                        productsFull.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        //notifyDataSetChanged();

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

    @SuppressLint("NotifyDataSetChanged")
    public void removeAll(){
        mProducts.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    //@Override
    public Filter getFilter() {
        return productFilter;
    }

    private final Filter productFilter =  new Filter() {
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

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mProducts.clear();
            mProducts.addAll((List) results.values);
            if(mProducts.size()!=0)
                if(!mProducts.get(0).getProductId().equals("1")) mProducts.add(0,new Product("1","dummy","dummy","1"));
            if(productsFull.size()!=0)
                if(!productsFull.get(0).getProductId().equals("1")) productsFull.add(0,new Product("1","dummy","dummy","1"));
            notifyDataSetChanged();
        }
    };

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public final TextView nameTextView;
        public final TextView priceTextView;
        public final Button messageButton;
        public final TextView headerTextView;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            headerTextView = (TextView) itemView.findViewById(R.id.item_product_header);
            nameTextView = (TextView) itemView.findViewById(R.id.item_sc_productName);
            priceTextView = (TextView) itemView.findViewById(R.id.item_sc_productPrice);
            messageButton = (Button) itemView.findViewById(R.id.sc_remove_button);


        }
    }
}
