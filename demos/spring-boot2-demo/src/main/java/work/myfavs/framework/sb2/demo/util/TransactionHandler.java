package work.myfavs.framework.sb2.demo.util;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class TransactionHandler {

  @Transactional(propagation = Propagation.REQUIRED)
  public <T> T runInTransaction(Supplier<T> supplier) {
    return supplier.get();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public <T> T runInNewTransaction(Supplier<T> supplier) {
    return supplier.get();
  }
}
