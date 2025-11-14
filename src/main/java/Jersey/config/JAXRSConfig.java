package Jersey.config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class JAXRSConfig extends ResourceConfig {
    public JAXRSConfig() {
        packages("src.main.resources");
    }
}