package cm22.ua.pharmanow;

import com.google.firebase.database.PropertyName;

public class Product{


    String productId;

    String productName;

    String productPharma;

    public Product(){}

    public Product(String productId, String productName, String productPharma) {
        this.productId = productId;
        this.productName = productName;
        this.productPharma = productPharma;
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
}
