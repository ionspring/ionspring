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

import com.ibm.as400.access.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AS400AuthenticationProvider implements AuthenticationProvider {
    private final AS400 as400;

    Logger logger = LoggerFactory.getLogger(AS400AuthenticationProvider.class);

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
        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(),
                getGrantedAuthorities(authentication.getPrincipal().toString(),
                        getSpecialAuthorities(authentication.getPrincipal().toString(),
                                authentication.getCredentials().toString().toCharArray())));
    }

    public boolean isAuthorized(String username) {
        return true;
    }

    public List<GrantedAuthority> getSpecialAuthorities(String username, char[] password) {
        List<GrantedAuthority> retVal = new ArrayList<>();
        AS400 userAs400;
        if (as400 instanceof SecureAS400) {
            userAs400 = new SecureAS400(as400.getSystemName(), username, password);
        } else {
            userAs400 = new AS400(as400.getSystemName(), username, password);
        }
        try (userAs400) {
            User user = new User(userAs400, username);
            if (user.hasSpecialAuthority(User.SPECIAL_AUTHORITY_AUDIT)) {
                retVal.add(new SimpleGrantedAuthority("ROLE_SPECIAL_AUTHORITY_AUDIT"));
            }
            if (user.hasSpecialAuthority(User.SPECIAL_AUTHORITY_SERVICE)) {
                retVal.add(new SimpleGrantedAuthority("ROLE_SPECIAL_AUTHORITY_SERVICE"));
            }
            if (user.hasSpecialAuthority(User.SPECIAL_AUTHORITY_ALL_OBJECT)) {
                retVal.add(new SimpleGrantedAuthority("ROLE_SPECIAL_AUTHORITY_ALL_OBJECT"));
            }
            if (user.hasSpecialAuthority(User.SPECIAL_AUTHORITY_IO_SYSTEM_CONFIGURATION)) {
                retVal.add(new SimpleGrantedAuthority("ROLE_SPECIAL_AUTHORITY_IO_SYSTEM_CONFIGURATION"));
            }
            if (user.hasSpecialAuthority(User.SPECIAL_AUTHORITY_JOB_CONTROL)) {
                retVal.add(new SimpleGrantedAuthority("ROLE_SPECIAL_AUTHORITY_JOB_CONTROL"));
            }
            if (user.hasSpecialAuthority(User.SPECIAL_AUTHORITY_SAVE_SYSTEM)) {
                retVal.add(new SimpleGrantedAuthority("ROLE_SPECIAL_AUTHORITY_SAVE_SYSTEM"));
            }
            if (user.hasSpecialAuthority(User.SPECIAL_AUTHORITY_SECURITY_ADMINISTRATOR)) {
                retVal.add(new SimpleGrantedAuthority("ROLE_SPECIAL_AUTHORITY_SECURITY_ADMINISTRATOR"));
            }
            if (user.hasSpecialAuthority(User.SPECIAL_AUTHORITY_SPOOL_CONTROL)) {
                retVal.add(new SimpleGrantedAuthority("ROLE_SPECIAL_AUTHORITY_SPOOL_CONTROL"));
            }
        } catch (AS400SecurityException | ObjectDoesNotExistException | IOException | InterruptedException |
                 ErrorCompletingRequestException e) {
            logger.error("Exception while getting special authorities for user {}", username, e);
        }
        return retVal;
    }

    public List<GrantedAuthority> getGrantedAuthorities(String username, List<GrantedAuthority> specialAuthorities) {
        return specialAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
