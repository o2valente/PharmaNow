package cm22.ua.pharmanow;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ToDeliverAdapter extends
        RecyclerView.Adapter<ToDeliverAdapter.ViewHolder> {


    private final List<Product> mProducts;


    // Pass in the contact array into the constructor
    public ToDeliverAdapter(List<Product> productsToDeliver) {
        mProducts = productsToDeliver;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View productsView = inflater.inflate(R.layout.item_to_deliver, parent, false);

        return new ViewHolder(productsView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = mProducts.get(position);
        if(!product.getProductId().equals("1")){
            // Set item views based on your views and data model
            TextView nameTextView = holder.nameTextView;
            nameTextView.setText(product.getProductName());
            TextView pharmaTextView = holder.pharmatextView;
            pharmaTextView.setText(product.getProductPharma());
        }
    }


    @Override
    public int getItemCount() {
        return mProducts.size();
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public final TextView nameTextView;
        public final TextView pharmatextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.item_toDeliver_productName);
            pharmatextView = itemView.findViewById(R.id.item_toDeliver_productPharma);

        }
    }
}
