package cm22.ua.pharmanow;

import com.google.firebase.database.PropertyName;

@SuppressWarnings("ALL")
public class Product {


    String productId;

    String productName;

    String productPharma;

    String price;

    public Product() {
    }

    public Product(String productId, String productName, String productPharma, String price) {
        this.productId = productId;
        this.productName = productName;
        this.productPharma = productPharma;
        this.price = price;
    }

    @PropertyName("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

   @PropertyName("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @PropertyName("productPharma")
    public void setProductPharma(String productPharma) {
        this.productPharma = productPharma;
    }

    @PropertyName("price")
    public void setPrice(String price){this.price = price;}

    @PropertyName("productId")
    public String getProductId() {
        return productId;
    }

    @PropertyName("productName")
    public String getProductName() {
        return productName;
    }

    @PropertyName("productPharma")
    public String getProductPharma() {
        return productPharma;
    }

    @PropertyName("price")
    public String getPrice(){return price;}
}
