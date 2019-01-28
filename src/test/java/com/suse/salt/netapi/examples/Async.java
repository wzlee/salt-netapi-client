package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.utils.HttpClientUtils;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.net.URI;

/**
 * Example code using HttpAsyncClient.
 */
public class Async {

    private static final String SALT_API_URL = "http://192.168.1.152:8000";
    private static final String USER = "leo";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        CloseableHttpAsyncClient httpClient = HttpClientUtils.defaultClient();
        // Init the client
        SaltClient client = new SaltClient(URI.create(SALT_API_URL), new HttpAsyncClientImpl(httpClient));

        // Clean up afterwards by calling close()
        Runnable cleanup = () -> {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Perform a non-blocking login
        client.login(USER, PASSWORD, AuthModule.MYSQL)
                .thenAccept(t -> System.out.println("Token -> " + t.getToken()))
                .thenRun(cleanup);
    }
}
