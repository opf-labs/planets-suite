package eu.planets_project.ifr.core.services.migration.generic.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MultiProperties implements Map<String, Map<String, String>>
{
	public static MultiProperties load(Properties p)
	{
		MultiProperties mp = new MultiProperties();
		for(Object key : p.keySet())
		{
			String s = (String)key;
			String[] parts = s.split("\\.", 3);
			if(mp.get(parts[1]) == null)
			{
				mp.put(parts[1], new HashMap<String, String>());
			}
			mp.get(parts[1]).put(parts[2], (String)p.get(key));
		}
		return mp;
	}

	public void clear()
	{
		props.clear();
	}

	public boolean containsKey(Object key_)
	{
		return props.containsKey(key_);
	}

	public boolean containsValue(Object value_)
	{
		return props.containsValue(value_);
	}

	public Set<Entry<String, Map<String, String>>> entrySet()
	{
		return props.entrySet();
	}

	public boolean equals(Object o_)
	{
		return props.equals(o_);
	}

	public Map<String, String> get(Object key_)
	{
		return props.get(key_);
	}

	public int hashCode()
	{
		return props.hashCode();
	}

	public boolean isEmpty()
	{
		return props.isEmpty();
	}

	public Set<String> keySet()
	{
		return props.keySet();
	}

	public Map<String, String> put(String key_, Map<String, String> value_)
	{
		return props.put(key_, value_);
	}

	public void putAll(Map<? extends String, ? extends Map<String, String>> t_)
	{
		props.putAll(t_);
	}

	public Map<String, String> remove(Object key_)
	{
		return props.remove(key_);
	}

	public int size()
	{
		return props.size();
	}

	public Collection<Map<String, String>> values()
	{
		return props.values();
	}

	private Map<String, Map<String, String>> props = new HashMap<String, Map<String, String>>();
}
