// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ScopeDataResolver;

/**
 * Abstract base class for HTTP scopes injection.
 */
public abstract class BaseScopeInjector {

	protected final ScopeDataResolver scopeDataResolver;
	protected final ScopeType scopeType;

	/**
	 * Creates scope injector for provided {@link jodd.madvoc.ScopeType}.
	 */
	protected BaseScopeInjector(ScopeType scopeType, ScopeDataResolver scopeDataResolver) {
		this.scopeType = scopeType;
		this.scopeDataResolver = scopeDataResolver;
	}

	// ---------------------------------------------------------------- beanutil

	/**
	 * Sets target bean property, optionally creates instance if doesn't exist.
	 */
	protected void setTargetProperty(Object target, String name, Object attrValue, boolean create) {
		if (create == true) {
			BeanUtil.setDeclaredPropertyForcedSilent(target, name, attrValue);
		} else {
			BeanUtil.setDeclaredPropertySilent(target, name, attrValue);
		}
	}

	/**
	 * Reads target property.
	 */
	protected Object getTargetProperty(Object target, ScopeData.Out out) {
		if (out.target == null) {
			return BeanUtil.getDeclaredProperty(target, out.name);
		} else {
			return BeanUtil.getDeclaredProperty(target, out.target);
		}
	}

	// ---------------------------------------------------------------- matched property

	/**
	 * Returns matched property name or <code>null</code> if name is not matched.
	 * <p>
	 * Matches if attribute name matches the required field name. If the match is positive,
	 * injection or outjection is performed on the field.
	 * <p>
	 * Parameter name matches field name if param name starts with field name and has
	 * either '.' or '[' after the field name.
	 * <p>
	 * Returns real property name, once when name is matched.
	 */
	protected String getMatchedPropertyName(ScopeData.In in, String attrName) {
		// match
		if (attrName.startsWith(in.name) == false) {
			return null;
		}
		int requiredLen = in.name.length();
		if (attrName.length() >= requiredLen + 1) {
			char c = attrName.charAt(requiredLen);
			if ((c != '.') && (c != '[')) {
				return null;
			}
		}

		// get param
		if (in.target == null) {
			return attrName;
		}
		return in.target + attrName.substring(in.name.length());
	}


	// ---------------------------------------------------------------- delegates

	/**
	 * Delegates to {@link jodd.madvoc.component.ScopeDataResolver#lookupInData(Class, jodd.madvoc.ScopeType)}.
	 */
	public ScopeData.In[] lookupInData(Class type) {
		return scopeDataResolver.lookupInData(type, scopeType);
	}

	/**
	 * Lookups scope data for many targets.
	 */
	public ScopeData.In[][] lookupInData(Object[] targets) {
		ScopeData.In[][] scopes = new ScopeData.In[targets.length][];

		boolean allNulls = true;
		for (int i = 0; i < targets.length; i++) {
			Object target = targets[i];
			scopes[i] = scopeDataResolver.lookupInData(target.getClass(), scopeType);
			if (scopes[i] != null) {
				allNulls = false;
			}
		}

		if (allNulls) {
			return null;
		}
		return scopes;
	}

	/**
	 * Delegates to {@link jodd.madvoc.component.ScopeDataResolver#lookupOutData(Class, jodd.madvoc.ScopeType)}.
	 */
	public ScopeData.Out[] lookupOutData(Class type) {
		return scopeDataResolver.lookupOutData(type, scopeType);
	}

	/**
	 * Lookups scope data for many targets.
	 */
	public ScopeData.Out[][] lookupOutData(Object[] targets) {
		ScopeData.Out[][] scopes = new ScopeData.Out[targets.length][];

		boolean allNulls = true;
		for (int i = 0; i < targets.length; i++) {
			Object target = targets[i];
			scopes[i] = scopeDataResolver.lookupOutData(target.getClass(), scopeType);
			if (scopes[i] != null) {
				allNulls = false;
			}
		}

		if (allNulls) {
			return null;
		}
		return scopes;
	}


}