package sr.grpc.server;

import io.grpc.stub.StreamObserver;
import sr.grpc.gen.CurrencyServiceGrpc.CurrencyServiceImplBase;
import sr.grpc.gen.CurrencyType;
import sr.grpc.gen.CurrencyValue;
import sr.grpc.gen.RequestCurrency;
import sr.grpc.gen.ResponseCurrencyWithValues;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;

public class CurrencyServiceImpl extends CurrencyServiceImplBase {
    private HashMap<CurrencyType, Double> currencies = new HashMap<>();

    private Timer timer= new Timer();


    CurrencyServiceImpl() {
        for (CurrencyType c : CurrencyType.values()) {
            currencies.put(c, 4.0);
        }
        timer.scheduleAtFixedRate(new UpdateTask(currencies),0,10_000);
    }

    @Override
    public void subscribeForCurrency(RequestCurrency request,
                                     StreamObserver<ResponseCurrencyWithValues> responseObserver) {
        LinkedList<CurrencyValue> responseList ;
        while (true) {
            responseList=new LinkedList<>();
            System.out.println("send currency with values");

            for (CurrencyType c : request.getCurrenciesList()) {

                responseList.add(CurrencyValue.newBuilder()
                        .setCurrency(c)
                        .setValue(currencies.get(c))
                        .build()
                );
            }
            ResponseCurrencyWithValues responseCurrency = ResponseCurrencyWithValues.newBuilder().addAllCurrencyValues(responseList).build();
            responseObserver.onNext(responseCurrency);
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }





}
