package backend.auth_app.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Role
 */
@Getter 
@Setter 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor 
@Entity 
@Table(name = "roles") // Changed to "roles" so it doesn't conflict with your @JoinTable "user_roles"
public class Role {
    
    @Id 
    private UUID id; // Inline initialization removed to satisfy Java 25 bytecode checks

    @Column(unique = true, nullable = false)
    private String name;

    @PrePersist 
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID(); // Safe runtime generation before DB insertion
        }
    }
}
