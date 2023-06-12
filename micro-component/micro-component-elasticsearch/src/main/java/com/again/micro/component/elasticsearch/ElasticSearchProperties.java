package com.again.micro.component.elasticsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "micro.component.elasticsearch")
public class ElasticSearchProperties {

	private String host;

	private String username;

	private String password;

	private Cluster cluster;

	public static class Cluster {

		private String[] nodes;

		public Cluster() {
		}

		public String[] getNodes() {
			return this.nodes;
		}

		public void setNodes(String[] nodes) {
			this.nodes = nodes;
		}

	}

}
