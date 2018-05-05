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

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import sr.grpc.gen.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Bank {
    private static final Logger logger = Logger.getLogger(Bank.class.getName());

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
//			String line = null;
//			java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
//			do
//			{
//				try
//				{
//					System.out.print("==> ");
//					System.out.flush();
//					line = in.readLine();


            subscribe();

//				}
//				catch (java.io.IOException ex)
//				{
//					System.err.println(ex);
//				}
//			}
//			while (!line.equals("x"));
        } finally {
            shutdown();
        }

    }

    private void subscribe() {
        List<CurrencyType> currencyTypes = Arrays.asList(CurrencyType.CAD, CurrencyType.GBP, CurrencyType.EUR);
        RequestCurrency requestCurrency = RequestCurrency.newBuilder().addAllCurrencies(currencyTypes).build();

        Iterator<ResponseCurrencyWithValues> currrenciesWithValues;
        currrenciesWithValues = serviceStub.subscribeForCurrency(requestCurrency);

        while (currrenciesWithValues.hasNext()) {
            ResponseCurrencyWithValues response = currrenciesWithValues.next();
            for (CurrencyValue v : response.getCurrencyValuesList()) {
                System.out.println(v.getCurrency() + "    " + v.getValue());
                bankCurrencies.put(v.getCurrency(), v.getValue());
            }

        }

    }

}