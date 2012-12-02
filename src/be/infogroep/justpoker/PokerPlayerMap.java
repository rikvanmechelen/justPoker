package be.infogroep.justpoker;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import android.util.Log;

public class PokerPlayerMap<K, PokerPlayer> implements Map<K, PokerPlayer> {

	private Vector<K> keys;
	private int amount;
	private Vector<PokerPlayer> values;

	public PokerPlayerMap() {
		keys = new Vector<K>(9);
		values = new Vector<PokerPlayer>(9);
		keys.setSize(9);
		values.setSize(9);
	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	public boolean containsKey(Object arg0) {
		return keys.contains(arg0);
	}

	public boolean containsValue(Object value) {
		return values.contains(value);
	}

	public Set<java.util.Map.Entry<K, PokerPlayer>> entrySet() {
		if (keys.size() != values.size() && values.size() != amount)
			throw new IllegalStateException("InternalError: keys and values out of sync");
		ArrayList al = new ArrayList();
		for (int i = 0; i < keys.size(); i++) {
			if (values.get(i) != null)
				al.add(new CustomSortMapEntry(keys.get(i), values.get(i)));
		}
		return new CustomSortMapSet(al);
	}

	public PokerPlayer get(Object key) {
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

	public PokerPlayer put(K key, PokerPlayer value) {
		int i = keys.indexOf(null);
		if (i == -1)
			return null;
 		keys.add(i,key);
		values.add(i,value);
		amount++;
		return value;
	}

	public void putAll(Map<? extends K, ? extends PokerPlayer> oldMap) {
		Iterator<? extends K> keysIter = oldMap.keySet().iterator();
		while (keysIter.hasNext()) {
			K k = keysIter.next();
			PokerPlayer v = oldMap.get(k);
			put(k, v);
		}
	}
	
	public PokerPlayer remove(Object key) {
		int i = keys.indexOf(key);
		if (i == -1)
			return null;
		PokerPlayer old = values.get(i);
		keys.set(i, null);
		values.set(i, null);
		amount--;
		return old;
	}
	
	public Boolean move(K k, int pos) {
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

	public PokerPlayer getFirst() {
		PokerPlayer player = null;
		for (PokerPlayer p : values)
		{
			if (p != null) {
				player = p;
				break;
			}
		}
		return player;
	}
	
	public PokerPlayer nextFrom(K k){
		int start = keys.indexOf(k);
		int i = start+1;
		PokerPlayer p = null;
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

	public Collection<PokerPlayer> values() {
		ArrayList<PokerPlayer> l = new ArrayList<PokerPlayer>();
		for (PokerPlayer p : values)
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

	public int indexOfKey(int client_id) {
		return keys.indexOf(client_id);
	}

}
