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

package org.springframework.boot.mail.autoconfigure;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.mail.Session;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.jiovan.plexus.spring.toolkit.support.SingletonObjectProvider;


/**
 * Utilities for configuring mail sender
 *
 * @author David Hsing
 *
 * @see org.springframework.mail.javamail.JavaMailSenderImpl
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class MailConfigurationUtils {
    /**
     * Returns a JNDI mail session resolved from the JNDI name in the given mail properties
     *
     * @param properties the mail properties that hold the JNDI name of the session
     *
     * @return a JNDI mail session resolved from the JNDI name in the given mail properties
     *
     * @throws IllegalStateException if the JNDI name cannot be resolved to a {@link jakarta.mail.Session}
     */
    public static Session jndiMailSession(@Nonnull MailProperties properties) throws IllegalStateException {
        MailSenderJndiConfiguration configuration = new MailSenderJndiConfiguration(properties);
        return configuration.session();
    }

    /**
     * Returns a JavaMail sender that sends through the given JNDI mail session
     *
     * @param properties the mail properties used to configure the sender
     * @param session the JNDI mail session through which the sender sends messages
     *
     * @return a JavaMail sender that sends through the given JNDI mail session
     */
    public static JavaMailSenderImpl jndiMailSender(@Nonnull MailProperties properties, @Nonnull Session session) {
        MailSenderJndiConfiguration configuration = new MailSenderJndiConfiguration(properties);
        return configuration.mailSender(session);
    }

    /**
     * Returns a JavaMail sender configured from the given mail properties and SSL bundles
     *
     * @param properties the mail properties that hold the host, port, credentials and protocol settings
     * @param bundles the SSL bundles used to configure SMTP over SSL, may be {@code null}
     *
     * @return a JavaMail sender configured from the given mail properties and SSL bundles
     */
    public static JavaMailSenderImpl classicMailSender(@Nonnull MailProperties properties, @Nullable SslBundles bundles) {
        MailSenderPropertiesConfiguration configuration = new MailSenderPropertiesConfiguration();
        return configuration.mailSender(properties, SingletonObjectProvider.ofNullable(bundles));
    }

    /**
     * Validates the connection of the given JavaMail sender at startup
     *
     * @param mailSender the JavaMail sender whose connection to test
     *
     * @throws IllegalStateException if the mail server is not available
     */
    public static void validateMailConnection(@Nonnull JavaMailSenderImpl mailSender) throws IllegalStateException {
        new MailSenderValidatorAutoConfiguration(mailSender);
    }
}
