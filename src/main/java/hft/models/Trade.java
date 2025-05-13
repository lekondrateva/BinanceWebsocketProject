package hft.models;

import lombok.Getter;

@Getter
public class Trade {
    private final String buyerId;
    private final String sellerId;
    private final double price;
    private final double quantity;

    public Trade(String buyerId, String sellerId, double price, double quantity) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("%s bought from %s: %.2f @ %.2f", buyerId, sellerId, quantity, price);
    }
}