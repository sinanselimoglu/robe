package io.robe.auth;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.dropwizard.auth.Authenticator;

import javax.inject.Inject;

/**
 * Injectable provider for {@link com.yammer.dropwizard.auth.Auth} annotation.
 * {@inheritDoc}
 *
 * @param <T> Type of the injectable parameter.
 */
public class AuthProvider<T extends Credentials> implements InjectableProvider<Auth, Parameter> {

    private Authenticator authenticator;

	/**
	 * Creates a new AuthProvider with the given {@link Authenticator}
	 * @param authenticator  Desired Authenticator to provide.
	 */
    @Inject
    public AuthProvider(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	/**
	 * {@inheritDoc}
	 * @return
	 */
	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}


	/**
	 * {@inheritDoc}
	 * @param context context of the component
	 * @param auth annotation
	 * @param parameter Injectable parameter
	 * @return Returns an instance of {@link io.robe.auth.AuthInjectable}.
	 */
	@Override
	public Injectable getInjectable(ComponentContext context, Auth auth, Parameter parameter) {
		return new AuthInjectable<T>(authenticator);
	}
}
