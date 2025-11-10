package io.swiftcache.core.support.struct.lru.impl;

import io.swiftcache.api.CacheEntry;
import io.swiftcache.core.exception.CacheRuntimeException;
import io.swiftcache.core.model.DefaultCacheEntry;
import io.swiftcache.core.model.CircleListNode;
import io.swiftcache.core.support.struct.lru.LruMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于双向列表的实现
 * @author binbin.hou
 * @since 0.0.13
 */
public class LruMapDoubleList<K,V> implements LruMap<K,V> {

    private static final Logger log = LoggerFactory.getLogger(LruMapDoubleList.class);

    /**
     * 头结点
     * @since 0.0.13
     */
    private CircleListNode<K,V> head;

    /**
     * 尾巴结点
     * @since 0.0.13
     */
    private CircleListNode<K,V> tail;

    /**
     * map 信息
     *
     * key: 元素信息
     * value: 元素在 list 中对应的节点信息
     * @since 0.0.13
     */
    private Map<K, CircleListNode<K,V>> indexMap;

    public LruMapDoubleList() {
        this.indexMap = new HashMap<>();
        this.head = new CircleListNode<>();
        this.tail = new CircleListNode<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }

    @Override
    public CacheEntry<K, V> removeEldest() {
        // 获取尾巴节点的前一个元素
        CircleListNode<K,V> tailPre = this.tail.pre();
        if(tailPre == this.head) {
            log.error("当前列表为空，无法进行删除");
            throw new CacheRuntimeException("不可删除头结点!");
        }

        K evictKey = tailPre.key();
        V evictValue = tailPre.value();

        // 执行删除
        this.removeKey(evictKey);

        return DefaultCacheEntry.of(evictKey, evictValue);
    }

    /**
     * 放入元素
     *
     * （1）删除已经存在的
     * （2）新元素放到元素头部
     *
     * @param key 元素
     * @since 0.0.13
     */
    @Override
    public void updateKey(final K key) {
        //1. 执行删除
        this.removeKey(key);

        //2. 新元素插入到头部
        //head<->next
        //变成：head<->new<->next
        CircleListNode<K,V> newNode = new CircleListNode<>();
        newNode.key(key);

        CircleListNode<K,V> next = this.head.next();
        this.head.next(newNode);
        newNode.pre(this.head);
        next.pre(newNode);
        newNode.next(next);

        //2.2 插入到 map 中
        indexMap.put(key, newNode);
    }

    /**
     * 移除元素
     *
     * 1. 获取 map 中的元素
     * 2. 不存在直接返回，存在执行以下步骤：
     * 2.1 删除双向链表中的元素
     * 2.2 删除 map 中的元素
     *
     * @param key 元素
     * @since 0.0.13
     */
    @Override
    public void removeKey(final K key) {
        CircleListNode<K,V> node = indexMap.get(key);

        if(node == null) {
            return;
        }

        // 删除 list node
        // A<->B<->C
        // 删除 B，需要变成： A<->C
        CircleListNode<K,V> pre = node.pre();
        CircleListNode<K,V> next = node.next();

        pre.next(next);
        next.pre(pre);

        // 删除 map 中对应信息
        this.indexMap.remove(key);
        log.debug("从 LruMapDoubleList 中移除 key: {}", key);
    }

    @Override
    public boolean isEmpty() {
        return indexMap.isEmpty();
    }

    @Override
    public boolean contains(K key) {
        return indexMap.containsKey(key);
    }

}
