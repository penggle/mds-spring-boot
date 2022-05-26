package org.springframework.boot.autoconfigure.mds;

import org.apache.ibatis.annotations.Mapper;

import java.lang.annotation.*;

/**
 * 基于命名(逻辑名称)的数据库注解.
 * 这里的数据库名既可以是物理上的(单库)也可以是逻辑上的(分库).
 *
 * 考虑到少数情况下某个XxxMapper接口可以为多个库复用，故需要支持多@NamedDatabase注解，例如下面这个数据迁移的例子中：
 *
 *      @NamedDatabase("dataMigrationSource") //数据迁移源头库
 *      @NamedDatabase("dataMigrationTarget") //数据迁移目标库
 *      public interface ProductMapper {
 *          ...
 *      }
 *
 * @author pengpeng
 * @since 2.1
 */
@Mapper
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Repeatable(NamedDatabases.class)
public @interface NamedDatabase {

    /**
     * 激活的数据库名(逻辑名称)
     * @return
     */
    String value();

}
