package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.event.EventListener;
import com.suse.salt.netapi.utils.HttpClientUtils;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import com.suse.salt.netapi.event.WebSocketEventStream;

import java.net.URI;

/**
 * Example code listening for events on salt's event bus.
 */
public class Events {

    private static final String SALT_API_URL = "http://192.168.1.152:8000";
    private static final String USER = "leo";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        // Init client and set the timeout to infinite
        SaltClient client = new SaltClient(URI.create(SALT_API_URL),
                new HttpAsyncClientImpl(HttpClientUtils.defaultClient()));

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(0)
                .setConnectTimeout(0)
                .setSocketTimeout(0)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);

        CloseableHttpAsyncClient asyncHttpClient = httpClientBuilder.build();
        asyncHttpClient.start();

        try {
            // Get a login token
            Token token = client.login(USER, PASSWORD, AuthModule.MYSQL).toCompletableFuture().join();
            System.out.println("Token: " + token.getToken());

            // Init the event stream with a basic listener implementation
            WebSocketEventStream eventStream = client.events(
                    token, 0, 0, 0,
                    new EventListener() {
                        @Override
                        public void notify(Event e) {
                            System.out.println("Tag  -> " + e.getTag());
                            System.out.println("Data -> " + e.getData());
                        }

                        @Override
                        public void eventStreamClosed(int code, String phrase) {
                            System.out.println("Event stream closed: " + phrase);
                        }
                    });

            // Wait for events and close the event stream after 30 seconds
            System.out.println("-- Waiting for events --");
            Thread.sleep(30000);
            eventStream.close();
            System.out.println("-- Stop waiting for events --");
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
