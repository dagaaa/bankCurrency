package sr.grpc.client.Accounts;

import Bank.AccountFactory;
import Bank.AccountPrx;
import Bank.Person;
import Bank.PremiumAccountPrx;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import sr.grpc.gen.CurrencyType;

import java.util.HashMap;

public class AccountFactoryI implements AccountFactory {
    HashMap<sr.grpc.gen.CurrencyType, Double> currencyMap;

    public AccountFactoryI(HashMap<CurrencyType, Double> currencyMap) {
        this.currencyMap = currencyMap;
    }

    @Override
    public AccountPrx crete(Person person, double incomes, Current current) {
        if (incomes < 10_000)
            return AccountPrx.uncheckedCast(current.adapter.add(new AccountI(person), new Identity(person.pesel, "standard")));
        return PremiumAccountPrx.uncheckedCast(current.adapter.add(new PremiumAccountI(person, currencyMap), new Identity(person.pesel, "premium")));

    }
}
