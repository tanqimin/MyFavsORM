package work.myfavs.framework.orm.meta.enumeration;

/** 用于描述@Cond中的Operator */
public enum Operator {
  EQUALS,
  NOT_EQUALS,
  LIKE,
  IS_NULL,
  IS_NOT_NULL,
  GREATER_THAN,
  GREATER_THAN_OR_EQUALS,
  LESS_THAN,
  LESS_THAN_OR_EQUALS,
  BETWEEN_START,
  BETWEEN_END

  /*  ,
  IN,
  NOT_IN,
  EXISTS,
  NOT_EXISTS*/
}
