package work.myfavs.framework.orm.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;

import java.sql.Connection;
import java.util.function.Consumer;

/**
 * 事务基类
 *
 * @author tanqimin
 */
public abstract class BaseService {

  private static final int[] ISOLATION_LEVEL_SCOPE =
      new int[]{
          Connection.TRANSACTION_NONE,
          Connection.TRANSACTION_READ_UNCOMMITTED,
          Connection.TRANSACTION_READ_COMMITTED,
          Connection.TRANSACTION_REPEATABLE_READ,
          Connection.TRANSACTION_SERIALIZABLE
      };

  @Autowired
  private PlatformTransactionManager transactionManager;

  /**
   * 在事务中执行回调.
   *
   * @param <T>      返回值类型泛型
   * @param callback 事务回调
   * @return 回调返回值
   */
  protected <T> T tx(TransactionCallback<T> callback) {
    return tx(callback, -1, -1, false);
  }

  /**
   * 在事务中执行回调，指定超时时间.
   *
   * @param <T>      返回值类型泛型
   * @param callback 事务回调
   * @param timeout  超时时间（秒），-1 表示使用默认值
   * @return 回调返回值
   */
  protected <T> T tx(TransactionCallback<T> callback, int timeout) {
    return tx(callback, -1, timeout, false);
  }

  /**
   * 在事务中执行回调，指定是否只读.
   *
   * @param <T>      返回值类型泛型
   * @param callback 事务回调
   * @param readOnly 是否只读事务
   * @return 回调返回值
   */
  protected <T> T tx(TransactionCallback<T> callback, boolean readOnly) {
    return tx(callback, -1, -1, readOnly);
  }

  /**
   * 在事务中执行回调，指定隔离级别和超时时间.
   *
   * @param <T>            返回值类型泛型
   * @param callback       事务回调
   * @param isolationLevel 事务隔离级别
   * @param timeout        超时时间（秒），-1 表示使用默认值
   * @return 回调返回值
   */
  protected <T> T tx(TransactionCallback<T> callback, int isolationLevel, int timeout) {
    return tx(callback, isolationLevel, timeout, false);
  }

  /**
   * 在事务中执行回调，指定只读和超时时间.
   *
   * @param <T>      返回值类型泛型
   * @param callback 事务回调
   * @param readOnly 是否只读事务
   * @param timeout  超时时间（秒），-1 表示使用默认值
   * @return 回调返回值
   */
  protected <T> T tx(TransactionCallback<T> callback, boolean readOnly, int timeout) {
    return tx(callback, -1, timeout, readOnly);
  }

  /**
   * 在事务中执行无返回值的操作.
   *
   * @param consumer 事务消费者
   */
  protected void tx(Consumer<TransactionStatus> consumer) {
    tx(consumer, -1, -1, false);
  }

  /**
   * 在事务中执行无返回值的操作，指定超时时间.
   *
   * @param consumer 事务消费者
   * @param timeout  超时时间（秒），-1 表示使用默认值
   */
  protected void tx(Consumer<TransactionStatus> consumer, int timeout) {
    tx(consumer, -1, timeout, false);
  }

  /**
   * 在事务中执行无返回值的操作，指定是否只读.
   *
   * @param consumer 事务消费者
   * @param readOnly 是否只读事务
   */
  protected void tx(Consumer<TransactionStatus> consumer, boolean readOnly) {
    tx(consumer, -1, -1, readOnly);
  }

  /**
   * 在事务中执行无返回值的操作，指定隔离级别和超时时间.
   *
   * @param consumer       事务消费者
   * @param isolationLevel 事务隔离级别
   * @param timeout        超时时间（秒），-1 表示使用默认值
   */
  protected void tx(Consumer<TransactionStatus> consumer, int isolationLevel, int timeout) {
    tx(consumer, isolationLevel, timeout, false);
  }

  /**
   * 在事务中执行无返回值的操作，指定只读和超时时间.
   *
   * @param consumer 事务消费者
   * @param readOnly 是否只读事务
   * @param timeout  超时时间（秒），-1 表示使用默认值
   */
  protected void tx(Consumer<TransactionStatus> consumer, boolean readOnly, int timeout) {
    tx(consumer, -1, timeout, readOnly);
  }

  private void tx(
      Consumer<TransactionStatus> consumer, int isolationLevel, int timeout, boolean readOnly) {
    final DefaultTransactionDefinition td =
        createTransDefinition(isolationLevel, timeout, readOnly);

    final TransactionStatus status = transactionManager.getTransaction(td);
    try {
      consumer.accept(status);
      transactionManager.commit(status);
    } catch (Exception e) {
      transactionManager.rollback(status);
      throw e;
    }
  }

  private <T> T tx(
      TransactionCallback<T> callback, int isolationLevel, int timeout, boolean readOnly) {
    final DefaultTransactionDefinition td =
        createTransDefinition(isolationLevel, timeout, readOnly);

    final TransactionStatus status = transactionManager.getTransaction(td);
    try {
      final T res = callback.doInTransaction(status);
      transactionManager.commit(status);
      return res;
    } catch (Exception e) {
      transactionManager.rollback(status);
      throw e;
    }
  }

  private DefaultTransactionDefinition createTransDefinition(
      int isolationLevel, int timeout, boolean readOnly) {
    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();

    for (int i : ISOLATION_LEVEL_SCOPE) {
      if (i == isolationLevel) {
        definition.setIsolationLevel(isolationLevel);
        break;
      }
    }

    definition.setReadOnly(readOnly);
    definition.setTimeout(timeout);
    return definition;
  }
}
