package com.swagger.match;

/**
 * Created by bsneha on 22/07/17.
 */
public class Reason {


    private static final String PATH_NOT_FOUND_TEMPLATE = "Path(s): %s not found.";
    private static final String HTTP_METHOD_NOT_FOUND_TEMPLATE  = "%s not found";
    private static final String RESPONSE_TYPE_NOT_FOUND_TEMPLATE  = "Respone Type : %s not found in %s.";
    private static final String HTTP_METHOD_OR_PATH_NOT_FOUND="%s not found";
    private static final String TYPE_MISMATCH = "%s has type mismatch for response type %s. Expected: %s, Actual: %s";

    private static final String REQUEST_PARAMETER_TYPE_MISMATCH = "%s has type mismatch for parameter %s. Expected: %s, Actual: %s";
    private static final String NO_SCHEMA_FOUND = "%s does not contain schema for reponse type %s." ;
    private static final String NO_REFERENCE_FOUND ="%s does not contain  reference %s in provider for reponse type %s." ;
    private static final String FIELD_NOT_FOUND_IN_REF = "%s schema ref %s does not contain %s field.";
    private static final String FILED_VALUE_NOT_MATCHING ="%s schema ref %s contains field %s, but has value mismatch. actual: %s expected: %s." ;
    private static final String ADDITIONAL_REQUIRED_PARAMTER ="%s requires an additonal parameter %s in %s" ;

    public static void setQueryMethodPath(String queryMethodPath) {
        Reason.queryMethodPath = queryMethodPath;
    }

    private static String queryMethodPath;
    public static String getPathNotFound(String path) {
       return String.format(PATH_NOT_FOUND_TEMPLATE,path);
    }
    public static String getHTTPMethodNotFound() {
        return String.format(HTTP_METHOD_NOT_FOUND_TEMPLATE,queryMethodPath);
    }

    public static String getResponseTypeNotFound(String responseType) {
        return String.format(RESPONSE_TYPE_NOT_FOUND_TEMPLATE,responseType,queryMethodPath);
    }


    public static String getHTTPMethodOrPathNotFound() {
        return String.format(HTTP_METHOD_OR_PATH_NOT_FOUND,queryMethodPath);
    }

    public static String getTypeMisMatch(String expected, String actual, String responseType) {
        return String.format(TYPE_MISMATCH,queryMethodPath,responseType,expected,actual );
    }

    public static String getParamterSchemaTypeMismatch(String expected, String actual,String parameter) {
        return String.format(REQUEST_PARAMETER_TYPE_MISMATCH,queryMethodPath,parameter,expected,actual );
    }

    public static String getSchemaNotFound(String responseType) {
        return String.format(NO_SCHEMA_FOUND, queryMethodPath,responseType);
    }
    public static String getSchemaNotFound() {
        return String.format(NO_SCHEMA_FOUND, queryMethodPath);
    }

    public static String getRefereneNotFound(String responseType, String referenceName) {
        return String.format(NO_REFERENCE_FOUND,queryMethodPath, referenceName,responseType);
    }

    public static String getRefereneNotFound( String referenceName) {
        return String.format(NO_REFERENCE_FOUND,queryMethodPath, referenceName);
    }

    public static String getFieldNotFoundInReference(String referenceName, String fieldName) {
        return String.format(FIELD_NOT_FOUND_IN_REF,queryMethodPath, referenceName, fieldName);
    }

    public static String getRefValueDoNotMatch(String referenceName, String fieldName, String providerValue, String localValue) {
       return String.format( FILED_VALUE_NOT_MATCHING, queryMethodPath, referenceName, fieldName, providerValue, localValue);
    }

    public static String getAnAdditionalRequiredParameter(String name, String parameterIn) {
        return String.format(ADDITIONAL_REQUIRED_PARAMTER, queryMethodPath,name,parameterIn);
    }
}
