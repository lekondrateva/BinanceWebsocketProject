package hft.orderbook;

import hft.engine.OrderBook;
import hft.models.Order;
import hft.models.Side;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderBookTest {

    @Test
    void testNoMatchingWhenPricesDoNotCross() {
        OrderBook ob = new OrderBook();

        ob.addOrder(new Order("o1", 100.0, 5.0, Side.BUY));   // куплю по 100
        ob.addOrder(new Order("o2", 101.0, 2.0, Side.BUY));   // куплю по 101
        ob.addOrder(new Order("o3", 102.0, 1.0, Side.SELL));  // продам по 102 → не матчится
        ob.addOrder(new Order("o4", 103.0, 3.0, Side.SELL));  // продам по 103 → не матчится
        ob.addOrder(new Order("o5", 101.0, 2.0, Side.BUY));   // куплю по 101 → не матчится
        ob.cancelOrder("o1");                                 // удаляем o1 (BUY 100)

        ob.printBook();

        // Количество ask-заявок по разным ценам
        assertEquals(2, ob.getAsks().size());
        // Количество bid-заявок по разным ценам (о1 отменён, остаются два по 101)
        assertEquals(1, ob.getBids().size());
        assertEquals(2, ob.getBids().get(101.0).size());
    }

    @Test
    void testMatchingOccursWhenPricesCross() {
        OrderBook ob = new OrderBook();

        // Сценарий: BUY заявка по 105 исполняется с SELL 102
        ob.addOrder(new Order("s1", 102.0, 1.0, Side.SELL)); // В стакан
        ob.addOrder(new Order("s2", 103.0, 2.0, Side.SELL)); // В стакан
        ob.addOrder(new Order("b1", 105.0, 2.0, Side.BUY));  // Исполняется с s1 и s2 (частично)

        ob.printBook();

        assertEquals(1, ob.getAsks().size());
        assertEquals(1, ob.getAsks().get(103.0).size());
        assertEquals(1.0, ob.getAsks().get(103.0).get(0).getQuantity());
        assertEquals(0, ob.getBids().size()); // b1 полностью исполнен
    }

}
