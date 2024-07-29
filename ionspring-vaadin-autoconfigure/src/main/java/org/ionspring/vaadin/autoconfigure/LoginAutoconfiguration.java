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

package org.ionspring.vaadin.autoconfigure;

import org.ionspring.vaadin.LoginViewI18n;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
public class LoginAutoconfiguration {

    @Configuration
    @ConditionalOnClass({LoginI18n.class})
    public static class LoginConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public LoginI18n loginI18n(I18NProvider i18NProvider) {
            return new LoginViewI18n(i18NProvider);
        }
    }
}