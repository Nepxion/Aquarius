package com.nepxion.aquarius.lock.exception;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

public class AopException extends RuntimeException {
    private static final long serialVersionUID = 7895884193269203187L;

    public AopException() {
        super();
    }

    public AopException(String message) {
        super(message);
    }

    public AopException(String message, Throwable cause) {
        super(message, cause);
    }

    public AopException(Throwable cause) {
        super(cause);
    }
}