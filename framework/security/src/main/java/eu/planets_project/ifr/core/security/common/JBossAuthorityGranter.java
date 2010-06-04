/**
 * 
 */
package eu.planets_project.ifr.core.security.common;

import java.security.Principal;
import java.util.Set;

import org.acegisecurity.providers.jaas.AuthorityGranter;

/**
 * @author AnJackson
 *
 */
public class JBossAuthorityGranter implements AuthorityGranter {

    /* (non-Javadoc)
     * @see org.acegisecurity.providers.jaas.AuthorityGranter#grant(java.security.Principal)
     */
    @SuppressWarnings("unchecked")
	public Set grant(Principal principal) {
        java.util.Set<String> auths = new java.util.HashSet<String>();
        auths.add(principal.getName());
        return auths;
    }

}
