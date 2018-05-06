module Bank
{
 enum CurrencyType {    EUR ,
                        USD ,
                        GBP ,
                        CAD };
  class  Person{
    string name;
    string surname;
    string pesel;
  };


class Cost{
 CurrencyType currencyType;
 double cost;
};

 class Credit{
    string startDate;
    string endDate;
    Cost cost;
 };
 class CreditCost{
    Cost foreignVal;
    double plCost;
 };
  interface Account {
   double getBalance();
   void transfer(double money);
  };

  interface PremiumAccount extends Account{
    CreditCost askForLoan(Credit credit);
  };

  interface AccountFactory{
    Account* crete(Person person, double incomes);
  };

};