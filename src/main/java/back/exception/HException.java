package back.exception;

import org.springframework.http.HttpStatus;

public class HException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    // 기본 400 (BAD_REQUEST) 사용
    public HException(String message) {
        this(null, message, null, HttpStatus.BAD_REQUEST);
    }

    public HException(String errorCode, String message) {
        this(errorCode, message, null, HttpStatus.BAD_REQUEST);
    }

    public HException(String message, Throwable cause) {
        this(null, message, cause, HttpStatus.BAD_REQUEST);
    }

    public HException(String errorCode, String message, Throwable cause) {
        this(errorCode, message, cause, HttpStatus.BAD_REQUEST);
    }

    public HException(String errorCode, String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
    }

    public HException(String message, HttpStatus status) {
    	 this(null, message, null, status);
    }
    
    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
