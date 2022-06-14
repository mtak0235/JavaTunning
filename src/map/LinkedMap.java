package map;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class LinkedMap<K, V> {
	private static final int DEFAULT_CAPACITY = 101;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private LinkedEntry<K, V> table[];
	private LinkedEntry<K, V> header;
	private int count;
	private int threshold;
	private float loadFactor;

	public LinkedMap(int initCapacity, float loadFactor) {
		if (initCapacity < 0)
			throw new RuntimeException("Capacity Error: " + initCapacity);
		if (loadFactor <= 0)
			throw new RuntimeException("Load Count Error: " + loadFactor);
		if (initCapacity == 0)
			initCapacity = 1;
		this.loadFactor = loadFactor;
		this.table = new LinkedEntry[initCapacity];
		this.header = new LinkedEntry(null, null, null);
		this.header.link_next = header.link_prev = header;
		threshold = (int) (initCapacity * loadFactor);
	}

	public LinkedMap() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	public int size() {
		return count;
	}

	public synchronized Enumeration<K> keys() {
		return new Enumer<K>(TYPE.KEYS);
	}

	public synchronized Enumeration<V> values() {
		return new Enumer<V>(TYPE.VALUES);
	}

	public synchronized Enumeration<LinkedEntry<K, V>> entries() {
		return new Enumer<LinkedEntry<K,V>>(TYPE.ENTRIES);
	}

	public synchronized Enumeration<K> keysR() {
		return new EnumerR<K>(TYPE.KEYS);
	}

	public synchronized Enumeration<V> valuesR() {
		return new EnumerR(TYPE.VALUES);
	}

	public synchronized Enumeration<LinkedEntry<K, V>> entriesR() {
		return new EnumerR<LinkedEntry<K, V>>(TYPE.ENTRIES);
	}

	public synchronized boolean containsValue(V value) {
		if (value == null) {
			return false;
		}
		LinkedEntry<K, V> tab[] = table;
		int i = tab.length;
		while (i-- > 0) {
			for (LinkedEntry<K, V> e = tab[i]; e != null; e = e.next) {
				if (equals(e.value, value)) {
					return true;
				}
			}
		}
		return false;
	}

	public synchronized boolean containsKey(K key) {
		if (key == null)
			return false;
		LinkedEntry<K, V> tab[] = table;
		int index = hash(key) % tab.length;
		for (LinkedEntry<K, V> e = tab[index]; e != null; e = e.next) {
			if (equals(e.key, key)) {
				return true;
			}
		}
		return false;
	}

	public synchronized V get(K key) {
		if (key == null)
			return null;
		LinkedEntry<K, V> tab[] = table;
		int index = hash(key) % tab.length;
		for (LinkedEntry<K, V> e = tab[index]; e != null; e = e.next) {
			if (equals(e.key, key)) {
				return e.value;
			}
		}
		return null;
	}

	public synchronized V getLRU(K key) {
		LinkedEntry<K, V> tab[] = table;
		int index = hash(key) % tab.length;
		for (LinkedEntry<K, V> e = tab[index]; e != null; e = e.next) {
			if (equals(e.key, key)) {
				V old = e.value;
				if (header.link_prev != e) {
					unchain(e);
					chain(header.link_prev, header, e);
				}
				return old;
			}
		}
		return null;
	}

	public synchronized LinkedEntry<K, V> getFirst() {
		if (isEmpty())
			return null;
		return this.header.link_next;
	}

	public synchronized K getFirstKey() {
		if (isEmpty())
			return null;
		return this.header.link_next.key;
	}

	public synchronized K getLastKey() {
		if (isEmpty())
			return null;
		return this.header.link_prev.key;
	}

	public synchronized V getFirstValue() {
		if (isEmpty())
			return null;
		return this.header.link_next.value;
	}

	public synchronized V getLastValue() {
		if (isEmpty())
			return null;
		return this.header.link_prev.value;
	}

	private int hash(Object key) {
		return (int) (key.hashCode()) & Integer.MAX_VALUE;
	}

	protected void rehash() {
		int oldCapacity = table.length;
		LinkedEntry<K, V> oldMap[] = table;
		int newCapacity = oldCapacity * 2 + 1;
		LinkedEntry<K, V> newMap[] = new LinkedEntry[newCapacity];
		threshold = (int) (newCapacity * loadFactor);
		table = newMap;
		for (int i = oldCapacity; i-- > 0;) {
			for (LinkedEntry<K, V> old = oldMap[i]; old != null;) {
				LinkedEntry<K, V> e = old;
				old = old.next;
				K key = e.key;
				int index = hash(key) % newCapacity;
				e.next = newMap[index];
				newMap[index] = e;
			}
		}
	}

	private int max;

	public LinkedMap<K, V> setMax(int max) {
		this.max = max;
		return this;
	}

	public boolean isFull() {
		return max > 0 && max <= count;
	}

	private static enum MODE {
		FORCE_FIRST, FORCE_LAST, FIRST, LAST
	};

	public V put(K key, V value) {
		return _put(key, value, MODE.LAST);
	}

	public V putLast(K key, V value) {
		return _put(key, value, MODE.FORCE_LAST);
	}

	public V putFirst(K key, V value) {
		return _put(key, value, MODE.FORCE_FIRST);
	}

	private synchronized V _put(K key, V value, MODE m) {
		LinkedEntry<K, V> tab[] = table;
		int index = hash(key) % tab.length;
		for (LinkedEntry<K, V> e = tab[index]; e != null; e = e.next) {
			if (equals(e.key, key)) {
				V old = e.value;
				e.value = value;
				switch (m) {
				case FORCE_FIRST:
					if (header.link_next != e) {
						unchain(e);
						chain(header, header.link_next, e);
					}
					break;
				case FORCE_LAST:
					if (header.link_prev != e) {
						unchain(e);
						chain(header.link_prev, header, e);
					}
					break;
				}
				return old;
			}
		}
		if (max > 0) {
			switch (m) {
			case FORCE_FIRST:
			case FIRST:
				while (count >= max) {
					K k = header.link_prev.key;
					V v = remove(k);
					overflowed(k, v);
				}
				break;
			case FORCE_LAST:
			case LAST:
				while (count >= max) {
					K k = header.link_next.key;
					V v = remove(k);
					overflowed(k, v);
				}
				break;
			}
		}
		if (count >= threshold) {
			rehash();
			tab = table;
			index = hash(key) % tab.length;
		}
		LinkedEntry<K, V> e = new LinkedEntry<K,V>(key, value, tab[index]);
		tab[index] = e;
		switch (m) {
		case FORCE_FIRST:
		case FIRST:
			chain(header, header.link_next, e);
			break;
		case FORCE_LAST:
		case LAST:
			chain(header.link_prev, header, e);
			break;
		}
		count++;
		return null;
	}

	protected void overflowed(K key, V value) {
	}

	protected V create(K key) {
		throw new RuntimeException("not implemented create()");
	}

	public V intern(K key) {
		return _intern(key, MODE.LAST);
	}

	private synchronized V _intern(K key, MODE m) {
		LinkedEntry<K, V> tab[] = table;
		int index = hash(key) % tab.length;
		for (LinkedEntry<K, V> e = tab[index]; e != null; e = e.next) {
			if (equals(e.key, key)) {
				V old = e.value;
				switch (m) {
				case FORCE_FIRST:
					if (header.link_next != e) {
						unchain(e);
						chain(header, header.link_next, e);
					}
					break;
				case FORCE_LAST:
					if (header.link_prev != e) {
						unchain(e);
						chain(header.link_prev, header, e);
					}
					break;
				}
				return old;
			}
		}

		V value = create(key);
		if (value == null)
			return null;

		if (max > 0) {
			switch (m) {
			case FORCE_FIRST:
			case FIRST:
				while (count >= max) {
					K k = header.link_prev.key;
					V v = remove(k);
					overflowed(k, v);
				}
				break;
			case FORCE_LAST:
			case LAST:
				while (count >= max) {
					K k = header.link_next.key;
					V v = remove(k);
					overflowed(k, v);
				}
				break;
			}
		}
		if (count >= threshold) {
			rehash();
			tab = table;
			index = hash(key) % tab.length;
		}
		LinkedEntry<K, V> e = new LinkedEntry<K, V>(key, value, tab[index]);
		tab[index] = e;
		switch (m) {
		case FORCE_FIRST:
		case FIRST:
			chain(header, header.link_next, e);
			break;
		case FORCE_LAST:
		case LAST:
			chain(header.link_prev, header, e);
			break;
		}
		count++;
		return value;
	}

	public synchronized V remove(K key) {
		if (key == null)
			return null;
		LinkedEntry<K, V> tab[] = table;
		int index = hash(key) % tab.length;
		for (LinkedEntry<K, V> e = tab[index], prev = null; e != null; prev = e, e = e.next) {
			if (equals(e.key, key)) {
				if (prev != null) {
					prev.next = e.next;
				} else {
					tab[index] = e.next;
				}
				count--;
				V oldValue = e.value;
				e.value = null;
				//
				unchain(e);
				return oldValue;
			}
		}
		return null;
	}

	public synchronized V removeFirst() {
		if (isEmpty())
			return null;
		return remove(header.link_next.key);
	}

	public synchronized V removeLast() {
		if (isEmpty())
			return null;
		return remove(header.link_prev.key);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public synchronized void clear() {
		LinkedEntry<K, V> tab[] = table;
		for (int index = tab.length; --index >= 0;)
			tab[index] = null;
		this.header.link_next = header.link_prev = header;
		count = 0;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		Enumeration<LinkedEntry<K, V>> it = entries();
		buf.append("{");
		for (int i = 0; it.hasMoreElements(); i++) {
			LinkedEntry<K, V> e = (LinkedEntry<K, V>) (it.nextElement());
			if (i > 0)
				buf.append(", ");
			buf.append(e.getKey() + "=" + e.getValue());
		}
		buf.append("}");
		return buf.toString();
	}

	public String toKeyString() {
		StringBuffer buf = new StringBuffer();
		Enumeration<K> it = keys();
		buf.append("{");
		for (int i = 0; it.hasMoreElements(); i++) {
			K key = it.nextElement();
			if (i > 0)
				buf.append(", ");
			buf.append(key);
		}
		buf.append("}");
		return buf.toString();
	}

	public String toFormatString() {
		StringBuffer buf = new StringBuffer();
		Enumeration<LinkedEntry<K, V>> it = entries();
		buf.append("{\n");
		while (it.hasMoreElements()) {
			LinkedEntry<K, V> e =  it.nextElement();
			buf.append("\t").append(e.getKey() + "=" + e.getValue()).append("\n");
		}
		buf.append("}");
		return buf.toString();
	}

	private enum TYPE {
		KEYS, VALUES, ENTRIES
	}

	private class Enumer<X> implements Enumeration<X> {
		TYPE type;
		LinkedEntry<K, V> entry = (LinkedEntry<K, V>) LinkedMap.this.header.link_next;
		LinkedEntry<K, V> lastEnt;

		Enumer(TYPE type) {
			this.type = type;
		}

		public boolean hasMoreElements() {
			return header != entry && entry != null;
		}

		public X nextElement() {
			if (hasMoreElements()) {
				LinkedEntry<K, V> e = lastEnt = entry;
				entry = e.link_next;
				switch (type) {
				case KEYS:
					return (X) e.key;
				case VALUES:
					return (X) e.value;
				default:
					return (X) e;
				}
			}
			throw new NoSuchElementException("no more next");
		}
	}

	private class EnumerR<X> implements Enumeration<X> {
		TYPE type;
		LinkedEntry<K, V> entry = LinkedMap.this.header.link_prev;
		LinkedEntry<K, V> lastEnt;

		EnumerR(TYPE type) {
			this.type = type;
		}

		public boolean hasMoreElements() {
			return header != entry && entry != null;
		}

		public X nextElement() {
			if (hasMoreElements()) {
				LinkedEntry<K, V> e = lastEnt = entry;
				entry = e.link_prev;
				switch (type) {
				case KEYS:
					return (X) e.key;
				case VALUES:
					return (X) e.value;
				default:
					return (X) e;
				}
			}
			throw new NoSuchElementException("no more next");
		}
	}

	private void chain(LinkedEntry<K, V> link_prev, LinkedEntry<K, V> link_next, LinkedEntry<K, V> e) {
		e.link_prev = link_prev;
		e.link_next = link_next;
		link_prev.link_next = e;
		link_next.link_prev = e;
	}

	private void unchain(LinkedEntry<K, V> e) {
		e.link_prev.link_next = e.link_next;
		e.link_next.link_prev = e.link_prev;
		e.link_prev = null;
		e.link_next = null;
	}

	public static class LinkedEntry<X, Y> {
		X key;
		Y value;
		LinkedEntry<X, Y> next;
		LinkedEntry<X, Y> link_next, link_prev;

		protected LinkedEntry(X key, Y value, LinkedEntry<X, Y> next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		protected Object clone() {
			return new LinkedEntry<X, Y>(key, value, (next == null ? null : (LinkedEntry) next.clone()));
		}

		public X getKey() {
			return key;
		}

		public Y getValue() {
			return value;
		}

		public Y setValue(Y value) {
			Y oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		public boolean equals(Object o) {
			if (!(o instanceof LinkedEntry))
				return false;
			LinkedEntry<X, Y> e = (LinkedEntry<X,Y>) o;
			return LinkedMap.equals(key, e.key) && LinkedMap.equals(value, e.value);
		}

		public int hashCode() {
			return key.hashCode() ^ (value == null ? 0 : value.hashCode());
		}

		public String toString() {
			return key + "=" + value;
		}

		public LinkedEntry<X, Y> next() {
			return this.next;
		}
	}

	public static boolean equals(Object v1, Object v2) {
		if (v1 == v2)
			return true;
		return (v1 == null) ? false : v1.equals(v2);
	}

	public synchronized void sort(Comparator<LinkedEntry<K, V>> c) {
		Object[] o = new Object[this.size()];
		Enumeration<LinkedEntry<K, V>> en = this.entries();
		for (int i = 0; en.hasMoreElements(); i++) {
			o[i] = en.nextElement();
		}
		this.clear();

		Arrays.sort(o, (Comparator) c);

		for (int i = 0; i < o.length; i++) {
			LinkedEntry<K, V> e = (LinkedEntry<K, V>) o[i];
			this.put(e.getKey(), e.getValue());
		}
	}

	public void putAll(LinkedMap<K, V> other) {
		if (other == null)
			return;
		Enumeration<LinkedEntry<K, V>> it = other.entries();
		while (it.hasMoreElements()) {
			LinkedEntry<K, V> e = it.nextElement();
			this.put(e.getKey(), e.getValue());
		}
	}

	public LinkedMap<K, V> trim(int max) {
		if (max < 0) {
			return this;
		}
		if (max < this.size()) {
			while (this.size() > max) {
				this.removeLast();
			}
		}
		return this;
	}
}