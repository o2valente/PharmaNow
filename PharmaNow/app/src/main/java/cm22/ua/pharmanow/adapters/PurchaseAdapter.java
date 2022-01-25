package cm22.ua.pharmanow.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.zxing.WriterException;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import cm22.ua.pharmanow.datamodel.Purchase;
import cm22.ua.pharmanow.R;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class PurchaseAdapter extends
        RecyclerView.Adapter<PurchaseAdapter.ViewHolder> implements Filterable {

    private final List<Purchase> mPurchase;
    private final List<Purchase> purchasesFull;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    Display display;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    View popUpView;




    // Pass in the contact array into the constructor
    public PurchaseAdapter(List<Purchase> purchases) {
        mPurchase = purchases;
        purchasesFull = new ArrayList<>(purchases);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View productsView = inflater.inflate(R.layout.item_purchase, parent, false);
        popUpView = inflater.inflate(R.layout.qrcode_popup, parent, false);


        ViewHolder viewHolder = new ViewHolder(productsView);

        display = productsView.getContext().getDisplay();
        dialogBuilder = new AlertDialog.Builder(productsView.getContext());


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Purchase purchase = mPurchase.get(position);

        // Set item views based on your views and data model
        TextView nameTextView = holder.dateTextView;
        nameTextView.setText(purchase.getDate());
        TextView priceTextView = holder.priceTextView;
        priceTextView.setText(String.valueOf(purchase.getTotalCost()));
        Button button = holder.messageButton;
        button.setOnClickListener(v -> {
            //TODO generate qrcode
            if(popUpView.getParent() != null)
                ((ViewGroup) popUpView.getParent()).removeView(popUpView);
            createDialog(purchase.getId());
        });
    }

    public void createDialog(String data){

        Button popup_cancelBtn = popUpView.findViewById(R.id.cancelButton);
        ImageView qrImageView = popUpView.findViewById(R.id.idIVQrcode);
        // below line is for getting
        // the windowmanager service.
        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }

        dialogBuilder.setView(popUpView);
        dialog = dialogBuilder.create();
        dialog.show();


        popup_cancelBtn.setOnClickListener(v -> dialog.dismiss());
//
    }

    @Override
    public int getItemCount() {
        return mPurchase.size();
    }

    @SuppressWarnings("unused")
    public Purchase getItem(int position) {
        return mPurchase.get(position);
    }

    @Override
    public Filter getFilter() {
        return purchaseFilter;
    }

    private final Filter purchaseFilter =  new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Purchase> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                filteredList.addAll(purchasesFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Purchase p : purchasesFull){
                    if (p.getDate().toLowerCase().contains(filterPattern)){
                        filteredList.add(p);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @SuppressWarnings("unchecked")
        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mPurchase.clear();
            mPurchase.addAll((List<Purchase>) results.values);
            notifyDataSetChanged();
        }
    };

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder // --Commented out by Inspection (25/01/2022 20:19):should contain a member variable
        // for any view that will be set as you render a row
        public final TextView dateTextView;
        public final TextView priceTextView;
        public final Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            dateTextView = itemView.findViewById(R.id.itemPurchase_Date);
            priceTextView = itemView.findViewById(R.id.itemPurchase_totalCost);
            messageButton = itemView.findViewById(R.id.itemPurchase_Qrcode);

        }
    }
}
