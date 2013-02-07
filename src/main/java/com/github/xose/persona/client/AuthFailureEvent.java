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

import com.google.common.base.Preconditions;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

class AuthFailureEvent extends Event<AuthFailureHandler> {

	private static final Type<AuthFailureHandler> TYPE = new Type<AuthFailureHandler>();

	static final HandlerRegistration register(EventBus eventBus, AuthFailureHandler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	@Override
	public Type<AuthFailureHandler> getAssociatedType() {
		return TYPE;
	}

	private final String message;

	public AuthFailureEvent(String message) {
		this.message = Preconditions.checkNotNull(message);
	}

	@Override
	protected void dispatch(AuthFailureHandler handler) {
		handler.onAuthFailure(message);
	}

}
