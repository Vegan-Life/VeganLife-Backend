package com.konggogi.veganlife.config;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(dateTimeProviderRef = "dateTimeImpl")
@TestConfiguration
public class JpaAuditingConfig {}
