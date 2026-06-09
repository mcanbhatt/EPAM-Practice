package com.epam.practice.aggregator.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${webclient.connection-timeout:5000}")
    private int connectionTimeout;

    @Value("${webclient.read-timeout:5000}")
    private int readTimeout;

    @Value("${webclient.max-connections:100}")
    private int maxConnections;

    /*@Bean
    /*public WebClient webClient() {
        log.info("Configuring WebClient with connection-timeout: {}ms, read-timeout: {}ms",
                connectionTimeout, readTimeout);

        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(maxConnections)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)))
                .responseTimeout(Duration.ofMillis(readTimeout));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }*/
    
    
	/*
	 * @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
	 */
    
    @Bean
    public RestTemplate restTemplate(@Value("${microservices.user-profile.timeout:5000}") int timeout) {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(timeout);
        //factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }
}
