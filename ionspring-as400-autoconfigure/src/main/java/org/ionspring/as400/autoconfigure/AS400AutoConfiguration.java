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
import com.ibm.as400.access.AS400JDBCDataSource;
import com.ibm.as400.access.SecureAS400;
import org.ionspring.as400.AS400AuthenticationProvider;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.security.authentication.AuthenticationProvider;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * AS400 and related bean autoconfiguration.
 */
@AutoConfiguration
@ConditionalOnClass(AS400.class)
@EnableConfigurationProperties(IonSpringProperties.class)
public class AS400AutoConfiguration {

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public AS400 as400(IonSpringProperties properties) {
        if (!System.getProperty("os.name").equals("OS/400")) {
            if (properties.getAs400().getSystem().equals("localhost")) {
                throw new BeanCreationException("ionspring.as400.system can't be set to \"localhost\" when not running on OS400");
            }
            if (properties.getAs400().getUser().equals("*CURRENT")) {
                throw new BeanCreationException("ionspring.as400.user can't be set to \"*CURRENT\" when not running on OS400");
            }
            if (properties.getAs400().getPassword().equals("*CURRENT")) {
                throw new BeanCreationException("ionspring.as400.passord can't be set to \"*CURRENT\" when not running on OS400");
            }
        }

        if (properties.getAs400().isSecured()) {
            return new SecureAS400(properties.getAs400().getSystem(),
                    properties.getAs400().getUser(),
                    properties.getAs400().getPassword().toCharArray());
        } else {
            return new AS400(properties.getAs400().getSystem(),
                    properties.getAs400().getUser(),
                    properties.getAs400().getPassword().toCharArray());
        }
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    @ConditionalOnBean(AS400.class)
    @ConfigurationProperties("ionspring.as400.datasource")
    public DataSource as400JDBCDataSource(ConfigurableEnvironment configurableEnvironment,
                                          AS400 as400,
                                          Environment environment) {
        // Hibernate dialect auto select is currently bugged with JTOpen driver and selects DB2Dialect instead of
        // DB2iDialect. Therefore, we force DB2iDialect, you can ignore the warning in the log saying it's
        // unnecessary.
        final Properties props = new Properties();
        props.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.DB2iDialect");
        configurableEnvironment.getPropertySources().addLast(new PropertiesPropertySource("ionspring", props));

        final AS400JDBCDataSource ds = new AS400JDBCDataSource(as400);
        Binder.get(environment).bindOrCreate("ionspring.as400.datasource", Bindable.ofInstance(ds));
        // AS400JDBCDataSource does not pool connections, it creates a new one each time.
        // If Hikari is found on the classpath, we wrap the AS400JDBCDataSource in a Hikari datasource
        try {
            final Class<?> configClass = Class.forName("com.zaxxer.hikari.HikariConfig");
            final Object config = configClass.getDeclaredConstructor().newInstance();
            final Method setDataSource = configClass.getDeclaredMethod("setDataSource", DataSource.class);
            setDataSource.invoke(config, ds);
            final Class<?> cpClass = Class.forName("com.zaxxer.hikari.HikariDataSource");
            @SuppressWarnings("JavaReflectionInvocation") final Object cp = cpClass.getDeclaredConstructor(configClass).newInstance(config);
            return (DataSource) cp;
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            return ds;
        }
    }

    @ConditionalOnClass(AuthenticationProvider.class)
    @Lazy
    static
    class AS400AuthenticationManager {
        @Bean
        @ConditionalOnMissingBean
        public AuthenticationProvider as400AuthenticationManager(AS400 as400) {
            return new AS400AuthenticationProvider(as400);
        }
    }
}
