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

import static org.assertj.core.api.Assertions.*;

import com.ibm.as400.access.AS400JDBCDataSource;
import com.ibm.as400.access.SecureAS400;
import org.junit.jupiter.api.AfterAll;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;

public class AS400AutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AS400AutoConfiguration.class));

    private static final String osName = System.getProperty("os.name");

    @AfterAll
    static void resetOsName() {
        System.setProperty("os.name", osName);
    }

    @Test
    void as400LocalhostSystemOnNonOS400ThrowsException() {
        System.setProperty("os.name", "LINUX");
        assertThrows(BeanCreationException.class, () -> {
            this.contextRunner.run((context) -> {
                context.getBean(AS400.class);
            });
        });
    }

    @Test
    void as400CurrentUserOnNonOS400ThrowsException() {
        System.setProperty("os.name", "LINUX");
        assertThrows(BeanCreationException.class, () -> {
            this.contextRunner.withPropertyValues("ionspring.as400.system=as400").run((context) -> {
                context.getBean(AS400.class);
            });
        });
    }

    @Test
    void as400CurrentPasswordOnNonOS400ThrowsException() {
        System.setProperty("os.name", "LINUX");
        assertThrows(BeanCreationException.class, () -> {
            this.contextRunner.withPropertyValues("ionspring.as400.system=as400", "ionspring.as400.user=user").run((context) -> {
                context.getBean(AS400.class);
            });
        });
    }

    @Test
    void as400NonDefaultOnNonOS400Works() {
        System.setProperty("os.name", "LINUX");
        this.contextRunner.withPropertyValues("ionspring.as400.system=as400", "ionspring.as400.user=user", "ionspring.as400.password=password").run((context) -> {
            assertThat(context).hasSingleBean(AS400.class);
        });
    }

    @Test
    void as400DefaultOnOS400Works() {
        System.setProperty("os.name", "OS/400");
        this.contextRunner.run((context) -> {
            assertThat(context).hasSingleBean(AS400.class);
        });
    }

    @Test
    void as400BacksOff() {
        System.setProperty("os.name", "OS/400");
        this.contextRunner.run((context) -> {
            assertThat(context).hasSingleBean(AS400.class);
            assertThat(context).getBean("as400").isSameAs(context.getBean(AS400.class));
        });
    }

    @Test
    void noAs400BeanIfNoAS400Class() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(AS400.class)).run((context) -> {
            assertThat(context).doesNotHaveBean(AS400.class);
        });
    }

    @Test
    void nonSecuredAS400() {
        System.setProperty("os.name","OS/400");
        this.contextRunner.run((context) -> {
            assertThat(context).getBean(AS400.class).isNotInstanceOf(SecureAS400.class);
        });
    }

    @Test
    void securedAS400() {
        System.setProperty("os.name","OS/400");
        this.contextRunner.withPropertyValues("ionspring.as400.secured=true").run((context) -> {
            assertThat(context).getBean(AS400.class).isInstanceOf(SecureAS400.class);
        });
    }

    @Test
    void dataSource() {
        System.setProperty("os.name","OS/400");
        this.contextRunner.run((context) -> {
           assertThat(context).hasSingleBean(DataSource.class);
           assertThat(context).getBean(DataSource.class).isInstanceOf(AS400JDBCDataSource.class);
        });
    }
}
