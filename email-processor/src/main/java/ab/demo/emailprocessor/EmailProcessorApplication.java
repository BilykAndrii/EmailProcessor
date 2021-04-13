package ab.demo.emailprocessor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class EmailProcessorApplication {

	@Value("${api.host}")
	private String apiHost;

	@Value("${api.port}")
	private String apiPort;

	@Value("${api.endpoint}")
	private String apiEndpoint;

	@Value("${api.scheme}")
	private String apiScheme;

	public static void main(String[] args) {
		SpringApplication.run(EmailProcessorApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public SenderService senderService() throws URISyntaxException {
		return new SenderService(createURI()) ;
	}

	@Bean
	public Executor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(10000);
		executor.initialize();
		return executor;
	}

	private URI createURI() throws URISyntaxException {
		return new URI(apiScheme, null, apiHost,
				Integer.parseInt(apiPort), apiEndpoint, null, null);
	}
}
