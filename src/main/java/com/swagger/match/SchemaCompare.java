package com.swagger.match;

import com.fasterxml.jackson.databind.JsonNode;
import com.swagger.parser.SwaggerSchema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bsneha on 27/07/17.
 */
public class SchemaCompare {
    protected static void compareLocalSchemaWithProviderSchema(HashMap<String, SwaggerSchema> provideSchemaResponse, HashMap<String, SwaggerSchema> localSchemaResponse, List<String> reasonForNotMatching) {
        for (HashMap.Entry<String, SwaggerSchema> entry : localSchemaResponse.entrySet()) {
            String responseType = entry.getKey();
            SwaggerSchema providerSwaggerSchema = provideSchemaResponse.get(responseType);
            if(providerSwaggerSchema==null){
                reasonForNotMatching.add(Reason.getResponseTypeNotFound(responseType));
                return;
            }
            SwaggerSchema localSwaggerSchema = entry.getValue();
            String providerType = providerSwaggerSchema.getType();
            String localType = localSwaggerSchema.getType();
            if (providerType != localType) {
                reasonForNotMatching.add(Reason.getTypeMisMatch(localType, providerType, responseType));
            } else {
                HashMap<String, JsonNode> providerParsedSchema = providerSwaggerSchema.getParsedSchema();
                HashMap<String, JsonNode> localParsedSchema = localSwaggerSchema.getParsedSchema();
                if (localParsedSchema != null) {
                    if (providerParsedSchema == null) {
                        reasonForNotMatching.add(Reason.getSchemaNotFound(responseType));
                    } else {
                        for (HashMap.Entry<String, JsonNode> schemaEntry : localParsedSchema.entrySet()) {
                            String referenceName = schemaEntry.getKey();
                            if (!providerParsedSchema.containsKey(referenceName)) {
                                reasonForNotMatching.add(Reason.getRefereneNotFound(responseType, referenceName));
                            } else {
                                JsonNode providerJsonNode = providerParsedSchema.get(referenceName);
                                JsonNode localJsonNode = schemaEntry.getValue();
                                addReasonForNotMatchingBetweenProviderAndLocal(localJsonNode, providerJsonNode, reasonForNotMatching, referenceName);
                            }

                        }
                    }
                }

            }

        }
    }

    protected static void addReasonForNotMatchingBetweenProviderAndLocal(JsonNode localNode, JsonNode providerNode, List<String> reasonForNotMatching, String referenceName) {
        Iterator<String> fieldNames = localNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode localFieldValue = localNode.get(fieldName);
            JsonNode providerFieldValue = providerNode.get(fieldName);
            if (providerFieldValue == null) {
                reasonForNotMatching.add(Reason.getFieldNotFoundInReference(referenceName, fieldName));
                continue;
            }
            if (localFieldValue.isObject()) {
                String refName= localFieldValue.equals("properties")? referenceName: String.format("%s->%s",referenceName,fieldName);
                addReasonForNotMatchingBetweenProviderAndLocal(localFieldValue, providerFieldValue, reasonForNotMatching, refName);
            } else if (fieldName == "$ref") {
                continue;
            } else {
                String providerValue = providerFieldValue.asText();
                String localValue = localFieldValue.asText();
                if (!providerValue.equals(localValue)) {
                    reasonForNotMatching.add(Reason.getRefValueDoNotMatch(referenceName, fieldName, providerValue, localValue));
                }
            }
        }
    }
}
