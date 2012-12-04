package be.infogroep.justpoker;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.vub.at.commlib.PlayerState;

import android.util.Log;

public class PokerPlayerMap<K, V> implements Map<K, V> {

	private Vector<K> keys;
	private int amount;
	private Vector<V> values;
	private HashMap<String, K> android_id_map;

	public PokerPlayerMap() {
		keys = new Vector<K>(9);
		values = new Vector<V>(9);
		android_id_map = new HashMap<String, K>();
		keys.setSize(9);
		values.setSize(9);
	}

	public void clear() {
		// TODO Auto-generated method stub

	}
	
	public void putAndroidID(String aid, K id) {
		android_id_map.put(aid, id);
	}
	
	public K getClientIDByAndroidID(String aid) {
		return android_id_map.get(aid);
	}

	public boolean containsKey(Object arg0) {
		return keys.contains(arg0);
	}
	
	
	public void logKeys(){
		Log.d("JustPoker - map", "Printing out the keys vector");
		Iterator<K> iter = keys.iterator();
		while(iter.hasNext()){
			Log.d("JustPoker - map", "key: "+iter.next());
		}
	}
	
	public void replaceKey(Object old, K n) {
		Log.d("JustPoker - replaceKey", "disctruction to keys");
		int index = keys.indexOf(old);
		if (index != -1){
			keys.setElementAt(n, index);
		}
	}

	public boolean containsValue(Object value) {
		return values.contains(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		if (keys.size() != values.size() && values.size() != amount)
			throw new IllegalStateException("InternalError: keys and values out of sync");
		ArrayList al = new ArrayList();
		for (int i = 0; i < keys.size(); i++) {
			if (values.get(i) != null)
				al.add(new CustomSortMapEntry(keys.get(i), values.get(i)));
		}
		return new CustomSortMapSet(al);
	}

	public V get(Object key) {
		int index = keys.indexOf(key);
		if (index == -1)
			return null;
		return values.get(index);
	}

	public boolean isEmpty() {
		if (keys.size() != values.size() && values.size() != amount)
			throw new IllegalStateException("InternalError: keys and values out of sync");
		return keys.size() == 0;
	}

	public Set<K> keySet() {
		Set<K> set = new HashSet<K>();
		Iterator<K> iter = keys.iterator();
		for (K k : keys)
		{
			if (k != null)
				set.add(k);
		}
		return set;
	}

	public V put(K key, V value) {
		Log.d("JustPoker - put", "disctruction to keys");
		int i = keys.indexOf(null);
		if (i == -1)
			return null;
 		keys.add(i,key);
		values.add(i,value);
		amount++;
		return value;
	}

	public void putAll(Map<? extends K, ? extends V> oldMap) {
		Iterator<? extends K> keysIter = oldMap.keySet().iterator();
		while (keysIter.hasNext()) {
			K k = keysIter.next();
			V v = oldMap.get(k);
			put(k, v);
		}
	}
	
	public V remove(Object key) {
		Log.d("JustPoker - remove", "disctruction to keys");
		int i = keys.indexOf(key);
		if (i == -1)
			return null;
		V old = values.get(i);
		keys.set(i, null);
		values.set(i, null);
		amount--;
		return old;
	}
	
	public Boolean move(K k, int pos) {
		Log.d("JustPoker - move", "disctruction to keys");
		int i = keys.indexOf(k);
		if (i == -1)
			return false;
		if (pos >= 0 && pos < keys.capacity()) {
			keys.set(pos, keys.get(i));
			values.set(pos, values.get(i));
			keys.set(i, null);
			values.set(i, null);
			return true;
		}
		return false;
	}

	public V getFirst() {
		V player = null;
		for (V p : values)
		{
			if (p != null) {
				player = p;
				break;
			}
		}
		return player;
	}
	
	public V nextFrom(K k){
		int start = keys.indexOf(k);
		int i = start+1;
		V p = null;
		Boolean keep_going = true;
		while(keep_going){
			if (i == keys.size())
				i = 0;
			p = values.get(i);
			if (p != null || i == start) 
				keep_going = false;
			i++;
		}
		return p;		
	}
	
	public int size() {
		return amount;
	}
	
	public int indexOfKey(K client_id) {
		return keys.indexOf(client_id);
	}

	public V nextUnfoldedFrom(K k) {
		int start = keys.indexOf(k);
		int i = start+1;
		V p = null;
		Boolean keep_going = true;
		while(keep_going){
			if (i == keys.size())
				i = 0;
			p = values.get(i);
			if (p != null) {
				if (((PokerPlayer) p).getState() != PlayerState.Fold) {
					keep_going = false;
				}
			}
			if (i == start)
				keep_going = false;
			i++;
		}
		return p;
	}

	public Collection<V> values() {
		ArrayList<V> l = new ArrayList<V>();
		for (V p : values)
		{
			if (p != null) {
				l.add(p);
			}
		}
		return l; 
	}

	

	final class CustomSortMapEntry<K, V> implements Map.Entry<K, V> {
		private final K key;
		private V value;

		public CustomSortMapEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			V old = this.value;
			this.value = value;
			return old;
		}

		public int compareTo(Object o2) {
			if (!(o2 instanceof CustomSortMapEntry))
				throw new IllegalArgumentException("Huh? Not a MapEntry?");
			Object otherKey = ((CustomSortMapEntry) o2).getKey();
			return ((Comparable) key).compareTo((Comparable) otherKey);
		}
	}

	private class CustomSortMapSet extends AbstractSet {
		List list;

		CustomSortMapSet(ArrayList al) {
			list = al;
		}

		public Iterator iterator() {
			return list.iterator();
		}

		public int size() {
			return list.size();
		}
	}	

}
