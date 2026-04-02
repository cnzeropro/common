package org.zero.common.core.extension.spring.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.zero.common.data.model.persistent.Identifiable;
import org.zero.common.data.model.persistent.SoftDeletable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 带有逻辑删除的 Repository 基类，entity 必须实现 {@link SoftDeletable} 与 {@link Identifiable}
 *
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.spring.io/spring-data/commons/reference/repositories/query-keywords-reference.html">Repository query keywords</a>
 * @see <a href="https://docs.spring.io/spring-data/commons/reference/repositories/query-return-types-reference.html">Repository query return types</a>
 * @since 2025/2/17
 */
@NoRepositoryBean
public interface BaseRepository<T extends SoftDeletable<?> & Identifiable<?>, ID extends Serializable> extends JpaRepository<T, ID> {
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.id = ?1 and e.deleted = 0")
    T logicFindById(ID id);

    @Transactional(readOnly = true)
    default Optional<T> logicFindOptById(ID id) {
        return Optional.ofNullable(this.logicFindById(id));
    }

    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.id in ?1 and e.deleted = 0")
    List<T> logicFindAllById(Iterable<ID> ids);

    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.deleted = 0")
    List<T> logicFindAll();

    @Transactional(readOnly = true)
    default boolean logicExistsById(ID id) {
        return Objects.nonNull(this.logicFindById(id));
    }

    @Transactional(readOnly = true)
    @Query("select count(e) from #{#entityName} e where e.deleted = 0")
    long logicCount();

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update #{#entityName} e set e.deleted = 1 where e.id = ?1")
    long logicDeleteById(ID id);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update #{#entityName} e set e.deleted = 1 where e.id in ?1")
    void logicDelete(Iterable<? extends T> entities);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update #{#entityName} e set e.deleted = 1")
    void logicDeleteAll();
}
