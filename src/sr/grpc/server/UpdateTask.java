package sr.grpc.server;

import sr.grpc.gen.CurrencyType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Random;
import java.util.TimerTask;

public class UpdateTask extends TimerTask{
    private HashMap<CurrencyType,Double> currencyHashMap;

    public UpdateTask(HashMap<CurrencyType,Double> hashMap){
        currencyHashMap=hashMap;
    }
    @Override
    public void run() {
        Random random = new Random();
        double value;
        double d;
        for(CurrencyType c: currencyHashMap.keySet()) {
            value=currencyHashMap.get(c);
            d = random.nextDouble() * (0.6) - 0.3;

            BigDecimal bd = new BigDecimal(Double.toString(value+d));
            bd = bd.setScale(2, RoundingMode.HALF_UP);

            currencyHashMap.put(c,bd.doubleValue());
        }
    }
}
