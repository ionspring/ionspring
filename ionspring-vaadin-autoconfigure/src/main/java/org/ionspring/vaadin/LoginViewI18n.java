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

package org.ionspring.vaadin;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.internal.LocaleUtil;

import java.util.Optional;

/**
 * Provides translations for the login view based on Vaadin internationalization facility.
 */
public class LoginViewI18n extends LoginI18n {
    private final I18NProvider i18NProvider;

    public static String getTranslation(String key, Object... params) {
        final Optional<I18NProvider> i18NProvider = LocaleUtil
                .getI18NProvider();
        return i18NProvider
                .map(i18n -> i18n.getTranslation(key,
                        LocaleUtil.getLocale(() -> i18NProvider), params))
                .orElseGet(() -> "!{" + key + "}!");
    }

    public LoginViewI18n(I18NProvider i18NProvider) {
        this.i18NProvider = i18NProvider;
        setHeader(new HeaderI18n());
        setForm(new FormI18n());
        setErrorMessage(new ErrorMessageI18n());
    }

    public static class HeaderI18n extends Header {
        @Override
        public String getTitle() {
            return getTranslation("ionspring.login.header.title");
        }

        @Override
        public String getDescription() {
            return getTranslation("ionspring.login.header.description");
        }
    }

    public static class FormI18n extends Form {
        @Override
        public String getTitle() {
            return getTranslation("ionspring.login.form.title");
        }

        @Override
        public String getUsername() {
            return getTranslation("ionspring.login.form.username");
        }

        @Override
        public String getPassword() {
            return getTranslation("ionspring.login.form.password");
        }

        @Override
        public String getSubmit() {
            return getTranslation("ionspring.login.form.submit");
        }

        @Override
        public String getForgotPassword() {
            return getTranslation("ionspring.login.form.forgotPassword");
        }
    }

    public static class ErrorMessageI18n extends ErrorMessage {
        @Override
        public String getTitle() {
            return getTranslation("ionspring.login.errorMessage.title");
        }

        @Override
        public String getMessage() {
            return getTranslation("ionspring.login.errorMessage.message");
        }

        @Override
        public String getUsername() {
            return getTranslation("ionspring.login.errorMessage.username");
        }

        @Override
        public String getPassword() {
            return getTranslation("ionspring.login.errorMessage.password");
        }
    }
}
