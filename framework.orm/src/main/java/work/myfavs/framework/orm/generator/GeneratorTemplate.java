package work.myfavs.framework.orm.generator;

import java.io.IOException;
import java.util.Map;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import work.myfavs.framework.orm.util.exception.DBException;

public class GeneratorTemplate {

  private ClasspathResourceLoader resourceLoader;
  private Configuration           configuration;
  private GroupTemplate           groupTemplate;
  private Template                template;

  public GeneratorTemplate() {

    try {
      resourceLoader = new ClasspathResourceLoader("template/");
      configuration = Configuration.defaultConfiguration();

//      template = groupTemplate.getTemplate("/hello.txt");
    } catch (IOException e) {
      throw new DBException(e);
    }
  }

  /**
   * 跟进模板渲染
   *
   * @param templateFile 模板路径如：/hello.txt
   * @param params       模板参数
   *
   * @return 模板
   */
  public String render(String templateFile, Map<String, Object> params) {

    groupTemplate = new GroupTemplate(resourceLoader, configuration);
    template = groupTemplate.getTemplate(templateFile);
    groupTemplate.setSharedVars(params);
    return template.render();
  }

}
