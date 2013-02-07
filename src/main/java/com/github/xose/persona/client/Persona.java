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
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public final class Persona {

	public static enum Status {
		LOGGEDOUT, VERIFYING, LOGGEDIN, LOGGINGOUT, CANCELLED, ERROR, UNKNOWN;
	}

	private static EventBus eventBus;

	private static Status status = Status.UNKNOWN;
	private static String currentUser;

	private static PersonaBackend service = new DefaultPersonaBackend();
	private static String audience = getDefaultAudience();
	private static String siteName;
	private static String siteLogo;
	private static String privacyPolicy;
	private static String termsOfService;
	private static String returnTo;

	private Persona() {
	}

	public static void setService(PersonaBackend service) {
		Persona.service = checkNotNull(service);
	}

	public static void setAudience(String audience) {
		Persona.audience = Strings.isNullOrEmpty(audience) ? getDefaultAudience() : audience;
	}

	public static void setSiteName(String siteName) {
		Persona.siteName = siteName;
	}

	public static void setSiteLogo(String siteLogo) {
		Persona.siteLogo = siteLogo;
	}

	public static void setPrivacyPolicy(String privacyPolicy) {
		Persona.privacyPolicy = privacyPolicy;
	}

	public static void setTermsOfService(String termsOfService) {
		Persona.termsOfService = termsOfService;
	}

	public static final void init(EventBus eventBus) {
		checkState(Persona.eventBus == null);
		Persona.eventBus = checkNotNull(eventBus);
		watch0();
	}

	public static final void init(EventBus eventBus, String loggedInUser) {
		checkState(Persona.eventBus == null);
		Persona.eventBus = checkNotNull(eventBus);
		watch0(loggedInUser);
	}

	public static final void login() {
		checkState(Persona.eventBus != null);
		login0();
	}

	public static final void logout() {
		checkState(Persona.eventBus != null);
		logout0();
	}

	public static final Optional<String> getCurrentUser() {
		return Optional.fromNullable(currentUser);
	}

	protected static final void setStatus(Status status) {
		Persona.status = status;

		if (status != Status.LOGGEDIN) {
			currentUser = null;
		}
	}

	public static final Status getStatus() {
		return status;
	}

	public static final HandlerRegistration addAuthSuccessHandler(AuthSuccessHandler handler) {
		return AuthSuccessEvent.register(eventBus, handler);
	}

	public static final HandlerRegistration addAuthFailureHandler(AuthFailureHandler handler) {
		return AuthFailureEvent.register(eventBus, handler);
	}

	public static final HandlerRegistration addAuthLoggedOutHandler(AuthLoggedOutHandler handler) {
		return AuthLoggedOutEvent.register(eventBus, handler);
	}

	public static final HandlerRegistration addAuthVerifyingHandler(AuthVerifyingHandler handler) {
		return AuthVerifyingEvent.register(eventBus, handler);
	}

	public static final HandlerRegistration addAuthLoggingOutHandler(AuthLoggingOutHandler handler) {
		return AuthLoggingOutEvent.register(eventBus, handler);
	}

	public static final HandlerRegistration addAuthCancelledHandler(AuthCancelledHandler handler) {
		return AuthCancelledEvent.register(eventBus, handler);
	}

	public static final HandlerRegistration addHandler(AuthHandler handler) {
		final Set<HandlerRegistration> handlers = Sets.newHashSet();
		handlers.add(addAuthSuccessHandler(handler));
		handlers.add(addAuthFailureHandler(handler));
		handlers.add(addAuthLoggedOutHandler(handler));
		handlers.add(addAuthVerifyingHandler(handler));
		handlers.add(addAuthLoggingOutHandler(handler));
		handlers.add(addAuthCancelledHandler(handler));

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				for (HandlerRegistration h : handlers) {
					h.removeHandler();
				}
			}
		};
	}

	private static final native void watch0() /*-{
		@com.github.xose.persona.client.Persona::watch1(*)({});
	}-*/;

	private static final native void watch0(String loggedInUser) /*-{
		var opts = {};
		opts.loggedInUser = loggedInUser;
		@com.github.xose.persona.client.Persona::watch1(*)(opts);
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

	private static final native void login0() /*-{
		var opts = {};
		opts.oncancel = $entry(function() {
			@com.github.xose.persona.client.Persona::onCancel()();
		});
		if (@com.github.xose.persona.client.Persona::siteName != null)
			opts.siteName = @com.github.xose.persona.client.Persona::siteName;
		if (@com.github.xose.persona.client.Persona::siteLogo != null)
			opts.siteName = @com.github.xose.persona.client.Persona::siteLogo;
		if (@com.github.xose.persona.client.Persona::privacyPolicy != null)
			opts.siteName = @com.github.xose.persona.client.Persona::privacyPolicy;
		if (@com.github.xose.persona.client.Persona::termsOfService != null)
			opts.siteName = @com.github.xose.persona.client.Persona::termsOfService;
		if (@com.github.xose.persona.client.Persona::returnTo != null)
			opts.siteName = @com.github.xose.persona.client.Persona::returnTo;
		$wnd.navigator.id.request(opts);
	}-*/;

	private static final native void logout0() /*-{
		$wnd.navigator.id.logout();
	}-*/;

	private static final void onLogin(String assertion) {
		setStatus(Status.VERIFYING);
		eventBus.fireEvent(new AuthVerifyingEvent());

		service.doLogin(assertion, audience, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				currentUser = result;
				setStatus(Status.LOGGEDIN);
				eventBus.fireEvent(new AuthSuccessEvent(result));
			}

			@Override
			public void onFailure(Throwable caught) {
				error(caught.getMessage());
			}
		});
	}

	private static final void onLogout() {
		setStatus(Status.LOGGINGOUT);
		eventBus.fireEvent(new AuthLoggingOutEvent());

		service.doLogout(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				setStatus(Status.LOGGEDOUT);
				eventBus.fireEvent(new AuthLoggedOutEvent());
			}

			@Override
			public void onFailure(Throwable caught) {
				error("Error logging out: " + caught.getMessage());
			}
		});
	}

	private static final void onCancel() {
		setStatus(Status.CANCELLED);
		eventBus.fireEvent(new AuthCancelledEvent());
	}

	private static final String getDefaultAudience() {
		UrlBuilder builder = new UrlBuilder();
		builder.setProtocol(Location.getProtocol());
		builder.setHost(Location.getHost());
		builder.setPort(Integer.parseInt(Location.getPort()));
		return builder.buildString();
	}

	protected static final void error(String message) {
		setStatus(Status.ERROR);
		eventBus.fireEvent(new AuthFailureEvent(message));
	}

}
