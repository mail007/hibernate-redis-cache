package com.github.gerulrich.hibernate.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.github.gerulrich.hibernate.regions.CacheTransactionalDataRegion;

/**
 * Ultimate superclass for all Cache specific Hibernate AccessStrategy
 * implementations.
 * 
 * @param <T> type of the enclosed region
 * 
 * @author German Ulrich
 */
public abstract class AbstractCacheAccessStrategy<T extends CacheTransactionalDataRegion> {

    /**
     * The wrapped Hibernate cache region.
     */
    protected final T region;

    /**
     * The settings for this persistence unit.
     */
    protected final Settings settings;

    /**
     * Create an access strategy wrapping the given region.
     */
    public AbstractCacheAccessStrategy(T region, Settings settings) {
        this.region = region;
        this.settings = settings;
    }

    /**
     * This method is a placeholder for method signatures supplied by interfaces
     * pulled in further down the class hierarchy.
     * 
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#putFromLoad(java.lang.Object,
     *      java.lang.Object, long, java.lang.Object)
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#putFromLoad(java.lang.Object,
     *      java.lang.Object, long, java.lang.Object)
     */
    public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
        return this.putFromLoad(key, value, txTimestamp, version, this.settings.isMinimalPutsEnabled());
    }

    /**
     * This method is a placeholder for method signatures supplied by interfaces
     * pulled in further down the class hierarchy.
     * 
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#putFromLoad(java.lang.Object,
     *      java.lang.Object, long, java.lang.Object, boolean)
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#putFromLoad(java.lang.Object,
     *      java.lang.Object, long, java.lang.Object, boolean)
     */
    public abstract boolean putFromLoad(Object key, Object value, long txTimestamp, Object version,
        boolean minimalPutOverride) throws CacheException;

    /**
     * Region locks are not supported.
     * 
     * @return <code>null</code>
     * 
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#lockRegion()
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#lockRegion()
     */
    public final SoftLock lockRegion() {
        return null;
    }

    /**
     * Region locks are not supported - perform a cache clear as a precaution.
     * 
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#unlockRegion(org.hibernate.cache.access.SoftLock)
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#unlockRegion(org.hibernate.cache.access.SoftLock)
     */
    public final void unlockRegion(SoftLock lock) throws CacheException {
        this.region.clear();
    }

    /**
     * A no-op since this is an asynchronous cache access strategy.
     * 
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#remove(java.lang.Object)
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#remove(java.lang.Object)
     */
    public void remove(Object key) throws CacheException {
    }

    /**
     * Called to evict data from the entire region
     * 
     * @throws CacheException Propogated from underlying {@link org.hibernate.cache.Region}
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#removeAll()
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#removeAll()
     */
    public final void removeAll() throws CacheException {
        this.region.clear();
    }

    /**
     * Remove the given mapping without regard to transactional safety
     * 
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#evict(java.lang.Object)
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#evict(java.lang.Object)
     */
    public final void evict(Object key) throws CacheException {
        this.region.remove(key);
    }

    /**
     * Remove all mappings without regard to transactional safety
     * 
     * @see org.hibernate.cache.access.EntityRegionAccessStrategy#evictAll()
     * @see org.hibernate.cache.access.CollectionRegionAccessStrategy#evictAll()
     */
    public final void evictAll() throws CacheException {
        this.region.clear();
    }
}
