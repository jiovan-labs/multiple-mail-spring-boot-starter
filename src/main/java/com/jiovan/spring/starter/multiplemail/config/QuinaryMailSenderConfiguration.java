/*
 * Copyright (c) 2020 Jiovan Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiovan.spring.starter.multiplemail.config;


import jakarta.activation.MimeType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration;
import org.springframework.boot.mail.autoconfigure.MailConfigurationUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.jiovan.plexus.spring.condition.annotation.ConditionalOnAnyProperties;
import com.jiovan.plexus.spring.condition.annotation.ConditionalOnMissingProperty;


/**
 * Quinary configuration for mail sender
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBooleanProperty(prefix = "jiovan.multiple-mail", name = "enabled", matchIfMissing = true)
@ConditionalOnAnyProperties(value = {
    @ConditionalOnProperty(prefix = QuinaryMailSenderConfiguration.PROPERTIES_PREFIX, name = "host"),
    @ConditionalOnProperty(prefix = QuinaryMailSenderConfiguration.PROPERTIES_PREFIX, name = "jndi-name")
})
@ConditionalOnClass(value = {MimeMessage.class, MimeType.class, MailSender.class})
@AutoConfigureAfter(value = QuaternaryMailSenderConfiguration.class)
@AutoConfigureBefore(value = MailSenderAutoConfiguration.class)
@Import(value = {QuinaryMailSenderConfiguration.Entry.class, QuinaryMailSenderConfiguration.Jndi.class, QuinaryMailSenderConfiguration.Classic.class, QuinaryMailSenderConfiguration.Validator.class})
public class QuinaryMailSenderConfiguration {
    public static final String PROPERTIES_PREFIX = "jiovan.multiple-mail.quinary";    // $NON-NLS-1$
    public static final String MAIL_PROPERTIES = "quinaryMailProperties";    // $NON-NLS-1$
    public static final String MAIL_SESSION = "quinaryMailSession";    // $NON-NLS-1$
    public static final String MAIL_SENDER = "quinaryMailSender";    // $NON-NLS-1$
    public static final String SSL_BUNDLES = "quinaryMailSslBundles";    // $NON-NLS-1$


    /**
     * Mail sender entry
     *
     * @author David Hsing
     */
    @Order(value = 0)
    static class Entry {
        /**
         * Returns a mail properties bean bound from the {@code PROPERTIES_PREFIX} configuration properties
         *
         * @return a mail properties bean bound from the {@code PROPERTIES_PREFIX} configuration properties
         */
        @Bean(name = MAIL_PROPERTIES)
        @ConditionalOnMissingBean(name = MAIL_PROPERTIES)
        @ConfigurationProperties(prefix = PROPERTIES_PREFIX)
        public MailProperties mailProperties() {
            return new MailProperties();
        }
    }


    /**
     * Mail sender of JNDI
     *
     * @author David Hsing
     */
    @ConditionalOnProperty(prefix = PROPERTIES_PREFIX, name = "jndi-name")
    @ConditionalOnClass(value = Session.class)
    @ConditionalOnBean(name = MAIL_PROPERTIES, value = MailProperties.class)
    @Order(value = 1)
    static class Jndi {
        /**
         * Returns a JNDI mail session resolved from the mail properties for the quinary mail sender
         *
         * @param properties the quinary mail properties that hold the JNDI name of the session
         *
         * @return a JNDI mail session resolved from the mail properties for the quinary mail sender
         *
         * @throws IllegalStateException if the JNDI name cannot be resolved to a {@link jakarta.mail.Session}
         */
        @Bean(name = MAIL_SESSION)
        @ConditionalOnMissingBean(name = MAIL_SESSION)
        public Session mailSession(@Qualifier(value = MAIL_PROPERTIES) @Nonnull MailProperties properties) throws IllegalStateException {
            return MailConfigurationUtils.jndiMailSession(properties);
        }

        /**
         * Returns a JNDI-backed JavaMail sender for the quinary mail sender
         *
         * @param properties the quinary mail properties used to configure the sender
         * @param session the quinary JNDI mail session through which the sender sends messages
         *
         * @return a JNDI-backed JavaMail sender for the quinary mail sender
         */
        @Bean(name = MAIL_SENDER)
        @ConditionalOnBean(name = MAIL_SESSION)
        @ConditionalOnMissingBean(name = MAIL_SENDER)
        public JavaMailSenderImpl mailSender(@Qualifier(value = MAIL_PROPERTIES) @Nonnull MailProperties properties, @Qualifier(value = MAIL_SESSION) @Nonnull Session session) {
            return MailConfigurationUtils.jndiMailSender(properties, session);
        }
    }


    /**
     * Mail sender of classic
     *
     * @author David Hsing
     */
    @ConditionalOnMissingProperty(prefix = PROPERTIES_PREFIX, name = "jndi-name")
    @ConditionalOnBean(name = MAIL_PROPERTIES, value = MailProperties.class)
    @Order(value = 2)
    static class Classic {
        /**
         * Returns a classic JavaMail sender configured from the quinary mail properties
         *
         * @param properties the quinary mail properties that hold the host, port, credentials and protocol settings
         * @param bundles the quinary SSL bundles used to configure SMTP over SSL, may be {@code null}
         *
         * @return a classic JavaMail sender configured from the quinary mail properties
         */
        @Bean(name = MAIL_SENDER)
        @ConditionalOnMissingBean(name = MAIL_SENDER)
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        public JavaMailSenderImpl mailSender(@Qualifier(value = MAIL_PROPERTIES) @Nonnull MailProperties properties, @Qualifier(value = SSL_BUNDLES) @Nullable SslBundles bundles) {
            return MailConfigurationUtils.classicMailSender(properties, bundles);
        }
    }


    /**
     * Mail sender validator
     *
     * @author David Hsing
     */
    @ConditionalOnProperty(prefix = PROPERTIES_PREFIX, value = "test-connection")
    @ConditionalOnBean(name = MAIL_SENDER)
    @Order(value = 3)
    static class Validator implements InitializingBean {
        @Autowired
        @Qualifier(value = MAIL_SENDER)
        private JavaMailSenderImpl mailSender;

        @Override
        public void afterPropertiesSet() throws IllegalStateException {
            MailConfigurationUtils.validateMailConnection(mailSender);
        }
    }
}
