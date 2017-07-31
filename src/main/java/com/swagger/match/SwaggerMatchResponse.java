package com.swagger.match;

import com.swagger.parser.Constants;
import com.swagger.parser.ResponseType;
import com.swagger.parser.SwaggerResponseSchema;
import com.swagger.parser.SwaggerSchemaParser;
import io.swagger.models.HttpMethod;

import java.io.IOException;
import java.util.*;

/**
 * Created by bsneha on 22/07/17.
 */
public class SwaggerMatchResponse {

    public static MatchResult areMatching(String providerSchemaPath, String localSchemaPath, String queryPath, HttpMethod httpMethod) throws IOException {
        return areMatching(providerSchemaPath, localSchemaPath, queryPath, httpMethod, ResponseType.All);
    }

    public static MatchResult areMatching(String providerSchemaPath, String localSchemaPath, String queryPath, HttpMethod httpMethod, ResponseType reponseType) throws IOException {
        SwaggerSchemaParser swaggerSchemaParser = new SwaggerSchemaParser();

        swaggerSchemaParser.initializeParser(providerSchemaPath);
        SwaggerResponseSchema providerSwaggerResponseSchema = swaggerSchemaParser.parseResponse(queryPath, httpMethod, reponseType);
        swaggerSchemaParser.initializeParser(localSchemaPath);
        SwaggerResponseSchema localSwaggerResponseSchema = swaggerSchemaParser.parseResponse(queryPath, httpMethod, reponseType);
        MatchResult matchResult = new MatchResult();
        matchResult.setQueryMethodPath(String.format("%s%s",httpMethod,queryPath));
        String errorMessage = providerSwaggerResponseSchema.getErrorMessage();
        List<String> reasonForNotMatching;
        Reason.setQueryMethodPath(String.format("%s%s",httpMethod,queryPath));
        if (errorMessage != null && localSwaggerResponseSchema.getErrorMessage() == null) {

            switch (errorMessage) {
                case Constants.INCORRECT_PATH:
                    reasonForNotMatching = Arrays.asList(Reason.getPathNotFound(queryPath));
                    break;
                case Constants.INCORRECT_HTTP_MTHHOD:
                    reasonForNotMatching = Arrays.asList(Reason.getHTTPMethodNotFound());
                    break;
                case Constants.INCORRECT_RESPONSE_TYPE:
                    reasonForNotMatching = Arrays.asList(Reason.getResponseTypeNotFound(reponseType.getCodeValue()));
                    break;
                default:
                    reasonForNotMatching = Arrays.asList(providerSwaggerResponseSchema.getErrorMessage());
            }
            matchResult.setIsMatching(false);
            matchResult.setReasonForNotMatching(reasonForNotMatching);
            return matchResult;
        }
        reasonForNotMatching = new ArrayList<>();
        SchemaCompare.compareLocalSchemaWithProviderSchema(providerSwaggerResponseSchema.getSwaggerStructurePerReponseType(), localSwaggerResponseSchema.getSwaggerStructurePerReponseType(), reasonForNotMatching);

        matchResult.setIsMatching(!(reasonForNotMatching.size() > 0));
        matchResult.setReasonForNotMatching(reasonForNotMatching);

        return matchResult;
    }

    public static MatchResult areMatching(String providerSchemaPath, String localSchemaPath) {
        MatchResult matchResult = new MatchResult();
        SwaggerSchemaParser swaggerSchemaParser = new SwaggerSchemaParser();
        swaggerSchemaParser.initializeParser(providerSchemaPath);
        HashMap<String, SwaggerResponseSchema> providerResponse = swaggerSchemaParser.parseResponseForAllPaths();

        swaggerSchemaParser.initializeParser(localSchemaPath);
        HashMap<String, SwaggerResponseSchema> localResponse = swaggerSchemaParser.parseResponseForAllPaths();
        List<String> reasonForNotMatching = new ArrayList<String>();

        for (Map.Entry<String, SwaggerResponseSchema> entry : localResponse.entrySet()) {
            String pathWithHTTPMEthod = entry.getKey();
            Reason.setQueryMethodPath(pathWithHTTPMEthod);
            if (!providerResponse.containsKey(pathWithHTTPMEthod)) {
                matchResult.setIsMatching(false);
                reasonForNotMatching.add(Reason.getHTTPMethodOrPathNotFound());
            }else{
                SwaggerResponseSchema providerSwaggerSchema = providerResponse.get(pathWithHTTPMEthod);
                SwaggerResponseSchema localSwaggerSchema = entry.getValue();
                SchemaCompare.compareLocalSchemaWithProviderSchema(providerSwaggerSchema.getSwaggerStructurePerReponseType(), localSwaggerSchema.getSwaggerStructurePerReponseType(), reasonForNotMatching);
            }
        }
        if (matchResult.isMatching())
            matchResult.setIsMatching(true);
        else
            matchResult.setReasonForNotMatching(reasonForNotMatching);
        return matchResult;
    }




}
