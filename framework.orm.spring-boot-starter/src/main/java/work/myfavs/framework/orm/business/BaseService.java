package work.myfavs.framework.orm.business;

import cn.hutool.core.util.ArrayUtil;
import java.sql.Connection;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;

/**
 * 事务基类
 *
 * @author tanqimin
 */
public abstract class BaseService {

  private static final int[] ISOLATION_LEVEL_SCOPE =
      new int[] {
        Connection.TRANSACTION_NONE,
        Connection.TRANSACTION_READ_UNCOMMITTED,
        Connection.TRANSACTION_READ_COMMITTED,
        Connection.TRANSACTION_REPEATABLE_READ,
        Connection.TRANSACTION_SERIALIZABLE
      };

  @Autowired private PlatformTransactionManager transactionManager;

  protected <T> T tx(TransactionCallback<T> callback) {
    return tx(callback, -1, -1, false);
  }

  protected <T> T tx(TransactionCallback<T> callback, int timeout) {
    return tx(callback, -1, timeout, false);
  }

  protected <T> T tx(TransactionCallback<T> callback, boolean readOnly) {
    return tx(callback, -1, -1, readOnly);
  }

  protected <T> T tx(TransactionCallback<T> callback, int isolationLevel, int timeout) {
    return tx(callback, isolationLevel, timeout, false);
  }

  protected <T> T tx(TransactionCallback<T> callback, boolean readOnly, int timeout) {
    return tx(callback, -1, timeout, readOnly);
  }

  protected void tx(Consumer<TransactionStatus> consumer) {
    tx(consumer, -1, -1, false);
  }

  protected void tx(Consumer<TransactionStatus> consumer, int timeout) {
    tx(consumer, -1, timeout, false);
  }

  protected void tx(Consumer<TransactionStatus> consumer, boolean readOnly) {
    tx(consumer, -1, -1, readOnly);
  }

  protected void tx(Consumer<TransactionStatus> consumer, int isolationLevel, int timeout) {
    tx(consumer, isolationLevel, timeout, false);
  }

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
    if (ArrayUtil.contains(ISOLATION_LEVEL_SCOPE, isolationLevel)) {
      definition.setIsolationLevel(isolationLevel);
    }
    definition.setReadOnly(readOnly);
    definition.setTimeout(timeout);
    return definition;
  }
}
