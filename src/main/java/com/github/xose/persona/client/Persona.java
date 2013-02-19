/**
 * Copyright 2013 José Martínez
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.xose.persona.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * Mozilla Persona authentication.
 */
public final class Persona {

	/**
	 * Persona status.
	 */
	public static enum Status {
		/** The user is logged out. */
		LOGGEDOUT,
		/** The user is logged in. */
		LOGGEDIN,
		/** A login assertion has been received, but not yet validated. */
		LOGGINGIN,
		/** The user has started the logout procedure. */
		LOGGINGOUT,
		/** Persona has not been initialized yet. */
		UNKNOWN;
	}

	private static final EventBus eventBus = new SimpleEventBus();

	private static Status status = Status.UNKNOWN;
	private static String currentUser;

	// Configuration
	private static PersonaBackend backend = new NullPersonaBackend();
	private static String siteName;
	private static String siteLogo;
	private static String privacyPolicy;
	private static String termsOfService;
	private static String returnTo;

	private Persona() {
	}

	/**
	 * Set the application's authorization backend.
	 * 
	 * @param backend
	 *            the authentication backend
	 * 
	 * @see PersonaBackend
	 */
	public static void setBackend(PersonaBackend backend) {
		Persona.backend = checkNotNull(backend);
	}

	/**
	 * Set the application's name to be shown on the login screen.
	 * <p>
	 * Must be in plain text, no markup.
	 * 
	 * @param siteName
	 *            the site name
	 */
	public static void setSiteName(String siteName) {
		Persona.siteName = siteName;
	}

	/**
	 * Sets the application's logo for the login screen.
	 * <p>
	 * Must be an absolute URL and available over SSL. The image will be scaled
	 * down to 100x100px.
	 * 
	 * @param siteLogo
	 *            the site logo
	 */
	public static void setSiteLogo(String siteLogo) {
		Persona.siteLogo = siteLogo;
	}

	/**
	 * Sets the application's privacy policy absolute URL.
	 * <p>
	 * If set, a terms of service URL must be provided too.
	 * 
	 * @param privacyPolicy
	 *            the site's privacy policy
	 */
	public static void setPrivacyPolicy(String privacyPolicy) {
		Persona.privacyPolicy = privacyPolicy;
	}

	/**
	 * Sets the application's terms of service absolute URL.
	 * <p>
	 * If set, a privacy policy URL must be provided too.
	 * 
	 * @param termsOfService
	 *            the site's terms of service
	 */
	public static void setTermsOfService(String termsOfService) {
		Persona.termsOfService = termsOfService;
	}

	/**
	 * Initializes Persona with an unknown user.
	 * <p>
	 * Call this method if the current user identity is not known. Persona will
	 * perform the login procedure if a user is currently logged in, or a logout
	 * otherwise.
	 * 
	 * @see <a href="https://developer.mozilla.org/en-US/docs/DOM/navigator.id.watch">navigator.id.watch</a>
	 */
	public static final native void watch() /*-{
		@com.github.xose.persona.client.Persona::watch1(*)({});
	}-*/;

	/**
	 * Initializes Persona with a known user.
	 * <p>
	 * Call this method if the application has a way to know if there is a user
	 * logged in or not.
	 * 
	 * @param loggedInUser
	 *            the expected logged in user, null if no user is expected
	 * 
	 * @see <a href="https://developer.mozilla.org/en-US/docs/DOM/navigator.id.watch">navigator.id.watch</a>
	 * @see <a href="https://developer.mozilla.org/en-US/docs/DOM/navigator.id.watch#Parameters">loggedInUser</a>
	 */
	public static final void watch(String loggedInUser) {
		Persona.currentUser = loggedInUser;
		setStatus(loggedInUser != null ? Status.LOGGEDIN : Status.LOGGEDOUT, null, false);
		watch0(loggedInUser);
	}

	private static final native void watch0(String loggedInUser) /*-{
		@com.github.xose.persona.client.Persona::watch1(*)({loggedInUser: loggedInUser});
	}-*/;

	private static final native void watch1(JavaScriptObject opts) /*-{
		opts.onlogin = $entry(function(assertion) {
			@com.github.xose.persona.client.Persona::onLogin(*)(assertion);
		});
		opts.onlogout = $entry(function() {
			@com.github.xose.persona.client.Persona::onLogout()();
		});
		$wnd.navigator.id.watch(opts);
	}-*/;

	/**
	 * Starts a new login procedure.
	 * 
	 * @see <a href="https://developer.mozilla.org/en-US/docs/DOM/navigator.id.request">navigator.id.request</a>
	 */
	public static final native void request() /*-{
		var opts = {};
		opts.oncancel = $entry(function() {
			@com.github.xose.persona.client.Persona::onCancel()();
		});
		if (@com.github.xose.persona.client.Persona::siteName != null)
			opts.siteName = @com.github.xose.persona.client.Persona::siteName;
		if (@com.github.xose.persona.client.Persona::siteLogo != null)
			opts.siteLogo = @com.github.xose.persona.client.Persona::siteLogo;
		if (@com.github.xose.persona.client.Persona::privacyPolicy != null)
			opts.privacyPolicy = @com.github.xose.persona.client.Persona::privacyPolicy;
		if (@com.github.xose.persona.client.Persona::termsOfService != null)
			opts.termsOfService = @com.github.xose.persona.client.Persona::termsOfService;
		if (@com.github.xose.persona.client.Persona::returnTo != null)
			opts.returnTo = @com.github.xose.persona.client.Persona::returnTo;
		$wnd.navigator.id.request(opts);
	}-*/;

	/**
	 * Starts the logout procedure.
	 * 
	 * @see <a href="https://developer.mozilla.org/en-US/docs/DOM/navigator.id.logout">navigator.id.logout</a>
	 */
	public static final native void logout() /*-{
		$wnd.navigator.id.logout();
	}-*/;

	/**
	 * Returns the logged in user, if any.
	 * 
	 * @return the logged in user, or null if no user is logged in
	 */
	@Nullable
	public static final String getCurrentUser() {
		return currentUser;
	}

	protected static final void setStatus(Status status, Event<?> event, boolean clearUser) {
		Persona.status = status;
		if (clearUser)
			currentUser = null;
		if (event != null)
			eventBus.fireEvent(event);
	}

	/**
	 * Returns the current login status.
	 * 
	 * @return the current login status
	 */
	public static final Status getStatus() {
		return status;
	}

	/**
	 * Adds a logged in handler.
	 * 
	 * @param handler
	 *            the logged in handler to be added
	 * @return an object to deregister the handler
	 */
	public static final HandlerRegistration addAuthLoggedInHandler(AuthLoggedInHandler handler) {
		return AuthLoggedInEvent.register(eventBus, handler);
	}

	/**
	 * Adds a logged out handler.
	 * 
	 * @param handler
	 *            the logged out handler to be added
	 * @return an object to deregister the handler
	 */
	public static final HandlerRegistration addAuthLoggedOutHandler(AuthLoggedOutHandler handler) {
		return AuthLoggedOutEvent.register(eventBus, handler);
	}

	/**
	 * Adds a logging in handler.
	 * 
	 * @param handler
	 *            the logging in handler to be added
	 * @return an object to deregister the handler
	 */
	public static final HandlerRegistration addAuthLoggingInHandler(AuthLoggingInHandler handler) {
		return AuthLoggingInEvent.register(eventBus, handler);
	}

	/**
	 * Adds a logging out handler.
	 * 
	 * @param handler
	 *            the logging out handler to be added
	 * @return an object to deregister the handler
	 */
	public static final HandlerRegistration addAuthLoggingOutHandler(AuthLoggingOutHandler handler) {
		return AuthLoggingOutEvent.register(eventBus, handler);
	}

	/**
	 * Adds a cancelled handler.
	 * 
	 * @param handler
	 *            the cancelled handler to be added
	 * @return an object to deregister the handler
	 */
	public static final HandlerRegistration addAuthCancelledHandler(AuthCancelledHandler handler) {
		return AuthCancelledEvent.register(eventBus, handler);
	}

	/**
	 * Adds an error handler.
	 * 
	 * @param handler
	 *            the error handler to be added
	 * @return an object to deregister the handler
	 */
	public static final HandlerRegistration addAuthErrorHandler(AuthErrorHandler handler) {
		return AuthErrorEvent.register(eventBus, handler);
	}

	/**
	 * Adds an authentication handler.
	 * 
	 * @param handler
	 *            the authentication handler to be added
	 * @return an object to deregister the handler
	 */
	public static final HandlerRegistration addHandler(AuthHandler handler) {
		final Set<HandlerRegistration> handlers = Sets.newHashSet();
		handlers.add(addAuthLoggedInHandler(handler));
		handlers.add(addAuthLoggedOutHandler(handler));
		handlers.add(addAuthLoggingInHandler(handler));
		handlers.add(addAuthLoggingOutHandler(handler));
		handlers.add(addAuthCancelledHandler(handler));
		handlers.add(addAuthErrorHandler(handler));

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				for (HandlerRegistration h : handlers) {
					h.removeHandler();
				}
			}
		};
	}

	private static final void onLogin(String assertion) {
		setStatus(Status.LOGGINGIN, new AuthLoggingInEvent(), true);

		backend.doLogin(assertion, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				currentUser = checkNotNull(result);
				setStatus(Status.LOGGEDIN, new AuthLoggedInEvent(result), false);
			}

			@Override
			public void onFailure(Throwable caught) {
				setStatus(Status.LOGGEDOUT, new AuthErrorEvent(caught.getMessage()), true);
			}
		});
	}

	private static final void onLogout() {
		setStatus(Status.LOGGINGOUT, new AuthLoggingOutEvent(), false);

		backend.doLogout(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				setStatus(Status.LOGGEDOUT, new AuthLoggedOutEvent(), true);
			}

			@Override
			public void onFailure(Throwable caught) {
				setStatus(Status.LOGGEDIN, new AuthErrorEvent(caught.getMessage()), false);
			}
		});
	}

	private static final void onCancel() {
		setStatus(status, new AuthCancelledEvent(), false);
	}

}
