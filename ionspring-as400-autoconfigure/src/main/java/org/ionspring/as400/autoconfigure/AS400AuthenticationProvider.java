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

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AS400AuthenticationProvider implements AuthenticationProvider {
    private final AS400 as400;

    public AS400AuthenticationProvider(AS400 as400) {
        this.as400 = as400;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            as400.validateSignon(authentication.getPrincipal().toString(), authentication.getCredentials().toString().toCharArray());
        } catch (AS400SecurityException e) {
            throw new BadCredentialsException("Invalid user/password");
        } catch (IOException e) {
            throw new AuthenticationServiceException("Authentication error", e);
        }
        if (!isAuthorized(authentication.getPrincipal().toString())) {
            throw new NotAuthorizedException("User " + authentication.getPrincipal().toString() + " not authorized.");
        }
        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), getGrantedAuthorities(authentication.getPrincipal().toString()));
    }

    public boolean isAuthorized(String username) {
        return true;
    }

    public List<GrantedAuthority> getGrantedAuthorities(String username) {
        return new ArrayList<>();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
