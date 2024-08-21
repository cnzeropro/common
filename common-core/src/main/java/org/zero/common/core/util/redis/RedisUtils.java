package org.zero.common.core.util.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.experimental.UtilityClass;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.zero.common.core.util.spring.context.SpringContextUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redisTemplate封装
 *
 * @author Zero
 * @since 2021/10/20 13:36
 */
@UtilityClass
public class RedisUtils {
    /**
     * 指定key的失效时间
     * <p>
     * expire
     *
     * @param key     键
     * @param timeout 时间（秒）
     * @return
     */
    public static boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 指定某个key的失效时间
     * <p>
     * expire
     *
     * @param key     键
     * @param timeout 时间
     * @param unit    时间单位
     * @return
     */
    public static boolean expire(String key, long timeout, TimeUnit unit) {
        if (Objects.isNull(key)) {
            return false;
        }
        if (Objects.isNull(unit)) {
            unit = TimeUnit.SECONDS;
        }

        return Boolean.TRUE.equals(getRedisTemplate().expire(key, timeout, unit));
    }

    /**
     * 指定某个key在多久失效
     * <p>
     * expire
     *
     * @param key
     * @param date
     * @return
     */
    public static boolean expire(String key, Date date) {
        if (Objects.isNull(key) || Objects.isNull(date)) {
            return false;
        }
        return Boolean.TRUE.equals(getRedisTemplate().expireAt(key, date));
    }

    /**
     * 持久化key（移除key过期时间）
     * <p>
     * persist
     *
     * @param key
     * @return
     */
    public static boolean persist(String key) {
        if (Objects.isNull(key)) {
            return false;
        }
        return Boolean.TRUE.equals(getRedisTemplate().persist(key));
    }

    /**
     * 获取key的过期时间，默认时间单位秒
     * <p>
     * 从Redis 2.8开始，如果key不存在或者已过期，返回-2；如果key存在并且没有设置过期时间（永久有效），返回-1
     * <p>
     * ttl
     * pttl
     *
     * @param key 键
     * @return
     */
    public static long ttl(String key) {
        return ttl(key, TimeUnit.SECONDS);
    }

    /**
     * 获取key的过期时间
     * <p>
     * 从Redis 2.8开始，如果key不存在或者已过期，返回-2；如果key存在并且没有设置过期时间（永久有效），返回-1
     * <p>
     * ttl
     * pttl
     *
     * @param key
     * @param unit
     * @return
     */
    public static long ttl(String key, TimeUnit unit) {
        Long timeout = getRedisTemplate().getExpire(key, unit);
        return Objects.isNull(timeout) ? Long.MIN_VALUE : timeout;
    }

    /**
     * 检查给定key是否存在
     * <p>
     * exists
     *
     * @param key
     * @return
     */
    public static boolean exists(String key) {
        return Boolean.TRUE.equals(getRedisTemplate().hasKey(key));
    }

    /**
     * 查找所有符合给定模式（pattern）的key
     * <p>
     * keys
     *
     * @param pattern
     * @return
     */
    public static Set<String> keys(String pattern) {
        if (Objects.isNull(pattern)) {
            return Collections.emptySet();
        }
        return getRedisTemplate().keys(pattern);
    }

    /**
     * 删除key
     * <p>
     * del
     *
     * @param keys
     */
    public static boolean del(String... keys) {
        Boolean deleted = Boolean.TRUE;
        if (ArrayUtil.isNotEmpty(keys)) {
            if (keys.length == 1) {
                deleted = getRedisTemplate().delete(keys[0]);
            } else {
                deleted = Optional.ofNullable(getRedisTemplate().delete(Arrays.asList(keys))).map(c -> c <= 0).orElse(Boolean.FALSE);
            }
        }
        return Boolean.TRUE.equals(deleted);
    }

    /**
     * 重命名key
     * <p>
     * rename
     *
     * @param oldKey
     * @param newKey
     * @return
     */
    public static boolean rename(String oldKey, String newKey) {
        if (Objects.isNull(oldKey) || Objects.isNull(newKey)) {
            return false;
        }
        getRedisTemplate().rename(oldKey, newKey);
        return true;
    }

    /**
     * 返回key所储存的值的类型
     * <p>
     * type
     *
     * @param key
     * @return
     */
    public static DataType type(String key) {
        return getRedisTemplate().type(key);
    }

    /* ************************************************ String ************************************************ */

    /**
     * 获取指定key的值
     * <p>
     * get
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        if (Objects.isNull(key)) {
            return null;
        }
        return getRedisTemplate().opsForValue().get(key);
    }

    /**
     * 获取指定key的值，并转成提供的类型
     * <p>
     * get
     *
     * @param key 键
     * @return 值
     */
    public static <T> T get(String key, Class<T> clazz) {
        return clazz.cast(get(key));
    }

    /**
     * 获取指定key的值
     * <p>
     * get
     *
     * @param keys
     * @return
     */
    public static List<Object> multiGet(String... keys) {
        if (ArrayUtil.isEmpty(keys)) {
            return Collections.emptyList();
        }
        return multiGet(Arrays.asList(keys));
    }

    /**
     * 获取指定key的值
     * <p>
     * get
     *
     * @param keys
     * @return
     */
    public static List<Object> multiGet(Collection<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return Collections.emptyList();
        }
        return getRedisTemplate().opsForValue().multiGet(keys);
    }

    /**
     * 设置指定key的值
     * <p>
     * set
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static boolean set(String key, Object value) {
        if (Objects.isNull(key)) {
            return false;
        }
        getRedisTemplate().opsForValue().set(key, value);
        return true;
    }

    /**
     * 设置指定key的值
     * <p>
     * set
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    public static boolean set(String key, Object value, long timeout) {
        return set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置指定key的值并设置过期时间
     * <p>
     * set
     *
     * @param key
     * @param value
     * @param timeout
     * @param unit
     * @return
     */
    public static boolean set(String key, Object value, long timeout, TimeUnit unit) {
        if (Objects.isNull(key)) {
            return false;
        }
        getRedisTemplate().opsForValue().set(key, value, timeout, unit);
        return true;
    }

    /**
     * 设置指定key的值
     * <p>
     * set
     *
     * @param map
     * @return
     */
    public static boolean multiSet(Map<String, Object> map) {
        if (CollUtil.isEmpty(map)) {
            return false;
        }
        getRedisTemplate().opsForValue().multiSet(map);
        return true;
    }

    /**
     * 获取指定key的值并设置成新值
     * 原本RedisTemplate.getAndSet方法会丢失ttl，本工具进行处理
     * <p>
     * getset 从Redis 6.2开始过期
     *
     * @param key
     * @param value
     * @return
     */
    public static Object getAndSet(String key, Object value) {
        long ttl = ttl(key);
        Object oldValue = getRedisTemplate().opsForValue().getAndSet(key, value);
        expire(key, ttl);
        return oldValue;
    }

    /**
     * 获取指定key的值并设置成新值，旧值转成提供的类型
     * 原本RedisTemplate.getAndSet方法会丢失ttl，本工具进行处理
     * <p>
     * getset 从Redis 6.2开始过期
     *
     * @param key
     * @param value
     * @return
     */
    public static <T> T getAndSet(String key, Object value, Class<T> clazz) {
        return clazz.cast(getAndSet(key, value));
    }

    /**
     * 递增并获取结果
     * 如果key不存在，那么key的值会先被初始化为0，然后再执行该命令
     * <p>
     * incr
     *
     * @param key 键
     * @return
     */
    public static Long incr(String key) {
        return getRedisTemplate().opsForValue().increment(key);
    }

    /**
     * 递增并获取结果
     * 具体是递增还是递减取决于传入值的正负
     * 如果key不存在，那么key的值会先被初始化为0，然后再执行该命令
     * <p>
     * incrby
     *
     * @param key   键
     * @param delta 递增因子
     * @return
     */
    public static Long incr(String key, long delta) {
        return getRedisTemplate().opsForValue().increment(key, delta);
    }

    /**
     * 递增并获取结果
     * 具体是递增还是递减取决于传入值的正负
     * 如果key不存在，那么key的值会先被初始化为0，然后再执行该命令
     * <p>
     * incrbyfloat
     *
     * @param key   键
     * @param delta 递增因子
     * @return
     */
    public static Double incr(String key, double delta) {
        return getRedisTemplate().opsForValue().increment(key, delta);
    }

    /**
     * 递减并获取结果
     * 如果key不存在，那么key的值会先被初始化为0，然后再执行该命令
     * <p>
     * decr
     *
     * @param key 键
     * @return
     */
    public static Long decr(String key) {
        return getRedisTemplate().opsForValue().decrement(key);
    }

    /**
     * 递减并获取结果
     * 具体是递减还是递增取决于传入值的正负
     * 如果key不存在，那么key的值会先被初始化为0，然后再执行该命令
     * <p>
     * decrby
     *
     * @param key   键
     * @param delta 递减因子
     * @return
     */
    public static Long decr(String key, long delta) {
        return getRedisTemplate().opsForValue().decrement(key, delta);
    }

    /**
     * 递减并获取结果
     * 具体是递减还是递增取决于传入值的正负
     * 如果key不存在，那么key的值会先被初始化为0，然后再执行该命令
     * <p>
     * incrbyfloat
     *
     * @param key   键
     * @param delta 递减因子
     * @return
     */
    public static Double decr(String key, double delta) {
        return incr(key, -delta);
    }

    /* ********************************************* Map ********************************************* */

    /**
     * 获取存储在哈希表中指定字段的值
     * <p>
     * hget
     *
     * @param key   键
     * @param field 项
     * @return 值
     */
    public static Object hGet(String key, Object field) {
        return getRedisTemplate().opsForHash().get(key, field);
    }

    /**
     * 获取存储在哈希表中指定字段的值
     * <p>
     * hget
     *
     * @param key   键
     * @param field 项
     * @return 值
     */
    public static <T> T hGet(String key, Object field, Class<T> clazz) {
        return clazz.cast(hGet(key, field));
    }

    /**
     * 获取存储在哈希表中指定字段的值
     * <p>
     * hmget
     *
     * @param key    键
     * @param fields 项
     * @return 值
     */
    public static List<Object> hmGet(String key, Object... fields) {
        return hmGet(key, Arrays.asList(fields));
    }

    /**
     * 获取存储在哈希表中指定字段的值
     * <p>
     * hmget
     *
     * @param key    键
     * @param fields 项
     * @return 值
     */
    public static List<Object> hmGet(String key, Collection<Object> fields) {
        return getRedisTemplate().opsForHash().multiGet(key, fields);
    }

    /**
     * 获取所有存储在哈希表中字段的值
     * <p>
     * hvals
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hVals(String key) {
        return getRedisTemplate().opsForHash().entries(key);
    }

    /**
     * 同时将多个field-value（字段-值）对设置到哈希表中
     * <p>
     * hmset
     *
     * @param key 键
     * @param map 键值对
     * @return
     */
    public static boolean hmSet(String key, Map<Object, Object> map) {
        if (Objects.isNull(key) || CollUtil.isEmpty(map)) {
            return false;
        }
        getRedisTemplate().opsForHash().putAll(key, map);
        return true;
    }

    /**
     * HashSet并设置时间
     *
     * <p>
     * hmset
     * expire
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间（秒）
     * @return
     */
    public static boolean hmSet(String key, Map<Object, Object> map, long time) {
        if (!hmSet(key, map)) {
            return false;
        }
        expire(key, time);
        return true;
    }

    /**
     * 将哈希表key中的字段field的值设为value
     * <p>
     * hset
     *
     * @param key   键
     * @param field 项
     * @param value 值
     * @return
     */
    public static boolean hSet(String key, Object field, Object value) {
        if (Objects.isNull(key) || Objects.isNull(field)) {
            return false;
        }
        getRedisTemplate().opsForHash().put(key, field, value);
        return true;
    }

    /**
     * 将哈希表key中的字段field的值设为value
     * 注意：如果已存在的hash表有时间，这里将会替换原有的时间
     * <p>
     * hset
     * expire
     *
     * @param key   键
     * @param field 项
     * @param value 值
     * @param time  时间（秒）
     * @return
     */
    public static boolean hSet(String key, Object field, Object value, long time) {
        if (!hSet(key, field, value)) {
            return false;
        }
        expire(key, time);
        return true;
    }

    /**
     * 删除hash表中的值
     * <p>
     * hdel
     *
     * @param key   键
     * @param field 项
     * @return
     */
    public static long hDel(String key, Object... field) {
        return getRedisTemplate().opsForHash().delete(key, field);
    }

    /**
     * 查看指定哈希表中指定的字段是否存在
     * <p>
     * hexists
     *
     * @param key   键
     * @param field 项
     * @return
     */
    public static boolean hExists(String key, Object field) {
        return getRedisTemplate().opsForHash().hasKey(key, field);
    }

    /**
     * 获取指定哈希表中字段的个数
     * <p>
     * hlen
     *
     * @param key
     * @return
     */
    public static Long hSize(String key) {
        return getRedisTemplate().opsForHash().size(key);
    }

    /**
     * 递增并返回值
     * 具体是递增还是递减取决于传入值的正负
     * <p>
     * hincrby
     *
     * @param key   键
     * @param field 项
     * @param delta 递增因子
     * @return
     */
    public static long hIncr(String key, String field, long delta) {
        return getRedisTemplate().opsForHash().increment(key, field, delta);
    }

    /**
     * 递增并返回值
     * 具体是递增还是递减取决于传入值的正负
     * <p>
     * hincrbyfloat
     *
     * @param key   键
     * @param field 项
     * @param delta 递增因子
     * @return
     */
    public static double hIncr(String key, String field, double delta) {
        return getRedisTemplate().opsForHash().increment(key, field, delta);
    }


    /**
     * 递减并返回值
     * 具体是递减还是递增取决于传入值的正负
     * <p>
     * hincrby
     *
     * @param key   键
     * @param field 项
     * @param delta 递减因子
     * @return
     */
    public static long hDecr(String key, String field, long delta) {
        return hIncr(key, field, -delta);
    }

    /**
     * 递减并返回值
     * 具体是递减还是递增取决于传入值的正负
     * <p>
     * hincrbyfloat
     *
     * @param key   键
     * @param field 项
     * @param delta 递减因子
     * @return
     */
    public static double hDecr(String key, String field, double delta) {
        return hIncr(key, field, -delta);
    }

    //============================set=============================

    /**
     * 获取指定集合中的所有值
     * <p>
     * smembers
     *
     * @param key 键
     * @return
     */
    public static Set<Object> sGet(String key) {
        return getRedisTemplate().opsForSet().members(key);
    }

    /**
     * 判断元素是否是集合的成员
     * <p>
     * sismember
     *
     * @param key    键
     * @param member 元素
     * @return
     */
    public static boolean sExists(String key, Object member) {
        return Boolean.TRUE.equals(getRedisTemplate().opsForSet().isMember(key, member));
    }

    /**
     * 将数据放入集合
     * <p>
     * sadd
     *
     * @param key    键
     * @param values 值
     * @return 成功个数
     */
    public static long sSet(String key, Object... values) {
        Long count = getRedisTemplate().opsForSet().add(key, values);
        return Objects.isNull(count) ? Long.MIN_VALUE : count;
    }

    /**
     * 将数据放入集合
     * 注意：本方法会覆盖原有ttl
     * <p>
     * sadd
     * expire
     *
     * @param key     键
     * @param timeout 时间（秒）
     * @param values  值
     * @return 成功个数
     */
    public static long sSet(String key, long timeout, Object... values) {
        long count = sSet(key, values);
        if (count > 0) {
            expire(key, timeout);
        }
        return count;
    }

    /**
     * 获取指定集合的元素个数
     * <p>
     * scard
     *
     * @param key 键
     * @return
     */
    public static long sSize(String key) {
        Long size = getRedisTemplate().opsForSet().size(key);
        return Objects.isNull(size) ? Long.MIN_VALUE : size;
    }

    /**
     * 移除集合指定元素
     * <p>
     * srem
     *
     * @param key    键
     * @param values 值
     * @return
     */
    public static long sDel(String key, Object... values) {
        Long count = getRedisTemplate().opsForSet().remove(key, values);
        return Objects.isNull(count) ? Long.MIN_VALUE : count;
    }

    /**
     * 返回给定所有集合的差集
     * <p>
     * sdiff
     *
     * @param keys
     * @return
     */
    public static Set<Object> sDiff(String... keys) {
        return getRedisTemplate().opsForSet().difference(Arrays.asList(keys));
    }

    /**
     * 返回给定所有集合的差集
     * <p>
     * sdiff
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public static Set<Object> sDiff(String key, String... otherKeys) {
        return getRedisTemplate().opsForSet().difference(key, Arrays.asList(otherKeys));
    }

    /**
     * 返回给定所有集合的交集
     * <p>
     * sinter
     *
     * @param keys
     * @return
     */
    public static Set<Object> sInter(String... keys) {
        return getRedisTemplate().opsForSet().intersect(Arrays.asList(keys));
    }

    /**
     * 返回给定所有集合的交集
     * <p>
     * sinter
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public static Set<Object> sInter(String key, String... otherKeys) {
        return getRedisTemplate().opsForSet().intersect(key, Arrays.asList(otherKeys));
    }

    /**
     * 返回所有给定集合的并集
     * <p>
     * sunion
     *
     * @param keys
     * @return
     */
    public static Set<Object> sUnion(String... keys) {
        return getRedisTemplate().opsForSet().union(Arrays.asList(keys));
    }

    /**
     * 返回所有给定集合的并集
     * <p>
     * sunion
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public static Set<Object> sUnion(String key, String... otherKeys) {
        return getRedisTemplate().opsForSet().union(key, Arrays.asList(otherKeys));
    }

    /**
     * 将member元素从source集合移动到destination集合
     * <p>
     * smove
     *
     * @param key
     * @param destKey
     * @param value
     * @return
     */
    public static boolean sMove(String key, String destKey, Object value) {
        return Boolean.TRUE.equals(getRedisTemplate().opsForSet().move(key, value, destKey));
    }

    /**
     * 移除并返回集合中的一个随机元素
     * <p>
     * spop
     *
     * @param key
     * @return
     */
    public static Object sPop(String key) {
        return getRedisTemplate().opsForSet().pop(key);
    }

    /**
     * 移除并返回集合中的指定个数的随机元素
     * <p>
     * srandmember
     *
     * @param key
     * @return
     */
    public static List<Object> sPop(String key, long size) {
        return getRedisTemplate().opsForSet().pop(key, size);
    }

    /* ************************************************ List ************************************************ */

    /**
     * 获取列表全部元素
     * 0 ~ -1：代表所有值
     * <p>
     * lrange
     *
     * @param key 键
     * @return
     */
    public static List<Object> lGet(String key) {
        return getRedisTemplate().opsForList().range(key, 0L, -1L);
    }

    /**
     * 获取列表指定范围内的元素
     * <p>
     * lrange
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return
     */
    public static List<Object> lGet(String key, long start, long end) {
        return getRedisTemplate().opsForList().range(key, start, end);
    }

    /**
     * 通过索引（index）获取列表中的值
     * <p>
     * index &gt;= 0时：0表头，1第二个元素，依次类推；
     * index &lt; 0时，-1表尾，-2倒数第二个元素，依次类推
     * <p>
     * lindex
     *
     * @param key   键
     * @param index 索引
     * @return
     */
    public static Object lGet(String key, long index) {
        return getRedisTemplate().opsForList().index(key, index);
    }

    /**
     * 获取列表的长度
     * <p>
     * llen
     *
     * @param key 键
     * @return
     */
    public static long lSize(String key) {
        Long size = getRedisTemplate().opsForList().size(key);
        return Objects.isNull(size) ? Long.MIN_VALUE : size;
    }

    /**
     * 将一个或多个值插入到列表的尾部（最右边）
     * <p>
     * rpush
     *
     * @param key    键
     * @param values 值
     * @return
     */
    public static long lRightPush(String key, Object... values) {
        Long count = getRedisTemplate().opsForList().rightPushAll(key, values);
        return Objects.isNull(count) ? Long.MIN_VALUE : count;
    }

    /**
     * 将一个或多个值插入到列表的尾部（最右边）
     * <p>
     * rpush
     * expire
     *
     * @param key     键
     * @param timeout 时间（秒）
     * @param values  值
     * @return
     */
    public static long lRightPush(String key, long timeout, Object... values) {
        long count = lRightPush(key, values);
        if (count > 0) {
            expire(key, timeout);
        }
        return count;
    }

    /**
     * 移出并获取列表的最后一个元素
     * <p>
     * rpop
     *
     * @param key 键
     * @return
     */
    public static Object lRightPop(String key) {
        return getRedisTemplate().opsForList().rightPop(key);
    }

    /**
     * 单位时间内移出并获取列表的最后一个元素
     * <p>
     * rpop
     *
     * @param key 键
     * @return
     */
    public static Object lRightPop(String key, long timeout) {
        return getRedisTemplate().opsForList().rightPop(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 单位时间内移出并获取列表的最后一个元素
     * <p>
     * rpop
     *
     * @param key 键
     * @return
     */
    public static Object lRightPop(String key, long timeout, TimeUnit unit) {
        return getRedisTemplate().opsForList().rightPop(key, timeout, unit);
    }

    /**
     * 将一个或多个值插入到列表头部（最左边）
     * <p>
     * lpush
     *
     * @param key    键
     * @param values 值
     * @return
     */
    public static long lLeftPush(String key, Object... values) {
        Long count = getRedisTemplate().opsForList().leftPushAll(key, values);
        return Objects.isNull(count) ? Long.MIN_VALUE : count;
    }

    /**
     * 将一个或多个值插入到列表头部（最左边）
     * <p>
     * lpush
     * expire
     *
     * @param key     键
     * @param timeout 时间（秒）
     * @param values  值
     * @return
     */
    public static long lLeftPush(String key, long timeout, Object... values) {
        long count = lLeftPush(key, values);
        if (count > 0) {
            expire(key, timeout);
        }
        return count;
    }

    /**
     * 移出并获取列表的第一个元素
     * <p>
     * lpop
     *
     * @param key 键
     * @return
     */
    public static Object lLeftPop(String key) {
        return getRedisTemplate().opsForList().leftPop(key);
    }

    /**
     * 单位时间内移出并获取列表的第一个元素
     * <p>
     * lpop
     *
     * @param key 键
     * @return
     */
    public static Object lLeftPop(String key, long timeout) {
        return getRedisTemplate().opsForList().leftPop(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 单位时间内移出并获取列表的第一个元素
     * <p>
     * lpop
     *
     * @param key 键
     * @return
     */
    public static Object lLeftPop(String key, long timeout, TimeUnit unit) {
        return getRedisTemplate().opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 通过索引设置列表元素的值
     * <p>
     * LSET
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public static boolean lSet(String key, long index, Object value) {
        getRedisTemplate().opsForList().set(key, index, value);
        return true;
    }

    /**
     * 移除列表元素
     * count &gt; 0：从表头开始向表尾搜索，移除与VALUE相等的元素，数量为COUNT。
     * count &lt; 0：从表尾开始向表头搜索，移除与VALUE相等的元素，数量为COUNT的绝对值。
     * count = 0：移除表中所有与VALUE相等的值。
     * <p>
     * LREM
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static long lDel(String key, long count, Object value) {
        Long removed = getRedisTemplate().opsForList().remove(key, count, value);
        return Objects.isNull(removed) ? Long.MIN_VALUE : removed;
    }

    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * 延迟初始化RedisTemplate，而不是使用static代码块
     */
    private static RedisTemplate<String, Object> getRedisTemplate() {
        if (Objects.isNull(redisTemplate)) {
            synchronized (RedisUtils.class) {
                if (Objects.isNull(redisTemplate)) {
                    redisTemplate = SpringContextUtils.getBean(new ParameterizedTypeReference<RedisTemplate<String, Object>>() {
                    });
                }
            }
        }
        return redisTemplate;
    }
}
