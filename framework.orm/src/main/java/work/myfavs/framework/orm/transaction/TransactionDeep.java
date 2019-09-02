package work.myfavs.framework.orm.transaction;

import java.util.HashMap;
import java.util.Map;
import work.myfavs.framework.orm.util.exception.DBException;

public class TransactionDeep {

  private int                   currentTransactionDeep;
  private Map<Integer, Integer> logs = new HashMap<>();

  public TransactionDeep() {

    this.setCurrentTransactionDeep(1);

  }

  public void log(int transactionDeep, int isolation) {

    this.setCurrentTransactionDeep(transactionDeep);
    this.logs.put(transactionDeep, isolation);
  }

  public int getIsolation(int transactionDeep) {

    if (!this.logs.containsKey(transactionDeep)) {
      throw new DBException("Could not found the transaction isolation with transaction " + transactionDeep);
    }
    return this.logs.get(transactionDeep);
  }

  public int getCurrentTransactionDeep() {

    return currentTransactionDeep;
  }

  public void setCurrentTransactionDeep(int currentTransactionDeep) {

    this.currentTransactionDeep = currentTransactionDeep;
  }

}
