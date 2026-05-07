package com.sellspark.SellsHRMS.monitoring.service;

import com.sellspark.SellsHRMS.monitoring.dto.HttpCheckResult;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorUrl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpClientService {

    /**
     * Create a new RestClient instance per request to allow different timeouts per
     * URL
     */
    private RestClient createRestClient(MonitorUrl url) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(url.getTimeout()).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(url.getTimeout()).toMillis());

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    public HttpCheckResult checkUrl(MonitorUrl url) {
        long startTime = System.currentTimeMillis();

        try {
            RestClient restClient = createRestClient(url);

            // FIXED: Make the request with the actual URL
            ClientHttpResponse response = makeRequest(restClient, url);

            long responseTime = System.currentTimeMillis() - startTime;
            HttpStatusCode statusCode = response.getStatusCode();
            boolean isUp = statusCode.is2xxSuccessful();

            return HttpCheckResult.success(
                    statusCode.value(),
                    (int) responseTime,
                    isUp);

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("HTTP check failed for URL: {}", url.getUrl(), e);

            String errorMessage = e.getMessage();
            if (e.getMessage() != null && e.getMessage().contains("timeout")) {
                errorMessage = "Timeout after " + url.getTimeout() + " seconds";
            } else if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                errorMessage = "Connection refused";
            } else if (e.getMessage() != null && e.getMessage().contains("Unknown host")) {
                errorMessage = "DNS lookup failed - Unknown host";
            }

            return HttpCheckResult.failure(
                    null,
                    (int) responseTime,
                    false,
                    errorMessage);
        }
    }

    /**
     * Make the actual HTTP request with the specified URL
     */
    private ClientHttpResponse makeRequest(RestClient restClient, MonitorUrl url) {
        String targetUrl = url.getUrl();

        // Ensure URL has protocol
        if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
            targetUrl = "https://" + targetUrl;
            log.debug("Added https:// prefix to URL: {}", targetUrl);
        }

        switch (url.getMethod()) {
            case GET:
                return restClient.get()
                        .uri(targetUrl)
                        .exchange((req, res) -> res);
            case POST:
                return restClient.post()
                        .uri(targetUrl)
                        .exchange((req, res) -> res);
            case HEAD:
                return restClient.head()
                        .uri(targetUrl)
                        .exchange((req, res) -> res);
            default:
                return restClient.get()
                        .uri(targetUrl)
                        .exchange((req, res) -> res);
        }
    }
}