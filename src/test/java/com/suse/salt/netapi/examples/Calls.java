package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.modules.Grains;
import com.suse.salt.netapi.calls.modules.Test;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.PasswordAuth;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.HttpClientUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Example code calling salt modules using the generic interface.
 */
public class Calls {

    private static final String SALT_API_URL = "http://192.168.1.152:8000";
    private static final String USER = "leo";
    private static final String PASSWORD = "123456";
    static final AuthMethod AUTH = new AuthMethod(new PasswordAuth(USER, PASSWORD, AuthModule.MYSQL));

    public static void main(String[] args) {
        // Init the client
        SaltClient client = new SaltClient(URI.create(SALT_API_URL),
                new HttpAsyncClientImpl(HttpClientUtils.defaultClient()));

        // Ping all minions using a glob matcher
        Target<String> globTarget = new Glob("*");
        Map<String, Result<Boolean>> results = Test.ping().callSync(
                client, globTarget, AUTH).toCompletableFuture().join();

        System.out.println("--> Ping results:\n");
        results.forEach((minion, result) -> System.out.println(minion + " -> " + result));

        // Get the grains from a list of minions
        Target<List<String>> minionList = new MinionList("minion1", "minion2");

        // An empty result is returned for targeted minions that are down or minionList
        // entries that do not match actual targets.
        Map<String, Result<Map<String, Object>>> grainResults = Grains.items(false)
                .callSync(client, minionList, AUTH).toCompletableFuture().join();

        grainResults.forEach((minion, grains) -> {
            System.out.println("\n--> Listing grains for '" + minion + "':\n");
            String grainsOutput = grains.fold(
                    error -> "Error: " + error.toString(),
                    grainsMap -> grainsMap.entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(Collectors.joining("\n"))
            );
            System.out.println(grainsOutput);
        });
    }
}
