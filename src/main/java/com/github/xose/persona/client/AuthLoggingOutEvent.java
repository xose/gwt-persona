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

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

class AuthLoggingOutEvent extends Event<AuthLoggingOutHandler> {

	private static final Type<AuthLoggingOutHandler> TYPE = new Type<AuthLoggingOutHandler>();

	static final HandlerRegistration register(EventBus eventBus, AuthLoggingOutHandler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	@Override
	public Type<AuthLoggingOutHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AuthLoggingOutHandler handler) {
		handler.onAuthLoggingOut();
	}

}
