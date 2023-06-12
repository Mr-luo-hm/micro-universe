package com.again.micro.component.elasticsearch;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

/**
 * @author xyc35
 */
@RequiredArgsConstructor
@EnableConfigurationProperties({ ElasticSearchProperties.class })
public class EsAutoConfiguration {

	private static final String TYPE = "http";

	@Bean
	private RestHighLevelClient getClient(ElasticSearchProperties properties) {
		String[] nodes = properties.getCluster().getNodes();
		ArrayList<Node> list = new ArrayList<>();
		for (String node : nodes) {
			String[] split = node.split(":");
			list.add(new Node(new HttpHost(split[0], Integer.parseInt(split[1]), TYPE)));
		}

		return new RestHighLevelClient(
				RestClient.builder(list.toArray(new Node[0])).setHttpClientConfigCallback(httpClientBuilder -> {
					httpClientBuilder.disableAuthCaching();
					CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
					credentialsProvider.setCredentials(AuthScope.ANY,
							new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword()));
					return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
				}));
	}

}
