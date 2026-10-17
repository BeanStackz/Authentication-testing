package backend.auth_app.respositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.auth_app.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByJti(String jti);
    
}
