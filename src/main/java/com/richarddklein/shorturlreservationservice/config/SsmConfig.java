/**
 * Description: The SSM (Simple Systems Manager) @Configuration class.
 *
 * <p>Tells Spring how to construct classes that are needed in order to
 * work with AWS SSM (Simple Systems Manager), for example `SsmClient`.
 *
 * @author Richard D. Klein
 * @version 1.0
 * @since 2024-01-09
 */

package com.richarddklein.shorturlreservationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
public class SsmConfig {
    @Bean
    public SsmClient
    ssmClient() {
        return SsmClient.builder().build();
    }
}
