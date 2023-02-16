package com.sandbox.settlement.common.resolver;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**--------------------------------------------------------------------
 * ■Request 파라미터(JSON Style)를 변환하여 전달할 Value Object ■sangheon
 --------------------------------------------------------------------**/
public class RequestParameterMap<K, V> {
    private Map<K, V> map;

    /**--------------------------------------------------------------------
     * ■해당 Key의 Value가 존재하는지 리턴 ■sangheon
     --------------------------------------------------------------------**/
    public boolean containsKey(K objKey) {
        return map.containsKey(objKey);
    }

    /**--------------------------------------------------------------------
     * ■해당 Object가 존재하는지 리턴 ■sangheon
     --------------------------------------------------------------------**/
    public boolean containsValue(V objValue) {
        return map.containsValue(objValue);
    }

    /**--------------------------------------------------------------------
     * ■해당 Key의 Value를 리턴 ■sangheon
     --------------------------------------------------------------------**/
    public V get(K objKey) {
        return map.get(objKey);
    }

    /**--------------------------------------------------------------------
     * ■Key와 Value로 Map에 저장 ■sangheon
     --------------------------------------------------------------------**/
    public V put(K objKey, V objValue) {
        return map.put(objKey, objValue);
    }

    /**--------------------------------------------------------------------
     * ■Map이 비었는지 리턴 ■sangheon
     --------------------------------------------------------------------**/
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**--------------------------------------------------------------------
     * ■Map의 key Set을 리턴함 ■sangheon
     --------------------------------------------------------------------**/
    public Set<K> keySet() {
        return map.keySet();
    }

    /**--------------------------------------------------------------------
     * ■해당 Map의 mapping들을 Map에 복사함 ■sangheon
     --------------------------------------------------------------------**/
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    /**--------------------------------------------------------------------
     * ■Map에서 Key로서 대상 제거 ■sangheon
     --------------------------------------------------------------------**/
    public V remove(K objKey) {
        return map.remove(objKey);
    }

    /**--------------------------------------------------------------------
     * ■MMap의 사이즈 리턴 ■sangheon
     --------------------------------------------------------------------**/
    public int size() {
        return map.size();
    }

    /**--------------------------------------------------------------------
     * ■Map을 Set으로 리턴함 ■sangheon
     --------------------------------------------------------------------**/
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /**--------------------------------------------------------------------
     * ■멤버 변수 map의 Setter ■sangheon
     --------------------------------------------------------------------**/
    public void setMap(Map<K, V> map) {
        this.map = map;
    }

    @Override
    public String toString(){
        return map.toString();
    }
}
