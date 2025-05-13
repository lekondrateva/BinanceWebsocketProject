package hft.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    private final String id;
    private final double price;
    private double quantity;
    private final Side side;
    private final long timestamp;

    public Order(String id, double price, double quantity, Side side) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.timestamp = System.nanoTime();
    }

    public void reduceQuantity(double amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }

    @Override
    public String toString() {
        return side + " " + quantity + " @ " + price;
    }
}
