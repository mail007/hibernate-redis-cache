package com.github.gerulrich.redis.cache;

import org.hibernate.cache.CacheException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.github.gerulrich.cache.Cache;
import com.github.gerulrich.redis.cache.key.KeyGenerator;
import com.github.gerulrich.redis.cache.serializer.Serializer;

/**
 * Cache implementation based on redis. 
 * @author German Ulrich
 *
 */
public class RedisCacheImpl
    implements Cache {

    public static final String REDIS_CLEAR_INDEX_KEY = "hibernate:redis:clear:index";

    private JedisPool jedisPool;
    private String name;
    private KeyGenerator keyStrategy;
    private Serializer serializer;
    private int clearIndex;
    private int ttl;

    public RedisCacheImpl(String name, JedisPool jedisPool, KeyGenerator keyStrategy, Serializer serializer, int clearIndex,
        int ttl) {
        super();
        this.name = name;
        this.jedisPool = jedisPool;
        this.keyStrategy = keyStrategy;
        this.serializer = serializer;
        this.clearIndex = clearIndex;
        this.ttl = ttl;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object get(Object key) throws CacheException {
        Jedis jedis = this.jedisPool.getResource();
        try {
            String objectKey = this.keyStrategy.toKey(this.getName(), this.clearIndex, key);
            byte bytes[] = jedis.get(objectKey.getBytes());
            if (bytes == null) {
                return null;
            }
            return this.serializer.deserialize(bytes);
        } catch (Exception e) {
            this.jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CacheException("Error to get object from cache", e);
        } finally {
            if (jedis != null) {
                this.jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        Jedis jedis = this.jedisPool.getResource();
        try {
            String objectKey = this.keyStrategy.toKey(this.getName(), this.clearIndex, key);
            byte objectValue[] = this.serializer.serialize(value);
            jedis.setex(objectKey.getBytes(), this.ttl, objectValue);
        } catch (Exception e) {
            this.jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CacheException("Error to put object in cache", e);
        } finally {
            if (jedis != null) {
                this.jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public void remove(Object key) throws CacheException {
        Jedis jedis = this.jedisPool.getResource();
        try {
            String objectKey = this.keyStrategy.toKey(this.getName(), this.clearIndex, key);
            jedis.del(objectKey.getBytes());
        } catch (Exception e) {
            this.jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CacheException("Error to get object from cache", e);
        } finally {
            if (jedis != null) {
                this.jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public boolean isKeyInCache(Object key) {
        Jedis jedis = this.jedisPool.getResource();
        try {
            String objectKey = this.keyStrategy.toKey(this.getName(), this.clearIndex, key);
            return jedis.exists(objectKey);
        } catch (Exception e) {
            this.jedisPool.returnBrokenResource(jedis);
            jedis = null;
            return false;
        } finally {
            if (jedis != null) {
                this.jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public boolean removeAll() {
        this.clearIndex++;
        // increment clear index.
        // TODO Auto-generated method stub
        // return false;
        throw new UnsupportedOperationException("not yet implemented");
    }


}