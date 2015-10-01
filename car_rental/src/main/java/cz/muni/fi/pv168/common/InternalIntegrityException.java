package cz.muni.fi.pv168.common;

public class InternalIntegrityException extends RuntimeException {
    
     public InternalIntegrityException(String msg) {
        super(msg);
    }

    public InternalIntegrityException(Throwable cause) {
        super(cause);
    }

    public InternalIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
