package sr.grpc.client.Accounts;

import Bank.*;
import com.zeroc.Ice.Current;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

public class PremiumAccountI  extends AccountI implements PremiumAccount {

    private HashMap<sr.grpc.gen.CurrencyType,Double> currencies;
    PremiumAccountI(Person person, HashMap<sr.grpc.gen.CurrencyType,Double> hashMap) {
        super(person);
        currencies=hashMap;
    }

    @Override
    public CreditCost askForLoan(Credit credit, Current current) {
       // String string = "January 2, 2010";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        LocalDate start = LocalDate.parse(credit.startDate, formatter);
        int startMonth =start.getMonthValue();
        int startYear =start.getYear();

        LocalDate end= LocalDate.parse(credit.endDate, formatter);
        int endtMonth =end.getMonthValue();
        int endYear=end.getYear();
        int period= (endYear-startYear)*12+endtMonth-startMonth;
        Cost foreignVal = new Cost(credit.cost.currencyType,credit.cost.cost);
        foreignVal.cost+=0.015*foreignVal.cost*period;
        double plVal=foreignVal.cost*currencies.get(IceTogRPCCurrencyType(credit.cost.currencyType));

        return new CreditCost(foreignVal,plVal);
    }

    private sr.grpc.gen.CurrencyType IceTogRPCCurrencyType(CurrencyType type){
        return sr.grpc.gen.CurrencyType.valueOf(type.toString());
    }

}
