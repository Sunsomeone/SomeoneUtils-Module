package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/26 18:57
 */

import android.util.SparseArray;
import java.util.HashMap;

public class DataStorageUtils {

    public static class MultiValueMap<K, V> {
        private K currentKey;
        private final HashMap<K, Data<V>> keyMap;
        private Data<V> currentData;

        public MultiValueMap() {
            keyMap = new HashMap<>();
        }

        public MultiValueMap<K, V> setKey(K k) {
            currentKey = k;
            currentData = keyMap.containsKey(k) ? keyMap.get(k) : new Data<V>();
            return this;
        }

        public void put(int page, V v) {
            if (currentKey != null && currentData != null) {
                currentData.put(page, v);
                keyMap.put(currentKey, currentData);
            }
        }

        @SafeVarargs
        public final void put(V... vArray) {
            if (currentKey != null && currentData != null) {
                for (V v : vArray) {
                    currentData.put(v);
                }
                keyMap.put(currentKey, currentData);
            }
        }

        public Data<V> getData() {
            return currentKey == null || currentData == null ? null : currentData;
        }

        public static class Data<V> {
            private final SparseArray<V> valueArray;
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

