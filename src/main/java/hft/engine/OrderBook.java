package hft.engine;

import hft.models.Order;
import hft.models.Side;
import hft.models.Trade;

import java.util.*;

public class OrderBook {

    private final TreeMap<Double, List<Order>> bids = new TreeMap<>(Collections.reverseOrder());
    private final TreeMap<Double, List<Order>> asks = new TreeMap<>();

    private final Map<String, Order> orderById = new HashMap<>();
    private final List<Trade> tradeHistory = new ArrayList<>();

    public void addOrder(Order order) {
        if (order.getPrice() <= 0) {
            // Рыночная заявка (price = 0 или отрицательная)
            System.out.println("Market order: " + order);
            matchMarketOrder(order);
        } else {
            // Лимитная заявка
            System.out.println("Limit order: " + order);
            if (order.getSide() == Side.BUY) {
                match(order, asks);
                if (order.getQuantity() > 0) addToBook(order, bids);
            } else {
                match(order, bids);
                if (order.getQuantity() > 0) addToBook(order, asks);
            }
        }
    }

    private void matchMarketOrder(Order marketOrder) {
        TreeMap<Double, List<Order>> book = marketOrder.getSide() == Side.BUY ? asks : bids;
        match(marketOrder, book, true);
    }

    private void match(Order incoming, TreeMap<Double, List<Order>> book) {
        match(incoming, book, false);
    }

    private void match(Order incoming, TreeMap<Double, List<Order>> book, boolean ignorePriceCheck) {
        Iterator<Map.Entry<Double, List<Order>>> it = book.entrySet().iterator();
        while (it.hasNext() && incoming.getQuantity() > 0) {
            Map.Entry<Double, List<Order>> entry = it.next();
            double bookPrice = entry.getKey();

            //limit order
            if (!ignorePriceCheck) {
                if ((incoming.getSide() == Side.BUY && incoming.getPrice() < bookPrice) ||
                        (incoming.getSide() == Side.SELL && incoming.getPrice() > bookPrice)) {
                    break;
                }
            }

            List<Order> ordersAtPrice = entry.getValue();
            Iterator<Order> orderIt = ordersAtPrice.iterator();

            //пока есть предложения по этой цене и ордер не выполнился полностью
            while (orderIt.hasNext() && incoming.getQuantity() > 0) {
                Order resting = orderIt.next();
                //исполняем ордер по минимальному объему
                double executedQty = Math.min(incoming.getQuantity(), resting.getQuantity());

                // Сохраняем сделку в историю
                tradeHistory.add(new Trade(
                        incoming.getSide() == Side.BUY ? incoming.getId() : resting.getId(),
                        incoming.getSide() == Side.BUY ? resting.getId() : incoming.getId(),
                        bookPrice,
                        executedQty
                ));

                incoming.reduceQuantity(executedQty);
                resting.reduceQuantity(executedQty);

                //если ордер в стакане исполнился полностью, то удалить из стакана
                if (resting.getQuantity() == 0) {
                    orderIt.remove();
                    orderById.remove(resting.getId());
                }
            }

            if (ordersAtPrice.isEmpty()) {
                it.remove();
            }
        }
    }

    private void addToBook(Order order, TreeMap<Double, List<Order>> book) {
        book.computeIfAbsent(order.getPrice(), k -> new ArrayList<>()).add(order);
        orderById.put(order.getId(), order);
        System.out.println("Added to book: " + order);
    }

    public void cancelOrder(String orderId) {
        Order order = orderById.remove(orderId);
        if (order == null) return;

        TreeMap<Double, List<Order>> book = order.getSide() == Side.BUY ? bids : asks;
        List<Order> ordersAtPrice = book.get(order.getPrice());
        if (ordersAtPrice != null) {
            ordersAtPrice.removeIf(o -> o.getId().equals(orderId));
            if (ordersAtPrice.isEmpty()) book.remove(order.getPrice());
        }
        System.out.println("Order cancelled: " + orderId);
    }

    public void printBook() {
        System.out.println("ORDER BOOK");
        System.out.println("ASKS:");
        asks.forEach((price, orders) -> System.out.printf("%.2f : %s\n", price, orders));
        System.out.println("BIDS:");
        bids.forEach((price, orders) -> System.out.printf("%.2f : %s\n", price, orders));
        System.out.println();
    }

    public OptionalDouble getLastTradePrice() {
        if (tradeHistory.isEmpty()) return OptionalDouble.empty();
        return OptionalDouble.of(tradeHistory.get(tradeHistory.size() - 1).getPrice());
    }

    public OptionalDouble getBestBid() {
        return bids.isEmpty()
                ? OptionalDouble.empty()
                : OptionalDouble.of(bids.firstKey()); // bids — reverseOrder
    }

    public OptionalDouble getBestAsk() {
        return asks.isEmpty()
                ? OptionalDouble.empty()
                : OptionalDouble.of(asks.firstKey()); // asks — по возрастанию
    }

    public OptionalDouble getMidPrice() {
        OptionalDouble bestBid = getBestBid();
        OptionalDouble bestAsk = getBestAsk();

        if (bestBid.isPresent() && bestAsk.isPresent()) {
            return OptionalDouble.of((bestBid.getAsDouble() + bestAsk.getAsDouble()) / 2);
        }
        return OptionalDouble.empty();
    }

    public void printTradeHistory() {
        System.out.println("TRADE HISTORY:");
        for (Trade trade : tradeHistory) {
            System.out.println(trade);
        }
        System.out.println();
    }

    public Map<Double, List<Order>> getBids() {
        return Collections.unmodifiableMap(bids);
    }

    public Map<Double, List<Order>> getAsks() {
        return Collections.unmodifiableMap(asks);
    }

    public List<Trade> getTradeHistory() {
        return Collections.unmodifiableList(tradeHistory);
    }
}
