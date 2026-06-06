package work.myfavs.framework.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@ServletComponentScan
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

  // Spring应用启动起来
  public static void main(String[] args) {

    SpringApplication.run(Application.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

    return builder.sources(Application.class);
  }
}
