package br.com.algar.poc.cliente.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Spring Boot 4.1 removeu a autoconfiguração automática do Flyway (nenhuma classe
 * FlywayAutoConfiguration no spring-boot-autoconfigure-4.1.1 — confirmado por inspeção do jar).
 *
 * Dispara a migration interceptando o próprio bean "dataSource" via BeanPostProcessor: o Spring já
 * garante que "dataSource" está totalmente inicializado (incluindo post-processors) antes de injetá-lo
 * no factory method do entityManagerFactory, então a migration sempre roda antes do Hibernate validar
 * o schema (ddl-auto=validate). Uma tentativa anterior usando BeanFactoryPostProcessor + setDependsOn
 * no LocalContainerEntityManagerFactoryBean falhou em runtime real (NoSuchBeanDefinitionException:
 * '&entityManagerFactory') — descartada em favor desta, mais simples e sem introspecção de bean por tipo.
 */
@Configuration
public class FlywayConfig {

    @Bean
    static BeanPostProcessor flywayMigrationBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if ("dataSource".equals(beanName) && bean instanceof DataSource dataSource) {
                    Flyway.configure().dataSource(dataSource).load().migrate();
                }
                return bean;
            }
        };
    }
}
