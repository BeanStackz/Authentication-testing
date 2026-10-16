package backend.auth_app.exceptions;

public record ErrorResponse(
    String message,
    int statusCode
) {


}
