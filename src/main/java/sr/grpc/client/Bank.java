/*
 * Copyright 2015, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package sr.grpc.client;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import sr.grpc.client.Accounts.AccountFactoryI;
import sr.grpc.gen.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Bank {
    private static final Logger logger = Logger.getLogger(Bank.class.getName());
    private int port =10_001;

    private final ManagedChannel channel;
    private final CurrencyServiceGrpc.CurrencyServiceBlockingStub serviceStub;

    private HashMap<CurrencyType, Double> bankCurrencies = new HashMap<>();


    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public Bank(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
                .usePlaintext(true)
                .build();

        serviceStub = CurrencyServiceGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws Exception {
        Bank client = new Bank("localhost", 50051);
        client.test();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    public void test() throws InterruptedException {
        try {

           new Thread(this::subscribe).start();


            int status = 0;
            Communicator communicator = null;

            try {
                // 1. Inicjalizacja ICE - utworzenie communicatora
                communicator = Util.initialize();

                ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter1", "tcp -h localhost -p "+port+":udp -h localhost -p "+port);

                // 3. Stworzenie serwanta/serwant�w
                AccountFactoryI accountServnt =new AccountFactoryI(bankCurrencies);



                adapter.add(accountServnt, new Identity("servant1", "factory"));


                adapter.activate();

                System.out.println("Entering event processing loop...");

                communicator.waitForShutdown();

            } catch (Exception e) {
                System.err.println(e);
                status = 1;
            }
            if (communicator != null) {
                // Clean up
                //
                try {
                    communicator.destroy();
                } catch (Exception e) {
                    System.err.println(e);
                    status = 1;
                }
            }
            System.exit(status);

        } finally {
            shutdown();
        }

    }

    private void subscribe() {
        List<CurrencyType> currencyTypes = Arrays.asList(CurrencyType.USD, CurrencyType.GBP, CurrencyType.EUR);
        RequestCurrency requestCurrency = RequestCurrency.newBuilder().addAllCurrencies(currencyTypes).build();

        Iterator<ResponseCurrencyWithValues> currrenciesWithValues;
        currrenciesWithValues = serviceStub.subscribeForCurrency(requestCurrency);

        while (currrenciesWithValues.hasNext()) {
            ResponseCurrencyWithValues response = currrenciesWithValues.next();
            System.out.println("updated currencies:");
            for (CurrencyValue v : response.getCurrencyValuesList()) {

                System.out.println(v.getCurrency() + "    " + v.getValue());
                bankCurrencies.put(v.getCurrency(), v.getValue());
            }

        }

    }

}