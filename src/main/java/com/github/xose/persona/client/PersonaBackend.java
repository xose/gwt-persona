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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Represents a site-specific backend that will process Persona assertions.
 * <p>
 * An implementation of this interface must connect to a server backend to
 * verify assertions and set up a session for the user.
 * </p>
 * This can be implemented, for example, with a redirect, XHR request, RPC call,
 * WebSocket message, ...
 */
public interface PersonaBackend {

	/**
	 * Called when an assertion is returned by Persona.
	 * <p>
	 * The backend must verify the assertion and call the appropriate callback:
	 * <ul>
	 * <li> {@link AsyncCallback#onSuccess} Call with the user email when the
	 * assertion has been correctly verified and the user is logged in.
	 * <li> {@link AsyncCallback#onFailure} Call when the assertion is invalid or
	 * an error occurs.
	 * </ul>
	 * 
	 * @param assertion
	 *            the assertion that must be verified
	 * @param callback
	 *            the callback function
	 */
	void doLogin(String assertion, AsyncCallback<String> callback);

	/**
	 * Called when the user requests to log out of Persona.
	 * <p>
	 * The backend must close the session and call the appropriate callback:
	 * <ul>
	 * <li> {@link AsyncCallback#onSuccess} Call with a null parameter when the
	 * user has been successfully logged out.
	 * <li> {@link AsyncCallback#onFailure} Call when an error occurs.
	 * </ul>
	 * 
	 * @param callback
	 *            the callback function
	 */
	void doLogout(AsyncCallback<Void> callback);

}
