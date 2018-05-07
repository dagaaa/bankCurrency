package sr.grpc.client.Accounts;

import Bank.*;
import com.zeroc.Ice.Current;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;

public class PremiumAccountI extends AccountI implements PremiumAccount {

    private HashMap<sr.grpc.gen.CurrencyType, Double> currencies;

    PremiumAccountI(Person person, HashMap<sr.grpc.gen.CurrencyType, Double> hashMap) {
        super(person);
        currencies = hashMap;
    }

    @Override
    public CreditCost askForLoan(Credit credit, Current current) throws UnsupportedCurrencyException, DateFormatException {
        // String string = "January 2, 2010";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        try {
            LocalDate start = LocalDate.parse(credit.startDate, formatter);
            int startMonth = start.getMonthValue();
            int startYear = start.getYear();

            LocalDate end = LocalDate.parse(credit.endDate, formatter);

            int endtMonth = end.getMonthValue();
            int endYear = end.getYear();
            int period = (endYear - startYear) * 12 + endtMonth - startMonth;
            Cost foreignVal = new Cost(credit.cost.currencyType, credit.cost.cost);
            foreignVal.cost += 0.015 * foreignVal.cost * period;
            sr.grpc.gen.CurrencyType cType = IceTogRPCCurrencyType(credit.cost.currencyType);
            double plVal = foreignVal.cost * currencies.get(cType);

            return new CreditCost(foreignVal, plVal);
        } catch (DateTimeParseException ex) {
            throw new DateFormatException();
        }
    catch (NullPointerException ex){
        throw new UnsupportedCurrencyException();
    }
    }

    private sr.grpc.gen.CurrencyType IceTogRPCCurrencyType(CurrencyType type) {

        return sr.grpc.gen.CurrencyType.valueOf(type.toString());

    }

}
