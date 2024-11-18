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

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ionspring")
public class IonSpringProperties {

    private final As400Properties as400 = new As400Properties();

    public As400Properties getAs400() {
        return as400;
    }

    public static class As400Properties {
        /**
         * IBM i dns name or IP address
         */
        private String system = "localhost";
        /**
         * IBM i user name
         */
        private String user = "*CURRENT";
        /**
         * IBM i user password
         */
        private String password = "*CURRENT";
        /**
         * User SSL connection to IBM i
         */
        private boolean secured = false;

        public String getSystem() {
            return system;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public boolean isSecured() {
            return secured;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setSecured(boolean secured) {
            this.secured = secured;
        }

    }
}
