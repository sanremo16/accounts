package org.san.home.accounts;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class MyTestRequestFactory {
    public static MockHttpServletRequestBuilder get(String url, String headerName, String headerValue) {
        return MockMvcRequestBuilders.get(url).header(headerName, headerValue);
    }
}
