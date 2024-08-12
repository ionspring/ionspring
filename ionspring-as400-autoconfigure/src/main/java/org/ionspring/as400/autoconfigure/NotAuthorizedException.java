/*
 * Copyright 2024 Damien Ferrand
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

package org.ionspring.as400.autoconfigure;


import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when the IBM i user/password matches but the authentication was denied by {@link AS400AuthenticationProvider#isAuthorized(String)}.
 */
public class NotAuthorizedException extends AuthenticationException {
    public NotAuthorizedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NotAuthorizedException(String msg) {
        super(msg);
    }
}
