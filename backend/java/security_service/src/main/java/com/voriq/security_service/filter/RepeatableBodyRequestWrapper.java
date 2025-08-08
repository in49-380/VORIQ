package com.voriq.security_service.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * HttpServletRequest wrapper that caches the request body so it can be read multiple times.
 * <p>
 * Typical use-cases:
 * <ul>
 *   <li>Filters that need to inspect the body (e.g., rate limiting, auditing) before controllers.</li>
 *   <li>Aspects/handlers that must access the body after it has already been consumed.</li>
 * </ul>
 * <p>
 * The original input stream is read <em>once</em> by the creator of this wrapper and stored in-memory.
 * Subsequent calls to {@link #getInputStream()} or {@link #getReader()} return new readers over the cached bytes.
 * </p>
 *
 * <h3>Notes</h3>
 * <ul>
 *   <li>Thread-safe for typical servlet usage (each request handled by a single thread).</li>
 *   <li>Large payloads are fully buffered in memory; consider limits if bodies can be big.</li>
 * </ul>
 *
 * @author RsLan
 * @since 1.0.0
 */
public class RepeatableBodyRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    /**
     * Constructs a repeatable-body wrapper.
     *
     * @param request the original request
     * @param body    the already-read body bytes (may be {@code null}, treated as empty)
     */
    public RepeatableBodyRequestWrapper(HttpServletRequest request, byte[] body) {
        super(request);
        this.body = body != null ? body : new byte[0];
    }

    /**
     * Returns a fresh {@link ServletInputStream} over the cached body.
     */
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) { /* no-op */ }
        };
    }

    /**
     * Returns a fresh {@link BufferedReader} over the cached body (UTF-8).
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * Convenience accessor to get the cached body as a string (UTF-8).
     *
     * @return cached body text (never {@code null})
     */
    public String getCachedBody() {
        return new String(body, StandardCharsets.UTF_8);
    }
}
