package work.myfavs.framework.sb2.demo.util;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
/**
 * 事务处理工具类，支持在当前事务和新事务中执行代码.
 */
public class TransactionHandler {

  /**
   * 在当前事务中执行.
   *
   * @param supplier 待执行的函数
   * @param <T>      返回值类型
   * @return 执行结果
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public <T> T runInTransaction(Supplier<T> supplier) {
    return supplier.get();
  }

  /**
   * 在新事务中执行.
   *
   * @param supplier 待执行的函数
   * @param <T>      返回值类型
   * @return 执行结果
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public <T> T runInNewTransaction(Supplier<T> supplier) {
    return supplier.get();
  }
}
