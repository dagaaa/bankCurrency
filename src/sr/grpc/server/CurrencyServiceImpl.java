package sr.grpc.server;

import io.grpc.stub.StreamObserver;
import sr.grpc.gen.*;
import sr.grpc.gen.CurrencyServiceGrpc.CurrencyServiceImplBase;
import sr.grpc.gen.Number;

import java.util.HashMap;
import java.util.LinkedList;

public class CurrencyServiceImpl extends CurrencyServiceImplBase {
    HashMap<CurrencyType, Double> currencies = new HashMap<>();

    CurrencyServiceImpl() {
        for (CurrencyType c : CurrencyType.values()) {
            currencies.put(c, 1.0);
        }
    }

    @Override
    public void subscribeForCurrency(RequestCurrency request,
                                     StreamObserver<ResponseCurrencyWithValues> responseObserver) {
        LinkedList<CurrencyValue> responseList = new LinkedList<>();
        while (true) {
            System.out.println("send currency with values");

            for (CurrencyType c : request.getCurrencyListList()) {

                responseList.add(CurrencyValue.newBuilder()
                        .setCurrency(c)
                        .setValue(currencies.get(c))
                        .build()
                );
            }
            ResponseCurrencyWithValues responseCurrency = ResponseCurrencyWithValues.newBuilder().addAllCurrencyValuesList(responseList).build();
            responseObserver.onNext(responseCurrency);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //TODO kiedy ma sie wywolac onComplete
        }
    }





}