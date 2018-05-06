package sr.grpc.client.Accounts;

import Bank.Account;
import Bank.Person;
import com.zeroc.Ice.Current;

public class AccountI implements Account {

    private double balance;
    private Person person;
    AccountI(Person person){
        this.person=person;
        this.balance=0;
    }

    @Override
    public double getBalance(Current current) {
        return balance;
    }

    @Override
    public void transfer(double money, Current current) {
            balance+=money;
    }
}
