package com.penglecode.codeforce.mybatismds.examples.common.mybatis;



import com.penglecode.codeforce.common.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Mybatis-Mapper辅助类
 *
 * @author pengpeng
 * @since 2.1
 * @created 2021/5/15 14:02
 */
public class MapperHelper {

    private MapperHelper() {}

    public static String getMapperKey(Class<?> domainClass, String key) {
        return domainClass.getName() + "Mapper." + key;
    }

    public static boolean isEmpty(Object paramObj) {
        return !isNotEmpty(paramObj);
    }

    public static boolean isNotEmpty(Object paramObj) {
        if (paramObj == null) {
            return false;
        }
        if (paramObj instanceof String) {
            String str = (String) paramObj;
            return StringUtils.isNotEmpty(str);
        }
        if (paramObj.getClass().isArray()) {
            return Array.getLength(paramObj) > 0;
        }
        if (paramObj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) paramObj;
            return !map.isEmpty();
        }
        if (paramObj instanceof Collection) {
            Collection<?> collection = (Collection<?>) paramObj;
            return !collection.isEmpty();
        }
        return true;
    }

    public static boolean isArrayOrCollection(Object paramObj) {
        if (paramObj == null) {
            return false;
        }
        return paramObj instanceof Collection || paramObj.getClass().isArray();
    }
    
    public static boolean containsColumn(Map<String,Object> columnNames, String columnName) {
    	if(columnNames != null) {
    		return columnNames.containsKey(columnName);
    	}
    	return false;
    }

}
