package com.jack.userservice.constants;

public class ErrorMessages {
    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // General Status Messages
    public static final String NOT_FOUND_STATUS = "Not Found";
    public static final String CONFLICT_STATUS = "Conflict";
    public static final String UNAUTHORIZED_STATUS = "Unauthorized";

    // User-related Error Messages
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String EMAIL_ALREADY_REGISTERED = "Email already registered. Try to log in or register with another email.";
    public static final String EMAIL_ALREADY_REGISTERED_BY_ANOTHER_USER = "Email already registered by another user.";
    public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password.";

    // API Paths for Error Context
    public static final String GET_USER_API_PATH = "GET /api/users/";
    public static final String POST_USER_API_PATH = "POST /api/users";
    public static final String PUT_USER_API_PATH = "PUT /api/users/";
    public static final String DELETE_USER_API_PATH = "DELETE /api/users/";
    public static final String POST_LOGIN_API_PATH = "POST /api/users/login";
}
