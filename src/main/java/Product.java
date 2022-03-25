import java.util.Set;

public class Product {

    private Long id;

    private String name;
    private String category;
    private Double price;
    private Set<Order> orders;

    public Product() {
    }

    public Product(Long id, String name, String category, Double price, Set<Order> orders) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.orders = orders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Product withPrice(Double price) {
        if (price == this.price) {
            return this;
        }
        Product product = new Product(id, name, category, price, orders);
        return product;
    }
}