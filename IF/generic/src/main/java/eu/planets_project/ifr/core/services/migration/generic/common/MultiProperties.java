package eu.planets_project.ifr.core.services.migration.generic.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A map of maps to support multiple properties.
 */
public class MultiProperties implements Map<String, Map<String, String>>
{
	/**
	 * @param p a list of properties
	 * @return a MultiProperties instance
	 */
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

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#clear()
	 */
	public void clear()
	{
		this.props.clear();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key_)
	{
		return this.props.containsKey(key_);
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value_)
	{
		return this.props.containsValue(value_);
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<String, Map<String, String>>> entrySet()
	{
		return this.props.entrySet();
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o_)
	{
		return this.props.equals(o_);
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Map<String, String> get(Object key_)
	{
		return this.props.get(key_);
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.props.hashCode();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty()
	{
		return props.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet()
	{
		return this.props.keySet();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Map<String, String> put(String key_, Map<String, String> value_)
	{
		return this.props.put(key_, value_);
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends Map<String, String>> t_)
	{
		this.props.putAll(t_);
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Map<String, String> remove(Object key_)
	{
		return this.props.remove(key_);
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#size()
	 */
	public int size()
	{
		return this.props.size();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Map#values()
	 */
	public Collection<Map<String, String>> values()
	{
		return this.props.values();
	}

	private Map<String, Map<String, String>> props = new HashMap<String, Map<String, String>>();
}
