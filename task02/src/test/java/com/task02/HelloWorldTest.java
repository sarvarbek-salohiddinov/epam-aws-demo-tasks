package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldTest {
    private HelloWorld helloWorld;

    @Mock
    private Context context;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        helloWorld = new HelloWorld();
    }

    @Test
    public void testHandleRequest_ValidHelloRequest() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("rawPath", "/hello");

        Map<String, Object> httpContext = new HashMap<>();
        httpContext.put("method", "GET");

        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put("http", httpContext);

        requestMap.put("requestContext", requestContext);

        Map<String, Object> result = helloWorld.handleRequest(requestMap, context);
        System.out.println("requestMap:   "  + requestMap);
        System.out.println("================================================================");
        System.out.println("requestMap:   "  + requestMap);
        System.out.println("result:   " + result);
        System.out.println("================================================================");

        assertEquals(200, result.get("\"statusCode\""));
        assertEquals("\"Hello from Lambda\"", result.get("\"message\""));
    }

    @Test()
    public void testHandleRequest_InvalidPath() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("rawPath", "/invalid");

        Map<String, Object> httpContext = new HashMap<>();
        httpContext.put("method", "GET");

        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put("http", httpContext);

        requestMap.put("requestContext", requestContext);
        Map<String, Object> result = helloWorld.handleRequest(requestMap, context);

        assertEquals(400, result.get("\"statusCode\""));
        assertEquals("\"Bad request syntax or unsupported method. Request path: /invalid. HTTP method: GET\"", result.get("\"message\""));
    }

    @Test
    public void testHandleRequest_InvalidMethod() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("rawPath", "/hello");

        Map<String, Object> httpContext = new HashMap<>();
        httpContext.put("method", "POST");

        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put("http", httpContext);

        requestMap.put("requestContext", requestContext);
        Map<String, Object> result = helloWorld.handleRequest(requestMap, context);

        assertEquals(400, result.get("\"statusCode\""));
        assertEquals("\"Bad request syntax or unsupported method. Request path: /hello. HTTP method: POST\"", result.get("\"message\""));
    }

    @Test
    public void testHandleRequest_ExceptionThrown() {
        Map<String, Object> invalidRequest = new HashMap<>();
        Map<String, Object> result = helloWorld.handleRequest(invalidRequest, context);

        assertEquals(500, result.get("\"statusCode\""));
        assertEquals(true, ((String) result.get("\"message\"")).contains("\"Internal Server Error"));
    }
}