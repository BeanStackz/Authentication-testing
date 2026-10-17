package backend.auth_app.dtos;

/**
 * LoginRequest
 */
public record LoginRequest(
    String email,
    String password
) {

}
