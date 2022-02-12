package work.myfavs.framework.example.aspect;

import cn.hutool.core.util.StrUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import work.myfavs.framework.example.util.tenant.DynamicDataSourceContextHolder;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Order(1)
public class DataSourceAspect {

  Logger logger = LoggerFactory.getLogger(getClass());

  @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
  private void cutController() {}

  @Before("cutController()")
  public void before(JoinPoint joinPoint) {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) return;
    HttpServletRequest request = attributes.getRequest();
    // 租户标识
    String sign = request.getHeader("tenant-info");
    if (StrUtil.isNotEmpty(sign)) {
      DynamicDataSourceContextHolder.setDataSource(sign);
    } else {
      DynamicDataSourceContextHolder.clearDataSource();
    }
  }
}
