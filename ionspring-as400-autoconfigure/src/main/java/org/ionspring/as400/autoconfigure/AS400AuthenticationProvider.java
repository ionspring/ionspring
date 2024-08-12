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
import org.springframework.lang.NonNull;
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

/**
 * A Spring Security authentication provider that performs authentication against IBM i user profiles.
 * <p>This implementation will authenticate any enabled user profile that has a password.</p>
 * <p>Granted authorities are based on the user special authorities (see {@link #getGrantedAuthorities}).</p>
 * <p>To customize authentication and authorization, this class can be subclassed and exposed as an <code>AuthenticationProvider</code> bean.</p>
 * <p>To customize authentication (i.e. select which user profiles are allowed), override {@link #isAuthorized(String)}.</p>
 * <p>To customize authorization, override {@link #getGrantedAuthorities(String, List)}.</p>
 */
public class AS400AuthenticationProvider implements AuthenticationProvider {
    protected final AS400 as400;

    Logger logger = LoggerFactory.getLogger(AS400AuthenticationProvider.class);

    public AS400AuthenticationProvider(AS400 as400) {
        this.as400 = as400;
    }

    /**
     * Performs authentication against IBM i user profiles.
     *
     * @param authentication the authentication request object.
     * @return the UsernamePasswordAuthenticationToken object if authentication is successful.
     * @throws AuthenticationException if authentication isn't successful.
     */
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

    /**
     * Used to restrict which users are allowed to access the application. Called after user/password check is successful.
     * <p>This method should be overridden to customize authentication.</p>
     * <p>The default implementation always returns <code>true</code>, allowing all enabled user profiles with a password.</p>
     * @param username The username as entered by the user (mixed case)
     * @return <code>true</code> if user should be authenticated, <code>false</code> otherwize
     */
    public boolean isAuthorized(@SuppressWarnings("unused") @NonNull String username) {
        return true;
    }

    /**
     * Returns a list of <code>GrantedAuthority</code> for the authenticated user. Since the user profile used at the application level
     * might (and probably shouldn't) have *READ authority on the authenticating user profile, a connexion to the IBM i under that
     * user profile is done to get the special authorities the user has.
     * <p>The following granted authorities are created based on the corresponding special authorities:
     * <ul>
     *     <li>ROLE_SPECIAL_AUTHORITY_AUDIT</li>
     *     <li>ROLE_SPECIAL_AUTHORITY_SERVICE</li>
     *     <li>ROLE_SPECIAL_AUTHORITY_ALL_OBJECT</li>
     *     <li>ROLE_SPECIAL_AUTHORITY_IO_SYSTEM_CONFIGURATION</li>
     *     <li>ROLE_SPECIAL_AUTHORITY_JOB_CONTROL</li>
     *     <li>ROLE_SPECIAL_AUTHORITY_SAVE_SYSTEM</li>
     *     <li>ROLE_SPECIAL_AUTHORITY_SECURITY_ADMINISTRATOR</li>
     *     <li>ROLE_SPECIAL_AUTHORITY_SPOOL_CONTROL</li>
     * </ul></p>
     * <p>If {@link #getGrantedAuthorities(String, List)} is overridden and doesn't use its second parameter, this method can be overiridden
     * to return null in order to avoid the unnecessary connection to get special authorities.</p>
     *
     * @param username The username entered by the user
     * @param password The password entered by the user
     * @return The list of granted authorities based on the user special authorities.
     */
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

    /**
     * Returns a list of <code>GrantedAuthoritiy</code> for the authenticated used.
     * <p>This method should be overridden to customize user authorization.</p>
     * <p>The default implementation returns the authenticating user special authorities.</p>
     *
     * @param username The username entered by the user
     * @param specialAuthorities The granted authorities corresponding to the user special authorities.
     * @return The list of <code>GrantedAuthority</code>.
     */
    public @NonNull List<GrantedAuthority> getGrantedAuthorities(@NonNull String username, List<GrantedAuthority> specialAuthorities) {
        if(specialAuthorities == null) {
            return new ArrayList<>();
        }
        return specialAuthorities;
    }

    /**
     * Returns <code>true</code> if authentication is user/password authentication, <code>false</code> otherwise.
     *
     * @param authentication The <code>Authentication</code> object class.
     * @return <code>true</code> if authentication class is <code>UsernamePasswordAuthenticationToken</code>
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
