package com.micropace.ramp.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropace.ramp.base.constant.GlobalConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * redis 操作常用方法
 *
 * @author Suffrajet
 */
public class RedisHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper mapper = new ObjectMapper();

    private RedisHelper() {}

    public RedisHelper(RedisManager manager) {
        this.redisManager = manager;
    }

    private RedisManager redisManager;

    /**
     * 获取键名下存储的字符串值
     *
     * @param key 键名
     * @return 字符串值
     */
    public String getStr(String key) {
        String v = null;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                v = jedis.get(key);
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return v;
    }

    /**
     * 存储字符串
     *
     * @param key   键名
     * @param value 值
     * @return true 成功
     */
    public boolean setStr(String key, String value) {
        boolean b = false;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                String rp = jedis.set(key, value);
                b = rp.toUpperCase().equals(GlobalConst.REDIS_OK);
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return b;
    }

    /**
     * 存储字符串，并设置过期时间
     *
     * @param key    键名
     * @param value  值
     * @param expire 过期时间，单位秒
     * @return true 成功
     */
    public boolean setExStr(String key, String value, int expire) {
        boolean b = false;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                String rp = jedis.setex(key, expire, value);
                b = rp.toUpperCase().equals(GlobalConst.REDIS_OK);
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return b;
    }

    /**
     * 获取存储的对象
     *
     * @param key 键名
     * @param clz 对象的具体类型
     * @param <T> 对象泛型类型
     * @return 对象
     */
    public <T> T getObj(String key, Class<T> clz) {
        T v = null;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                String json = jedis.get(key);
                if (json != null) {
                    v = mapper.readValue(json, clz);
                }
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return v;
    }

    /**
     * 存储对象
     *
     * @param key 键名
     * @param obj 对象
     * @return true 成功
     */
    public boolean setObj(String key, Object obj) {
        boolean b = false;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                String json = mapper.writeValueAsString(obj);
                if (json != null) {
                    String rp = jedis.set(key, json);
                    b = rp.toUpperCase().equals(GlobalConst.REDIS_OK);
                }
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return b;
    }

    /**
     * 存储对象，并设置过期时间
     *
     * @param key    键名
     * @param obj    对象
     * @param expire 过期时间，单位秒
     * @return true 成功
     */
    public boolean setExObj(String key, Object obj, int expire) {
        boolean b = false;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                String json = mapper.writeValueAsString(obj);
                if (json != null) {
                    String rp = jedis.setex(key, expire, json);
                    b = rp.toUpperCase().equals(GlobalConst.REDIS_OK);
                }
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return b;
    }

    /**
     * 向List末尾添加字符串元素
     *
     * @param key   键名，数据类型为List
     * @param value 值
     * @return true 成功
     */
    public boolean pushStr2List(String key, String value) {
        boolean b = false;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                jedis.rpush(key, value);
                b = true;
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return b;
    }

    /**
     * 向List末尾添加对象元素
     *
     * @param key 键名
     * @param obj 对象
     * @return true 成功
     */
    public boolean pushObj2List(String key, Object obj) {
        boolean b = false;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                String json = mapper.writeValueAsString(obj);
                if (json != null) {
                    jedis.rpush(key, json);
                    b = true;
                }
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return b;
    }

    /**
     * 从List头部取出一个字符串元素
     *
     * @param key 键名
     * @return 值
     */
    public String popStrFromList(String key) {
        String v = null;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                v = jedis.lpop(key);
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return v;
    }

    /**
     * 从List头部取出一个对象元素
     *
     * @param key 键名
     * @param clz 对象的具体类型
     * @param <T> 对象泛型类型
     * @return 对象
     */
    public <T> T popObjFromList(String key, Class<T> clz) {
        T v = null;
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                String json = jedis.lpop(key);
                if (json != null) {
                    v = mapper.readValue(json, clz);
                }
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return v;
    }

    /**
     * 从List头部开始取出多个字符串元素
     * 如果指定的个数多于List已有元素个数，则全部取出
     *
     * @param key  键名
     * @param size 指定取出元素的个数
     * @return 字符串元素列表
     */
    public List<String> popMultiStrFromList(String key, Long size) {
        List<String> list = new ArrayList<>();
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                Long llen = jedis.llen(key);
                Long batchSize = llen > size ? size : llen;
                for (int i = 0; i < batchSize.intValue(); i++) {
                    String json = jedis.lpop(key);
                    if (json != null) {
                        list.add(json);
                    }
                }
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return list;
    }

    /**
     * 从List头部开始取出多个对象元素
     * 如果指定的个数多于List已有元素个数，则全部取出
     *
     * @param key  键名
     * @param clz  对象的具体类型
     * @param size 指定取出元素的个数
     * @param <T>  对象泛型类型
     * @return 对象元素列表
     */
    public <T> List<T> popMultiObjFromList(String key, Class<T> clz, Long size) {
        List<T> list = new ArrayList<>();
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                Long llen = jedis.llen(key);
                Long batchSize = llen > size ? size : llen;
                for (int i = 0; i < batchSize.intValue(); i++) {
                    String json = jedis.lpop(key);
                    if (json != null) {
                        T t = mapper.readValue(json, clz);
                        if (t != null) {
                            list.add(t);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
        return list;
    }

    /**
     * 删除指定键
     *
     * @param key 键名
     */
    public void deleteKey(String key) {
        Jedis jedis = this.redisManager.getJedis();
        if (jedis != null && !jedis.getClient().isBroken()) {
            try {
                jedis.del(key);
            } catch (Exception e) {
                logger.error(key, e.getMessage());
            } finally {
                jedis.close();
            }
        }
    }

}
