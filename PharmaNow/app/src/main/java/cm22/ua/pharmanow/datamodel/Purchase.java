package cm22.ua.pharmanow.datamodel;

import com.google.firebase.database.PropertyName;

import java.util.List;

@SuppressWarnings("unused")
public class Purchase {
    String id;
    String user;
    List<Product> productsBought;
    String date;
    double totalCost;
    boolean confirmed;

    public Purchase() { }

    public Purchase(String id, String user, List<Product> productsBought, String date, double totalCost, boolean confirmed) {
        this.id = id;
        this.user = user;
        this.productsBought = productsBought;
        this.date = date;
        this.totalCost = totalCost;
        this.confirmed = confirmed;
    }

    @PropertyName("id")
    public String getId() {
        return id;
    }

    @PropertyName("id")
    public void setId(String id) {
        this.id = id;
    }


    @PropertyName("user")
    public String getUser() {
        return user;
    }

    @PropertyName("user")
    public void setUser(String user) {
        this.user = user;
    }

    @PropertyName("productsBought")
    public List<Product> getProductsBought() {
        return productsBought;
    }

    @PropertyName("productsBought")
    public void setProductsBought(List<Product> productsBought) {
        this.productsBought = productsBought;
    }

    @PropertyName("date")
    public String getDate() {
        return date;
    }

  @PropertyName("date")
    public void setDate(String date) {
        this.date = date;
    }

    @PropertyName("totalCost")
    public double getTotalCost() {
        return totalCost;
    }

    @PropertyName("totalCost")
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    @PropertyName("confirmed")
    public boolean isConfirmed() {
        return confirmed;
    }

    @PropertyName("confirmed")
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
