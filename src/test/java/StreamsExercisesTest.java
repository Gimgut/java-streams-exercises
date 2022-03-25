import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class StreamsExercisesTest {

    private List<Customer> customers;
    private List<Product> products;
    private List<Order> orders;

    public StreamsExercisesTest() {
        populate();
    }

    @Test
    @DisplayName("Exercise 1 — Obtain a list of products belongs to category “Books” with price > 100")
    public void exercise1() {
        var res = products.stream()
                .filter( product -> product.getCategory().equalsIgnoreCase("Books"))
                .filter( product -> product.getPrice() > 100)
                .collect(Collectors.toList());

        assertEquals(1, res.size());
        assertEquals(8L, res.get(0).getId());
    }

    @Test
    @DisplayName("Exercise 2 — Obtain a list of order with products belong to category “Baby”")
    public void exercise2() {
        var res = orders.stream()
                .filter(
                        order -> order.getProducts().stream()
                                .anyMatch(product -> product.getCategory().equalsIgnoreCase("Baby"))
                )
                .collect(Collectors.toList());

        assertEquals(1, res.size());
        assertEquals(7L, res.get(0).getId());
    }

    @Test
    @DisplayName("Exercise 3 — Obtain a list of product with category = “Toys” and then apply 10% discount")
    public void exercise3() {
        double discount = 0.9;
        var res = products.stream()
                .filter( product -> product.getCategory().equals("Toys"))
                .map( product -> product.withPrice(product.getPrice() * discount))
                .collect(Collectors.toList());

        var toyName =  res.get(0).getName();
        var beforeDiscount = products.stream().filter(product -> product.getName().equals(toyName)).findFirst();
        assertEquals(3, res.size());
        assertFalse(beforeDiscount.isEmpty());
        assertEquals(beforeDiscount.get().getPrice() * discount, res.get(0).getPrice());
    }

    @Test
    @DisplayName("Exercise 4 — Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021")
    public void exercise4() {
        var res = orders.stream()
                .filter(order -> order.getCustomer().getTier() == 2)
                .filter(order ->
                        order.getOrderDate().isAfter(LocalDate.of(2021,2,1))
                                && order.getOrderDate().isBefore(LocalDate.of(2021, 4, 1))
                )
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());

        assertEquals(2, res.size());
        assertTrue(res.contains(products.get(4)));
        assertTrue(res.contains(products.get(5)));
    }

    @Test
    @DisplayName("Exercise 5 — Get the cheapest products of “Books” category")
    public void exercise5() {
        /*
        var res = products.stream()
                .filter(product -> product.getName().equalsIgnoreCase("Books"))
                .min(Comparator.comparing(Product::getPrice));
        */
        var minPrice =  products.stream()
                .filter(
                        product -> product.getCategory().equalsIgnoreCase("Books")
                )
                .mapToDouble(Product::getPrice).min();

        var res = minPrice.isPresent()
                ? products.stream()
                .filter(
                        product -> product.getCategory().equalsIgnoreCase("Books"))
                .filter(
                        product -> product.getPrice() == minPrice.getAsDouble())
                .collect(Collectors.toList())
                : new ArrayList<Product>();

        assertEquals(1, res.size());
        assertEquals(9L, res.get(0).getId());
    }

    @Test
    @DisplayName("Exercise 6 — Get the 3 most recent placed order")
    public void exercise6() {
        var res = orders.stream()
                .sorted(Comparator.comparing(Order::getDeliveryDate).reversed())
                .limit(3)
                .collect(Collectors.toList());

        assertEquals(4L,res.get(0).getId());
        assertEquals(5L, res.get(1).getId());
        assertEquals(3L, res.get(2).getId());
    }

    @Test
    @DisplayName("Exercise 7 — Get a list of orders which were ordered on 15-Mar-2021," +
            "log the order records to the console and then return its product list")
    public void exercise7() {
        LocalDate mar152021 = LocalDate.of(2021, 3, 15);
        var res = orders.stream()
                .filter(order -> order.getOrderDate().isEqual(mar152021))
                .peek(order -> System.out.println(order))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());

        assertEquals(2, res.size());
        assertTrue(res.contains(products.get(5)));
        assertTrue(res.contains(products.get(4)));
    }

    @Test
    @DisplayName("Exercise 8 — Calculate total lump sum of all orders placed in Feb 2021")
    public void exercise8() {
        var feb012021 = LocalDate.of(2021, 02, 1);
        var mar012021 = LocalDate.of(2021, 03, 1);
        var res = orders.stream()
                .filter(
                        order -> order.getOrderDate().compareTo(feb012021) >= 0
                                && order.getOrderDate().isBefore(mar012021)
                )
                .flatMap( order -> order.getProducts().stream())
                //.collect(Collectors.summarizingDouble( p -> p.getPrice()))
                //.getSum();
                .mapToDouble(Product::getPrice)
                .sum();

        assertEquals(100934.0, res);
    }

    @Test
    @DisplayName("Exercise 9 — Calculate order average payment placed on 14-Mar-2021")
    public void exercise9() {
        var mar142021 = LocalDate.of(2021, 03, 14);
        var res = orders.stream()
                .filter(order -> order.getOrderDate().isEqual(mar142021))
                .flatMap( order -> order.getProducts().stream())
                .mapToDouble(  Product::getPrice)
                .average()
                .getAsDouble();

        assertEquals(74.5, res);
    }

    @Test
    @DisplayName("Exercise 10 — Obtain a collection of statistic figures" +
            "(i.e. sum, average, max, min, count) for all products of category “Books”")
    public void exercise10() {
        var res = products.stream()
                .filter( product -> product.getCategory().equalsIgnoreCase("Books"))
                .mapToDouble( product -> product.getPrice())
                .summaryStatistics();
        var resMax = res.getMax();
        var resSum = res.getSum();

        assertEquals(340.0, resMax);
        assertEquals(574.0, resSum);
    }

    @Test
    @DisplayName("Exercise 11 — Obtain a data map with order id and order’s product count")
    public void exercise11() {
        var res = orders.stream()
                .collect(
                        Collectors.toMap(
                                order -> order.getId(),
                                order -> order.getProducts().size()
                        )
                );

        assertEquals(2, res.get(1L));
        assertEquals(3, res.get(8L));
    }

    @Test
    @DisplayName("Exercise 12 — Produce a data map with order records grouped by customer")
    public void exercise12() {
        var res = orders.stream()
                .collect(
                        Collectors.groupingBy(order -> order.getCustomer())
                );

        assertEquals(5, res.size());
        for (Customer customer : customers) {
            assertTrue(res.containsKey(customer));
        }
        assertEquals(1, res.get(customers.get(0)).size());
        assertEquals(2, res.get(customers.get(4)).size());
    }

    @Test
    @DisplayName("Exercise 13 — Produce a data map with order record and product total sum")
    public void exercise13() {
        var res = orders.stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                order -> order.getProducts().stream().mapToDouble( product -> product.getPrice()).sum()
                        )
                );

        assertEquals(8, res.size());
        assertEquals(205.0, res.get(orders.get(0)));
        assertEquals(100550.0, res.get(orders.get(1)));
        assertEquals(130.0, res.get(orders.get(2)));
        assertEquals(185.0, res.get(orders.get(7)));
    }

    @Test
    @DisplayName("Exercise 14 — Obtain a data map with list of product name by category")
    public void exercise14() {
        var res = products.stream()
                .collect(
                        Collectors.groupingBy(
                                product -> product.getCategory().toLowerCase(),
                                Collectors.mapping( product -> product.getName(), Collectors.toList())
                        )
                );

        assertEquals(4, res.size());
        assertEquals(5, res.get("cutlery").size());
        assertEquals(4, res.get("books").size());
        assertEquals(2, res.get("baby").size());
        assertEquals(3, res.get("toys").size());
    }

    @Test
    @DisplayName("Exercise 15 — Get the most expensive product by category")
    public void exercise15() {
        var res = products.stream()
                .collect(
                        Collectors.groupingBy(
                                product -> product.getCategory().toLowerCase(),
                                Collectors.maxBy(Comparator.comparing(Product::getPrice))
                        )
                );

        assertEquals(4, res.size());
        assertEquals(100500.0, res.get("cutlery").get().getPrice());
        assertEquals(340.0, res.get("books").get().getPrice());
        assertEquals(999.0, res.get("baby").get().getPrice());
        assertEquals(69.0, res.get("toys").get().getPrice());
    }

    private void populate() {
        customers = new ArrayList<>();
        customers.add(new Customer(1L, "Linda", 1));
        customers.add(new Customer(2L, "Paul", 1));
        customers.add(new Customer(3L, "Fiedel", 2));
        customers.add(new Customer(4L, "Gissie", 10));
        customers.add(new Customer(5L, "Greg", 8));

        products = new ArrayList<>();
        products.add(new Product(1L, "Super Platinum Fork", "Cutlery", 105.0, new HashSet<>()));
        products.add(new Product(2L, "Golden Fork", "Cutlery", 100.0, new HashSet<>()));
        products.add(new Product(3L, "Unrivaled Fork", "Cutlery", 100500.0, new HashSet<>()));
        products.add(new Product(4L, "Eye catcher Fork", "Cutlery", 50.0, new HashSet<>()));
        products.add(new Product(5L, "Eye disposer Fork", "Cutlery", 40.0, new HashSet<>()));
        products.add(new Product(6L, "The mighty", "Books", 90.0, new HashSet<>()));
        products.add(new Product(7L, "The useless", "books", 100.0, new HashSet<>()));
        products.add(new Product(8L, "The undefeated", "Books", 340.0, new HashSet<>()));
        products.add(new Product(9L, "Tomorrow", "books", 44.0, new HashSet<>()));
        products.add(new Product(10L, "Magic Powder", "Baby",999.0, new HashSet<>()));
        products.add(new Product(11L, "Super Powder", "Baby",99.0, new HashSet<>()));
        products.add(new Product(12L, "Lion", "Toys",59.0, new HashSet<>()));
        products.add(new Product(13L, "Giraffe", "Toys",69.0, new HashSet<>()));
        products.add(new Product(14L, "Eagle", "Toys",57.0, new HashSet<>()));

        orders = new ArrayList<>();
        orders.add(
                new Order(
                        1L,
                        LocalDate.of(2021, 1, 1),
                        LocalDate.of(2021,2,1),
                        "Shipping",
                        customers.get(0),
                        new HashSet<>()
                )
        );
        orders.add(
                new Order(
                        2L,
                        LocalDate.of(2021,2,2),
                        LocalDate.of(2021,2,10),
                        "Undefined",
                        customers.get(1),
                        new HashSet<>()
                )
        );
        orders.add(
                new Order(
                        3L,
                        LocalDate.of(2021, 3, 15),
                        LocalDate.of(2021, 3,28),
                        "Idk",
                        customers.get(2),
                        new HashSet<>()
                )
        );
        orders.add(
                new Order(
                        4L,
                        LocalDate.of(2021, 4, 21),
                        LocalDate.of(2021, 5, 9),
                        "Idk",
                        customers.get(3),
                        new HashSet<>()
                )
        );
        orders.add(
                new Order(
                        5L,
                        LocalDate.of(2021, 3, 14),
                        LocalDate.of(2021, 4, 7),
                        "Undefined",
                        customers.get(4),
                        new HashSet<>()
                )
        );
        orders.add(
                new Order(
                        6L,
                        LocalDate.of(2021, 2, 11),
                        LocalDate.of(2021, 2, 22),
                        "Idk",
                        customers.get(4),
                        new HashSet<>()
                )
        );
        orders.add(
                new Order(
                        7L,
                        LocalDate.of(2021, 1, 29),
                        LocalDate.of(2021, 2, 19),
                        "Idk",
                        customers.get(3),
                        new HashSet<>()
                )
        );
        orders.add(
                new Order(
                        8L,
                        LocalDate.of(2021, 1,20),
                        LocalDate.of(2021, 2, 28),
                        "Idk",
                        customers.get(2),
                        new HashSet<>()
                )
        );

        // Linking orders and products
        orders.get(0).setProducts(Set.of(products.get(0), products.get(1)));
        products.get(0).getOrders().add(orders.get(0));
        products.get(1).getOrders().add(orders.get(0));

        orders.get(1).setProducts(Set.of(products.get(2), products.get(3)));
        products.get(2).getOrders().add(orders.get(1));
        products.get(3).getOrders().add(orders.get(1));

        orders.get(2).setProducts(Set.of(products.get(4), products.get(5)));
        products.get(4).getOrders().add(orders.get(2));
        products.get(5).getOrders().add(orders.get(2));

        orders.get(3).setProducts(Set.of(products.get(6), products.get(7)));
        products.get(6).getOrders().add(orders.get(3));
        products.get(7).getOrders().add(orders.get(3));

        orders.get(4).setProducts(Set.of(products.get(8), products.get(0)));
        products.get(8).getOrders().add(orders.get(4));
        products.get(0).getOrders().add(orders.get(4));

        orders.get(5).setProducts(Set.of(products.get(7), products.get(8)));
        products.get(7).getOrders().add(orders.get(5));
        products.get(8).getOrders().add(orders.get(5));

        orders.get(6).setProducts(Set.of(products.get(9), products.get(10)));
        products.get(9).getOrders().add(orders.get(6));
        products.get(10).getOrders().add(orders.get(6));

        orders.get(7).setProducts(Set.of(products.get(11), products.get(12), products.get(13)));
        products.get(11).getOrders().add(orders.get(7));
        products.get(12).getOrders().add(orders.get(7));
        products.get(13).getOrders().add(orders.get(7));
    }
}