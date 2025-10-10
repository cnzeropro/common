package org.zero.common.core.extension.hibernate.id;

import cn.hutool.core.util.IdUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * JPA 雪花算法 ID 生成器
 *
 * @author zero
 * @since 2022/2/21
 */
public class SnowFlakeIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return IdUtil.getSnowflakeNextId();
    }
}