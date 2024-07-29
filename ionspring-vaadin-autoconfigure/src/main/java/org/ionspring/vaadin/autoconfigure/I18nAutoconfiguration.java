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

import org.ionspring.vaadin.SpringI18nProvider;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.spring.SpringBootAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@AutoConfiguration(before = SpringBootAutoConfiguration.class)
public class I18nAutoconfiguration {

    @Configuration
    @ConditionalOnClass({ResourceBundleMessageSource.class, I18NProvider.class})
    public static class I18nConfiguration {


        @Bean
        @ConditionalOnMissingBean
        public MessageSource messageSource() {
            ResourceBundleMessageSource parentRBMS = new ResourceBundleMessageSource();
            parentRBMS.setBasename("ionspring-i18n/translations");
            parentRBMS.setDefaultEncoding("UTF-8");
            ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
            resourceBundleMessageSource.setBasename("vaadin-i18n/translations");
            resourceBundleMessageSource.setParentMessageSource(parentRBMS);
            resourceBundleMessageSource.setDefaultEncoding("UTF-8");
            return resourceBundleMessageSource;
        }

        @Bean
        @ConditionalOnMissingBean
        public I18NProvider i18NProvider() {
            return new SpringI18nProvider(messageSource());
        }
    }
}
