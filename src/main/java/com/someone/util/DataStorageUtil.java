package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/26 18:57
 */

import android.util.SparseArray;
import java.util.HashMap;
import java.util.Objects;

public class DataStorageUtil {

    public static class MultiValueMap<K, V> {
        private K currentKey;
        private final HashMap<K, Data<V>> keyMap;
        private Data<V> currentData;

        public MultiValueMap() {
            keyMap = new HashMap<>();
        }

        public MultiValueMap<K, V> setKey(K k) {
            currentKey = k;
            if (keyMap.containsKey(k)) {
                currentData = keyMap.get(k);
            } else {
                currentData = new Data<>();
            }
            return this;
        }

        public void put(int page, V v) {
            if (Objects.isNull(currentKey) || Objects.isNull(currentData)) {
                return;
            }
            currentData.put(page, v);
            keyMap.put(currentKey, currentData);
        }

        public void put(V... vArray) {
            if (Objects.isNull(currentKey) || Objects.isNull(currentData)) {
                return;
            }
            for (V v : vArray) {
                currentData.put(v);
            }
            keyMap.put(currentKey, currentData);
        }

        public Data<V> getData() {
            if (Objects.isNull(currentKey) || Objects.isNull(currentData)) {
                return null;
            }
            return currentData;
        }

        public static class Data<V> {
            private SparseArray<V> valueArray;
            private int page = 0;

            public Data() {
                valueArray = new SparseArray<>();
            }

            public int getPageSize() {
                return page;
            }

            public void put(V v) {
                put(page, v);
            }

            public void put(int page, V v) {
                valueArray.put(page, v);
                this.page++;
            }

            public V get(int page) {
                return valueArray.get(page);
            }
        }
    }
}

