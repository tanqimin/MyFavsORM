package work.myfavs.framework.orm.repository;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.ReflectUtil;

@Slf4j
public class Repository<TModel>
    implements Serializable {

  protected Class<TModel> modelClass;

  private DBTemplate     dbTemplate;
  private ClassMeta      classMeta;
  private GenerationType strategy;

  private Repository() {

    this.modelClass = ReflectUtil.getActualClassArg(this.getClass(), 0);
    this.classMeta = Metadata.get(this.modelClass);
    this.strategy = this.classMeta.getStrategy();
  }

  public Repository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
  }

}
