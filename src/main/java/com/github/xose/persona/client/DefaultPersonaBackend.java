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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DefaultPersonaBackend implements PersonaBackend {

	// Configuration
	private String verifyUrl = "/auth/verify";
	private String logoutUrl = "/auth/logout";
	private int requestTimeout = 0;
	
	public final void setVerifyUrl(String verifyUrl) {
		this.verifyUrl = checkNotNull(verifyUrl);
	}

	public final void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = checkNotNull(logoutUrl);
	}

	public void setRequestTimeout(int requestTimeout) {
		checkArgument(requestTimeout > 0);
		this.requestTimeout = requestTimeout;
	}
	
	@Override
	public void doLogin(final String assertion, final String audience, final AsyncCallback<String> callback) {
		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, verifyUrl);
		rb.setTimeoutMillis(requestTimeout);
		rb.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.FORM_DATA.toString());
		rb.setRequestData("assertion=" + URL.encodeQueryString(assertion) + "&audience=" + URL.encodeQueryString(audience));
		rb.setCallback(new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (Response.SC_OK != response.getStatusCode()) {
					callback.onFailure(new Exception("HTTP code " + response.getStatusCode()));
					return;
				}

				try {
					JSONObject jsonResponse = JSONParser.parseStrict(response.getText()).isObject();
					String jsonStatus = jsonResponse.get("status").isString().stringValue();
					if ("okay".equals(jsonStatus)) {
						String jsonEmail = jsonResponse.get("email").isString().stringValue();
						if (jsonResponse.containsKey("audience")) {
							String jsonAudience = jsonResponse.get("audience").isString().stringValue();
							if (!audience.equals(jsonAudience)) {
								callback.onFailure(new Exception("Audiences differ"));
								return;
							}
						}
						callback.onSuccess(jsonEmail);
					} else if ("failure".equals(jsonStatus)) {
						String reason = jsonResponse.get("reason").isString().stringValue();
						callback.onFailure(new Exception(reason));
					}
					else {
						callback.onFailure(new Exception("Invalid status"));
					}
				} catch (JSONException e) {
					callback.onFailure(e);
				} catch (Throwable e) {
					callback.onFailure(new JSONException("Error parsing JSON: " + e.getMessage(), e));
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				callback.onFailure(exception);
			}
		});
		
		try {
			rb.send();
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}

	@Override
	public void doLogout(final AsyncCallback<Void> callback) {
		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, logoutUrl);
		rb.setTimeoutMillis(requestTimeout);
		rb.setCallback(new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (Response.SC_OK != response.getStatusCode()) {
					callback.onFailure(new Exception("HTTP code " + response.getStatusCode()));
					return;
				}
				
				callback.onSuccess(null);
			}

			@Override
			public void onError(Request request, Throwable exception) {
				callback.onFailure(exception);
			}
		});
		
		try {
			rb.send();
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}
	
}
