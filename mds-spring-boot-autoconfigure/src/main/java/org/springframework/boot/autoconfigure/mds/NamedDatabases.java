package org.springframework.boot.autoconfigure.mds;

import java.lang.annotation.*;

/**
 * 考虑到某个XxxMapper接口可以为多个库复用，故需要支持多@NamedDatabase注解
 *
 * @author pengpeng
 * @since 2.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface NamedDatabases {

    NamedDatabase[] value();

}
