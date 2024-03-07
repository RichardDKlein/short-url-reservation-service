/**
 * Description: The interface between AWS Lambda and Spring Boot
 *
 * <p>Handles an incoming request from AWS Lambda, by proxying it
 * to Spring Boot.
 *
 * @author Richard D. Klein
 * @version 1.0
 * @since 2024-03-06
 */
package com.richarddklein.shorturlreservationservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

/**
 * Handles an incoming request from AWS Lambda, by proxying it to Spring Boot.
 */
public class StreamLambdaHandler implements RequestStreamHandler {
    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);
        } catch (ContainerInitializationException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    /**
     * Handles an incoming request from AWS Lambda, by proxying it to Spring Boot.
     *
     * @param inputStream The Lambda Function input stream
     * @param outputStream The Lambda function output stream
     * @param context The Lambda execution environment context object.
     * @throws IOException
     */
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }
}
