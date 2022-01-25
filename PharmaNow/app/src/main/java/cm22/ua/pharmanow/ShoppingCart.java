package cm22.ua.pharmanow;

import com.google.firebase.database.PropertyName;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class ShoppingCart {

    ArrayList<Product> productsList;
    String userEmail;


    public ShoppingCart(ArrayList<Product> products, String userEmail) {
        this.productsList = products;
        this.userEmail = userEmail;
    }

    @PropertyName("productsList")
    public ArrayList<Product> getProducts() {
        return productsList;
    }

    @PropertyName("productsList")
    public void setProducts(ArrayList<Product> products) {
        this.productsList = products;
    }

    @PropertyName("userEmail")
    public String getUserEmail() {
        return userEmail;
    }

    @PropertyName("userEmail")
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
