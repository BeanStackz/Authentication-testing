package backend.auth_app.dtos;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import backend.auth_app.entities.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor
@NoArgsConstructor 
@Builder 
public class UserDto {
    private UUID id;

    private String email;
    
    private String name;

    private String password;
    private String image;
    private Boolean enable;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder.Default
    private Provider provider = Provider.LOCAL;

    @Builder.Default
    private Set<RoleDto> roles = new HashSet<>();

    
    
}
