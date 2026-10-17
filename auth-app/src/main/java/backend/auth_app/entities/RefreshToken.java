package backend.auth_app.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "refresh_token", indexes = {
    @Index (name = "refresh_tokens_jti_index", columnList = "jti"),
    @Index (name = "refresh_tokens_userId_index", columnList = "user_id")
})
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column (unique = true, nullable = false, updatable = false)
    private String jti;

    @ManyToOne(
        fetch = FetchType.LAZY, 
        optional = false
    )
    @JoinColumn (name = "user_id", nullable = false)
    private User user;

    @Column (nullable = false, updatable = false)
    private Instant createdAt;

    @Column (nullable = false, updatable = false)
    private Instant expiresAt;

    @Column (nullable = false)
    private boolean revoked;

    private String replacedByToken;
    
}
