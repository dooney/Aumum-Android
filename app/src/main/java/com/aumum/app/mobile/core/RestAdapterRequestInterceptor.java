package com.aumum.app.mobile.core;


import com.aumum.app.mobile.authenticator.ApiKeyProvider;

import static com.aumum.app.mobile.core.Constants.Http.HEADER_PARSE_APP_ID;
import static com.aumum.app.mobile.core.Constants.Http.HEADER_PARSE_REST_API_KEY;
import static com.aumum.app.mobile.core.Constants.Http.HEADER_PARSE_SESSION_TOKEN;
import static com.aumum.app.mobile.core.Constants.Http.PARSE_APP_ID;
import static com.aumum.app.mobile.core.Constants.Http.PARSE_REST_API_KEY;

import retrofit.RequestInterceptor;

public class RestAdapterRequestInterceptor implements RequestInterceptor {

    private UserAgentProvider userAgentProvider;
    private ApiKeyProvider apiKeyProvider;

    public RestAdapterRequestInterceptor(UserAgentProvider userAgentProvider, ApiKeyProvider apiKeyProvider) {
        this.userAgentProvider = userAgentProvider;
        this.apiKeyProvider = apiKeyProvider;
    }

    @Override
    public void intercept(RequestFacade request) {

        // Add header to set content type of JSON
        request.addHeader("Content-Type", "application/json");

        // Add auth info for PARSE, normally this is where you'd add your auth info for this request (if needed).
        request.addHeader(HEADER_PARSE_REST_API_KEY, PARSE_REST_API_KEY);
        request.addHeader(HEADER_PARSE_APP_ID, PARSE_APP_ID);

        // Add the user agent to the request.
        request.addHeader("User-Agent", userAgentProvider.get());

        String authToken = apiKeyProvider.getAuthToken();
        if (authToken != null) {
            request.addHeader(HEADER_PARSE_SESSION_TOKEN, authToken);
        }
    }
}
