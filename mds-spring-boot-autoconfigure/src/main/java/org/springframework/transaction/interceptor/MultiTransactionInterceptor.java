package org.springframework.transaction.interceptor;

import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableMultiTransactionManagement;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义的支持多事务(多数据源)的事务拦截器
 * 通过spring-data-commons中提供的{@link ChainedTransactionManager}来解决单个JVM中的跨库事务问题。
 * 注意：广义上的分布式事务(贯穿多个JVM中的数据库事务)此方案是解决不了的，这个还得通过专业的分布式事务中间件来解决!
 *
 * @author pengpeng
 * @since 2.1
 */
public class MultiTransactionInterceptor extends TransactionInterceptor {

    private final ConcurrentMap<TransactionManagerKey,TransactionManager> transactionManagerCache = new ConcurrentReferenceHashMap<>(4);

    private final String defaultTransactionManagerName = EnableMultiTransactionManagement.DEFAULT_TRANSACTION_MANAGER_NAME;

    public MultiTransactionInterceptor(TransactionAttributeSource transactionAttributeSource) {
        Assert.notNull(transactionAttributeSource, "Parameter 'transactionAttributeSource' must be required!");
        setTransactionAttributeSource(transactionAttributeSource);
    }

    /**
     * Determine the specific transaction manager to use for the given transaction.
     */
    @Nullable
    protected TransactionManager determineTransactionManager(@Nullable TransactionAttribute txAttr) {
        // Do not attempt to lookup tx manager if no tx attributes are set
        if (txAttr == null) {
            return getDefaultTransactionManager();
        }

        String qualifier = txAttr.getQualifier();
        if (StringUtils.hasText(qualifier)) {
            return getQualifiedTransactionManager(qualifier);
        } else {
            return getDefaultTransactionManager();
        }
    }

    protected PlatformTransactionManager getDefaultTransactionManager() {
        return getBeanFactory().getBean(defaultTransactionManagerName, PlatformTransactionManager.class);
    }

    protected TransactionManager getQualifiedTransactionManager(String qualifier) {
        TransactionManagerKey transactionManagerKey = new TransactionManagerKey(qualifier);
        return transactionManagerCache.computeIfAbsent(transactionManagerKey, this::createQualifiedTransactionManager);
    }

    protected TransactionManager createQualifiedTransactionManager(TransactionManagerKey transactionManagerKey) {
        Set<String> transactionManagerNames = transactionManagerKey.getTransactionManagerNames();
        //如果当前声明式事务指定的是单个事务管理器? 例如：@Transactional(transactionManager="productTransactionManager", ...)
        if(transactionManagerNames.size() == 1) {
            return getTransactionManagerBean(transactionManagerNames.iterator().next());
        } else { //如果当前声明式事务指定的是多个事务管理器? 例如：@Transactional(transactionManager="productTransactionManager,orderTransactionManager", ...)
            PlatformTransactionManager[] transactionManagers = transactionManagerNames.stream()
                    .map(this::getTransactionManagerBean)
                    .toArray(PlatformTransactionManager[]::new);
            return new ChainedTransactionManager(transactionManagers);
        }
    }

    private PlatformTransactionManager getTransactionManagerBean(String transactionManagerQualifier) {
        return BeanFactoryAnnotationUtils.qualifiedBeanOfType(getBeanFactory(), PlatformTransactionManager.class, transactionManagerQualifier);
    }

    public String getDefaultTransactionManagerName() {
        return defaultTransactionManagerName;
    }

    protected ConcurrentMap<TransactionManagerKey,TransactionManager> getTransactionManagerCache() {
        return transactionManagerCache;
    }

    public static class TransactionManagerKey {

        public static final String TRANSACTION_MANAGER_QUALIFIER_SUFFIX = TransactionManager.class.getSimpleName();

        private final Set<String> transactionManagerNames;

        private final String transactionManagerQualifier;

        public TransactionManagerKey(String transactionManagerQualifier) {
            Set<String> transactionManagerNames = Stream.of(transactionManagerQualifier.split(","))
                    .filter(StringUtils::hasText)
                    .map(this::prepareTransactionManagerQualifier)
                    .sorted(String::compareTo)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            Assert.notEmpty(transactionManagerNames, String.format("Illegal TransactionManager qualifier: %s, may be you need to check @Transactional#transactionManager()", transactionManagerQualifier));
            this.transactionManagerNames = Collections.unmodifiableSet(transactionManagerNames);
            this.transactionManagerQualifier = String.join(",", transactionManagerNames);
        }

        private String prepareTransactionManagerQualifier(String qualifier) {
            qualifier = qualifier.trim();
            if(!qualifier.endsWith(TRANSACTION_MANAGER_QUALIFIER_SUFFIX)) {
                return qualifier + TRANSACTION_MANAGER_QUALIFIER_SUFFIX;
            }
            return qualifier;
        }

        public Set<String> getTransactionManagerNames() {
            return transactionManagerNames;
        }

        public String getTransactionManagerQualifier() {
            return transactionManagerQualifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TransactionManagerKey)) return false;
            TransactionManagerKey that = (TransactionManagerKey) o;
            return transactionManagerQualifier.equals(that.transactionManagerQualifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(transactionManagerQualifier);
        }

        @Override
        public String toString() {
            return transactionManagerQualifier;
        }
    }

}
