package hft.websocket;

import hft.models.TradeMessage;
import hft.utils.WebSocketClient;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static hft.utils.Waiter.waitFor;
import static org.junit.jupiter.api.Assertions.*;

public class WebSocketTradePojoTest {

    private final String BINANCE_WS_URL = "wss://stream.binance.com:9443/ws/btcusdt@trade";

    @Test
    void testTradeMessagePojoWithAssertAll() {
        CompletableFuture<TradeMessage> tradeFuture = WebSocketClient.connectAndReceiveTrade(BINANCE_WS_URL);

        waitFor(() -> {
            assertTrue(tradeFuture.isDone(), "Trade future не завершён");

            TradeMessage trade = tradeFuture.getNow(null);
            assertNotNull(trade, "Trade message пустой");

            assertAll("Проверка основных полей trade-сообщения",
                    () -> assertEquals("trade", trade.getEventType(), "Неверный eventType"),
                    () -> assertEquals("BTCUSDT", trade.getSymbol(), "Неверный symbol"),
                    () -> assertNotNull(trade.getPrice(), "Поле price пустое"),
                    () -> assertFalse(trade.getPrice().isEmpty(), "Поле price пустая строка")
            );
        }, 4000, 100);
    }

}
