package org.springframework.boot.autoconfigure.mds.mybatis;

import org.springframework.boot.autoconfigure.mds.NamedDatabase;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.util.ClassUtils;

/**
 * 基于@NamedDatabase("dbName")注解，通过区分数据库来决定为哪些库生成哪些Mapper实现
 *
 * @author pengpeng
 * @since 2.1
 */
public class DatabasedMapperExcludeFilter extends AbstractTypeHierarchyTraversingFilter {

    private final String targetDatabase;

    protected DatabasedMapperExcludeFilter(String targetDatabase) {
        super(true, true);
        this.targetDatabase = targetDatabase;
    }

    @Override
    protected boolean matchSelf(MetadataReader metadataReader) {
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        //如果当前XxxMapper上标注的@NamedDatabase所指定的数据库名与当前目标数据库都不匹配,返回true(代表需要exclude)
        return annotationMetadata.getAnnotations()
                .stream(NamedDatabase.class)
                .noneMatch(mergedAnotation -> targetDatabase.equals(mergedAnotation.getString("value")));
    }

    @Override
    protected Boolean matchSuperClass(String superClassName) {
        return matchDatabase(superClassName);
    }

    @Override
    protected Boolean matchInterface(String interfaceName) {
        return matchDatabase(interfaceName);
    }

    /**
     * 如果当前XxxMapper的@NamedDatabase所指定的数据库名与当前目标数据库都不匹配,返回true(代表需要exclude)
     * @param typeName
     * @return
     */
    protected Boolean matchDatabase(String typeName) {
        try {
            Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
            NamedDatabase[] namedDatabases = clazz.getAnnotationsByType(NamedDatabase.class);
            if(namedDatabases.length > 0) {
                for(NamedDatabase namedDatabase : namedDatabases) {
                    if(targetDatabase.equals(namedDatabase.value())) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            // Class not regularly loadable - can't determine a match that way.
        }
        return false;
    }


}