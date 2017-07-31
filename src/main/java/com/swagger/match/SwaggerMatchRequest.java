package com.swagger.match;

import com.fasterxml.jackson.databind.JsonNode;
import com.swagger.parser.*;
import io.swagger.models.HttpMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bsneha on 27/07/17.
 */
public class SwaggerMatchRequest {

    public static MatchResult areMatching(String providerSchemaPath, String localSchemaPath) {
        MatchResult matchResult = new MatchResult();
        SwaggerSchemaParser swaggerSchemaParser = new SwaggerSchemaParser();
        swaggerSchemaParser.initializeParser(providerSchemaPath);
        HashMap<String, SwaggerRequestSchema> providerSchemaForAllPaths = swaggerSchemaParser.parseRequestForAllPaths();

        swaggerSchemaParser.initializeParser(localSchemaPath);
        HashMap<String, SwaggerRequestSchema> localSchemaForAllPaths = swaggerSchemaParser.parseRequestForAllPaths();
        List<String> reasonForNotMatching = new ArrayList<String>();
        for (HashMap.Entry<String, SwaggerRequestSchema> localSchemaForPath : localSchemaForAllPaths.entrySet()) {
            String pathWithHTTPMEthod = localSchemaForPath.getKey();
            Reason.setQueryMethodPath(pathWithHTTPMEthod);
            if (!providerSchemaForAllPaths.containsKey(pathWithHTTPMEthod)) {
                matchResult.setIsMatching(false);
                reasonForNotMatching.add(Reason.getHTTPMethodOrPathNotFound());
            } else {
                SwaggerRequestSchema providerRequestSchema = providerSchemaForAllPaths.get(pathWithHTTPMEthod);
                SwaggerRequestSchema localRequestSchema = localSchemaForPath.getValue();

                matchParameters(providerRequestSchema, localRequestSchema, reasonForNotMatching);
            }
        }
        matchResult.setIsMatching(!(reasonForNotMatching.size() > 0));
        matchResult.setReasonForNotMatching(reasonForNotMatching);
        return matchResult;
    }

    public static MatchResult areMatching(String providerSchemaPath, String localSchemaPath, String queryPath, HttpMethod httpMethod) {
        MatchResult matchResult = new MatchResult();
        SwaggerSchemaParser swaggerSchemaParser = new SwaggerSchemaParser();
        swaggerSchemaParser.initializeParser(providerSchemaPath);
        SwaggerRequestSchema providerRequestSchema = swaggerSchemaParser.parseRequest(queryPath, httpMethod);

        swaggerSchemaParser.initializeParser(localSchemaPath);
        SwaggerRequestSchema localRequestSchema = swaggerSchemaParser.parseRequest(queryPath, httpMethod);
        Reason.setQueryMethodPath(String.format("%s%s", httpMethod, queryPath));
        String errorMessage = providerRequestSchema.getErrorMessage();
        List<String> reasonForNotMatching;
        if (errorMessage != null && localRequestSchema.getErrorMessage() == null) {

            switch (errorMessage) {
                case Constants.INCORRECT_PATH:
                    reasonForNotMatching = Arrays.asList(Reason.getPathNotFound(queryPath));
                    break;
                case Constants.INCORRECT_HTTP_MTHHOD:
                    reasonForNotMatching = Arrays.asList(Reason.getHTTPMethodNotFound());
                    break;
                default:
                    reasonForNotMatching = Arrays.asList(providerRequestSchema.getErrorMessage());
            }
            matchResult.setIsMatching(false);
            matchResult.setReasonForNotMatching(reasonForNotMatching);
            return matchResult;
        }
        reasonForNotMatching = new ArrayList<String>();
        matchParameters(providerRequestSchema, localRequestSchema, reasonForNotMatching);

        matchResult.setIsMatching(!(reasonForNotMatching.size() > 0));
        matchResult.setReasonForNotMatching(reasonForNotMatching);
        return matchResult;
    }

    private static void matchParameters(SwaggerRequestSchema providerRequestSchema, SwaggerRequestSchema localRequestSchema, List<String> reasonForNotMatching) {
        ArrayList<RequestParameter> providerParameters = providerRequestSchema.getParameters();
        ArrayList<RequestParameter> localParameters = localRequestSchema.getParameters();
        for (RequestParameter provideParameter : providerParameters) {
            RequestParameter sameNameLocalParameter = null;
            for (RequestParameter localParameter : localParameters) {
                if (localParameter.getName().equals(provideParameter.getName()) && localParameter.getParameterIn().equals(provideParameter.getParameterIn())) {
                    sameNameLocalParameter = localParameter;
                    break;
                }
            }
            if (sameNameLocalParameter == null && provideParameter.getIsRequired()) {
                reasonForNotMatching.add(Reason.getAnAdditionalRequiredParameter(provideParameter.getName(), provideParameter.getParameterIn()));
            } else if (sameNameLocalParameter == null) {
                continue;
            } else {
                SwaggerSchema providerSwaggerSchema = provideParameter.getSwaggerSchema();
                HashMap<String, JsonNode> providerParsedSchema = providerSwaggerSchema.getParsedSchema();
                SwaggerSchema sameNameLocalSwaggerSchema = sameNameLocalParameter.getSwaggerSchema();
                HashMap<String, JsonNode> localParsedSchema = sameNameLocalSwaggerSchema.getParsedSchema();
                String providerType = providerSwaggerSchema.getType();
                String localType = sameNameLocalSwaggerSchema.getType();
                if (providerType != localType) {
                    reasonForNotMatching.add(Reason.getParamterSchemaTypeMismatch(localType, providerType,provideParameter.getName()));
                }
                else if (localParsedSchema != null) {
                    if (providerParsedSchema == null) {
                        reasonForNotMatching.add(Reason.getSchemaNotFound());
                    } else {
                        for (HashMap.Entry<String, JsonNode> schemaEntry : localParsedSchema.entrySet()) {
                            String referenceName = schemaEntry.getKey();
                            if (!providerParsedSchema.containsKey(referenceName)) {
                                reasonForNotMatching.add(Reason.getRefereneNotFound(referenceName));
                            } else {
                                JsonNode providerJsonNode = providerParsedSchema.get(referenceName);
                                JsonNode localJsonNode = schemaEntry.getValue();
                                SchemaCompare.addReasonForNotMatchingBetweenProviderAndLocal(localJsonNode, providerJsonNode, reasonForNotMatching, referenceName);
                            }

                        }
                    }
                }


            }

        }
    }
}
