/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

/**
 * Provides the interface between AWS Lambda and Spring Boot.
 */
public class StreamLambdaHandler implements RequestStreamHandler {
    private static final Logger logger = LoggerFactory.getLogger(StreamLambdaHandler.class);
    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);
        } catch (ContainerInitializationException e) {
            logger.error("====> ", e);
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    /**
     * Handles an incoming request from AWS Lambda, by proxying it to
     * Spring Boot, and returning the response to AWS Lambda.
     *
     * @param inputStream The Lambda Function input stream
     * @param outputStream The Lambda function output stream
     * @param context The Lambda execution environment context object.
     * @throws IOException if an error occurs during the stream processing.
     */
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }
}
