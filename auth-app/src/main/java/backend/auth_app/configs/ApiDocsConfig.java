package backend.auth_app.configs;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration 
@OpenAPIDefinition (info = @Info ( 
    title = "Auth App API",
    version = "1.0",
    description = "API documentation for the Auth App" ,
    contact = @Contact (
        name = "Marsal",
        email = "marsalsoren1660@gmail.com"
    )   
),
security = @SecurityRequirement (name = "bearerAuth")
)
@SecurityScheme (
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class ApiDocsConfig {
    
}
