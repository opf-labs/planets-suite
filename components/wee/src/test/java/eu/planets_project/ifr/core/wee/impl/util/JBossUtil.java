package eu.planets_project.ifr.core.wee.impl.util; 

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JBossUtil {
	
	private static InitialContext ctx;
	
	private JBossUtil() {
    }
	
	
	private static InitialContext getContext() {
        //We only keep one context around, so lazily initialize it
        if (ctx == null) {
            try {
                ctx = new InitialContext();
            } catch (NamingException e) {
                throw new RuntimeException("Unable to get initial context", e);
            }
        }
 
        return ctx;
    }


	/**
     * The lookup method on InitialContext returns Object. This simple wrapper
     * asks first for the expected type and the for the name to find. It gets
     * the name out of JNDI and performs a simple type-check. It then casts to
     * the type provided as the first parameter.
     * 
     * This isn't strictly correct since the cast uses the expression (T), where
     * T is the generic parameter and the type is erased at run-time. However,
     * since we first perform a type check, we know this cast is safe. The -at-
     * SuppressWarnings lets the Java Compiler know that we think we know what
     * we are doing.
     * 
     * @param <T> Type type provided as the first parameter
     * @param clazz The type to cast to upon return
     * @param name The name to find in Jndi, e.g. XxxDao/local or, XxxDao/Remote
     * @return Something out of Jndi cast to the type provided as the first
     *         parameter.
     */
    @SuppressWarnings("unchecked")
    public static <T> T lookup(Class<T> clazz, String name) {
        final InitialContext ctx = getContext();
        /**
         * Perform the lookup, verify that it is type-compatible with clazz and
         * cast the return type (using the erased type because that's all we
         * have) so the client does not need to perform the cast.
         */
        try {
            final Object object = ctx.lookup(name);
            if (clazz.isAssignableFrom(object.getClass())) {
                return (T) object;
            } else {
                throw new RuntimeException(String.format(
                        "Class found: %s cannot be assigned to type: %s",
                        object.getClass(), clazz));
            }
 
        } catch (NamingException e) {
            throw new RuntimeException(String.format(
                    "Unable to find ejb for %s", clazz.getName()), e);
        }
    }


}
