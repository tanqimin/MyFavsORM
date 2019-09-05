package work.myfavs.framework.example.domain.dto;

import lombok.Data;
import work.myfavs.framework.orm.meta.pagination.IPageable;

@Data
public class PageableDTO
    implements IPageable {

  private Boolean enablePage = false;
  private int     currentPage;
  private int     pageSize;

}
