package cm22.ua.pharmanow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class PharmaProductsFragment extends Fragment {

    private TextView pharmaNameText;
    public PharmaProductsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pharma_products,
                container, false);

        pharmaNameText = rootView.findViewById(R.id.pharmaName);

        //Bundle bundle = this.getArguments();
        //String pharma = bundle.getString("requestKey");

        //System.out.println(pharma);

        //pharmaNameText.setText(pharma);

        return rootView;
    }

}
