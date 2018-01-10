package com.nepxion.aquarius.common.exception;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

public class AquariusException extends RuntimeException {
    private static final long serialVersionUID = 7895884193269203187L;

    public AquariusException() {
        super();
    }

    public AquariusException(String message) {
        super(message);
    }

    public AquariusException(String message, Throwable cause) {
        super(message, cause);
    }

    public AquariusException(Throwable cause) {
        super(cause);
    }
}