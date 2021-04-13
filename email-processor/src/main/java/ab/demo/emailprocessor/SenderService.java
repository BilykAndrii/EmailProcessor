package ab.demo.emailprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

public class SenderService {

    @Autowired
    private RestTemplate restTemplate;

    private URI uri;

    public SenderService(URI uri) {
        this.uri = uri;
    }

    @Async
    public void send(List<DataModel> chunk) {
        try {
            restTemplate.postForEntity(uri, chunk, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
