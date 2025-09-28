package com.voriq.security_service.config;

/**
 * Centralized API path constants for the Security Service token endpoints.
 * <p>
 * These constants are intended to be reused across controllers, clients, and tests
 * to keep endpoint definitions consistent and versioned.
 * </p>
 *
 * <h2>How to use</h2>
 * <p><b>Choose one pattern and stick to it to avoid double prefixes:</b></p>
 *
 * <ul>
 *   <li><b>Controller base + short segments</b> — recommended for Spring MVC:
 *     <pre>{@code
 *     @RequestMapping(ApiPaths.TOKENS_BASE)      // "/v1/tokens"
 *     public interface TokenApi {
 *       @PostMapping(ApiPaths.ISSUE)             // "/issue"
 *       ResponseEntity<TokensDto> issue(...);
 *     }
 *     // Effective URL (with global context-path, e.g. "/api"): /api/v1/tokens/issue
 *     }</pre>
 *   </li>
 *   <li><b>Full endpoint constants</b> — handy for tests, clients, link building:
 *     <pre>{@code
 *     String url = ApiPaths.ISSUE_URL;           // "/v1/tokens/issue"
 *     // With context-path "/api": /api/v1/tokens/issue
 *     }</pre>
 *   </li>
 * </ul>
 *
 * <p>
 * If your application is mounted under a global servlet context-path (e.g., <code>/api</code>),
 * the effective external URL becomes <code>/api{this-constant}</code>. Do not prepend
 * the context-path here — keep it configured in application settings.
 * </p>
 *
 * @since 1.0
 */
public final class ApiPaths {

    /**
     * Versioned base path for all token endpoints.
     * <p>Example effective URL (with context-path <code>/api</code>): <code>/api/v1/tokens</code>.</p>
     */
    public static final String TOKENS_BASE = "/v1/tokens";

    /**
     * Full path (relative to the servlet context-path) for issuing tokens.
     * <p>Combines {@link #TOKENS_BASE} and {@link #ISSUE}.</p>
     * <p>Example: <code>/v1/tokens/issue</code> → with context-path <code>/api</code>: <code>/api/v1/tokens/issue</code>.</p>
     *
     * @see #ISSUE
     */
    public static final String ISSUE_URL = TOKENS_BASE + "/issue";

    /**
     * Full path (relative to the servlet context-path) for validating tokens.
     * <p>Combines {@link #TOKENS_BASE} and {@link #VALIDATE}.</p>
     * <p>Example: <code>/v1/tokens/validate</code> → with context-path <code>/api</code>: <code>/api/v1/tokens/validate</code>.</p>
     *
     * @see #VALIDATE
     */
    public static final String VALIDATE_URL = TOKENS_BASE + "/validate";

    /**
     * Full path (relative to the servlet context-path) for revoking tokens.
     * <p>Combines {@link #TOKENS_BASE} and {@link #REVOKE}.</p>
     * <p>Example: <code>/v1/tokens/revoke</code> → with context-path <code>/api</code>: <code>/api/v1/tokens/revoke</code>.</p>
     *
     * @see #REVOKE
     */
    public static final String REVOKE_URL = TOKENS_BASE + "/revoke";

    /**
     * Short endpoint segment to be used together with a controller-level mapping
     * {@link #TOKENS_BASE}. Do <b>not</b> prepend {@link #TOKENS_BASE} again when using this form.
     * <p>Example: <code>@RequestMapping(TOKENS_BASE) + @PostMapping(ISSUE)</code> → <code>/v1/tokens/issue</code>.</p>
     */
    public static final String ISSUE = "/issue";

    /**
     * Short endpoint segment for token validation, to be appended to {@link #TOKENS_BASE}
     * at the controller level.
     */
    public static final String VALIDATE = "/validate";

    /**
     * Short endpoint segment for token revocation, to be appended to {@link #TOKENS_BASE}
     * at the controller level.
     */
    public static final String REVOKE = "/revoke";

    /**
     * Utility class: no instances.
     */
    private ApiPaths() {
    }
}
