package org.recap;

import org.apache.catalina.connector.Connector;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.solr.core.SolrTemplate;

import java.io.File;

@SpringBootApplication
public class Main {

	@Value("${server.protocol}")
	String serverProtocol;

	@Value("${solr.url}")
	String solrUrl;

	@Value("${solr.parent.core}")
	String solrParentCore;

	@Value("${tomcat.maxParameterCount}")
	Integer tomcatMaxParameterCount;

	@Bean
	public SolrClient solrAdminClient() {
		return new HttpSolrClient(serverProtocol + solrUrl);
	}

	@Bean
	public SolrClient solrClient() {
		String baseURLForParentCore = serverProtocol + solrUrl + File.separator + solrParentCore;
		return new HttpSolrClient(baseURLForParentCore);
	}

	@Bean
	public SolrTemplate solrTemplate(SolrClient solrClient) throws Exception {
		SolrTemplate solrTemplate = new SolrTemplate(solrClient);
		return solrTemplate;
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainerFactory() {
		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
		factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
			@Override
			public void customize(Connector connector) {
				connector.setMaxParameterCount(tomcatMaxParameterCount);
			}
		});
		return factory;
	}

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
