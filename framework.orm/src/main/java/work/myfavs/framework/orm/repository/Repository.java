package work.myfavs.framework.orm.repository;

import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.ReflectUtil;

@Slf4j
public class Repository<TModel>
    extends AbstractRepository {

  protected Class<TModel> modelClass;

  private DBTemplate     dbTemplate;
  private ClassMeta      classMeta;
  private GenerationType strategy;


  public Repository(DBTemplate dbTemplate) {

    super(dbTemplate);
    this.modelClass = ReflectUtil.getActualClassArg(this.getClass(), 0);
    this.classMeta = Metadata.get(this.modelClass);
    this.strategy = this.classMeta.getStrategy();
  }

}
