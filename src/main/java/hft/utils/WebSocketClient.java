package hft.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import hft.models.TradeMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class WebSocketClient {

    public static CompletableFuture<TradeMessage> connectAndReceiveTrade(String wsUrl) {
        CompletableFuture<TradeMessage> receivedMessage = new CompletableFuture<>();
        ObjectMapper objectMapper = new ObjectMapper();

        HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        try {
                            TradeMessage trade = objectMapper.readValue(data.toString(), TradeMessage.class);
                            receivedMessage.complete(trade);
                        } catch (Exception e) {
                            receivedMessage.completeExceptionally(e);
                        }
                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }
                });

        return receivedMessage;
    }
}
