package backend.auth_app.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class ProjectConfig {

    @Bean 
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    
}
