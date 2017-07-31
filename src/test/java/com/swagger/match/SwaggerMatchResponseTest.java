package com.swagger.match;

import com.swagger.parser.ResponseType;
import io.swagger.models.HttpMethod;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by bsneha on 22/07/17.
 */
public class SwaggerMatchResponseTest {
    @Test
    public void shouldBeAbleToCompareResponseSchemaForAGivenPathHTTPMethodAndResponseTypeAndReturnErrorIfResponseIsNotPresentInProviderButIsPresentInConsumer() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/sampleJson.json";
        String localSchemaPath = "./src/test/resources/consumer/sampleJson.json";

        String queryPath = "/pet/findByStatus";
        MatchResult matchResult = SwaggerMatchResponse.areMatching(providerSchemaPath, localSchemaPath, queryPath, HttpMethod.GET, ResponseType.OK);
        Assert.assertFalse(matchResult.isMatching());
        Assert.assertEquals(matchResult.getReasonForNotMatching().get(0), Reason.getPathNotFound(queryPath));
    }


    @Test
    public void shouldBeAbleToCompareResponseSchemaAndNotReturnErrorIfResponsePathIsPresentInBoth() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/sampleJson.json";
        String localSchemaPath = "./src/test/resources/consumer/sampleJson.json";

        String queryPath = "/pet/findByTags";
        MatchResult matchResult = SwaggerMatchResponse.areMatching(providerSchemaPath, localSchemaPath, queryPath, HttpMethod.GET, ResponseType.OK);
        Assert.assertTrue(matchResult.isMatching());
    }

    @Test
    public void shouldBeAbleToCompareResponseSchemaAndReturnErrorIfResponsePathIsPresentInBothButHTTPMethodIsNOTPresentInProvider() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/sampleJson.json";
        String localSchemaPath = "./src/test/resources/consumer/sampleJson.json";

        String queryPath = "/store/order/{orderId}";
        MatchResult matchResult = SwaggerMatchResponse.areMatching(providerSchemaPath, localSchemaPath, queryPath, HttpMethod.DELETE,ResponseType.All);
        Assert.assertFalse(matchResult.isMatching());
        Assert.assertEquals(matchResult.getReasonForNotMatching().get(0), Reason.getHTTPMethodNotFound());
    }

    @Test
    public void shouldBeAbleToCompareResponseSchemaAndReturnErrorIfResponsePathAndHTTPMethodIsPresentInBothButResponseTypeIsNot() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/sampleJson.json";
        String localSchemaPath = "./src/test/resources/consumer/sampleJson.json";

        String queryPath = "/store/order/{orderId}";
        MatchResult matchResult = SwaggerMatchResponse.areMatching(providerSchemaPath, localSchemaPath, queryPath, HttpMethod.GET,ResponseType.BAD_REQUEST);
        Assert.assertFalse(matchResult.isMatching());
        Assert.assertEquals(matchResult.getReasonForNotMatching().get(0), Reason.getResponseTypeNotFound(ResponseType.BAD_REQUEST.getCodeValue()));
    }

    @Test
    public void shouldBeAbleToCompareResponseSchemaForAllPathsAndReturnErrorIfPathMismatch() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/sampleJson.json";
        String localSchemaPath = "./src/test/resources/consumer/sampleJson.json";

        MatchResult matchResult = SwaggerMatchResponse.areMatching(providerSchemaPath, localSchemaPath);
        Assert.assertFalse(matchResult.isMatching());
        List<String> reasonForNotMatching = matchResult.getReasonForNotMatching();
        Assert.assertEquals("Respone Type : 400 not found in GET/store/order/{orderId}.",reasonForNotMatching.get(0));
        Assert.assertEquals("DELETE/store/order/{orderId} not found",reasonForNotMatching.get(1));
        Assert.assertEquals("GET/pet/findByTagsExtra not found",reasonForNotMatching.get(2));
        Assert.assertEquals("GET/pet/{petId} schema ref root->properties does not contain category field.",reasonForNotMatching.get(3));
        Assert.assertEquals("GET/pet/{petId} does not contain  reference category in provider for reponse type 200.",reasonForNotMatching.get(4));
        Assert.assertEquals("GET/pet/findByStatus not found",reasonForNotMatching.get(5));
    }


    @Test
    public void shouldNotMatchWhenProviderSchemaResponseDoesNotContainRefType() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/uberSample.json";
        String localSchemaPath = "./src/test/resources/consumer/uberSample.json";

        MatchResult matchResult = SwaggerMatchResponse.areMatching(providerSchemaPath,localSchemaPath,"/v1/estimates/price", HttpMethod.GET);
        Assert.assertFalse(matchResult.isMatching());
        Assert.assertEquals("GET/v1/estimates/price schema ref items->properties does not contain low_estimate field.",matchResult.getReasonForNotMatching().get(0));
    }

    @Test
    public void shouldNotMatchWhenProviderSchemaResponseContainSameRefTypeButDifferentRefValue() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/uberSample.json";
        String localSchemaPath = "./src/test/resources/consumer/uberSample.json";

        MatchResult matchResult = SwaggerMatchResponse.areMatching(providerSchemaPath,localSchemaPath,"/v1/estimates/time", HttpMethod.GET);
        Assert.assertFalse(matchResult.isMatching());
        Assert.assertEquals("GET/v1/estimates/time schema ref items->properties->product_id contains field type, but has value mismatch. actual: integer expected: string.",matchResult.getReasonForNotMatching().get(0));
    }

    @Test
    public void shouldNotMatchWhenProviderSchemaResponseDoesNotContainSameSchemaType() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/uberSample.json";
        String localSchemaPath = "./src/test/resources/consumer/uberSample.json";

        MatchResult matchResult = SwaggerMatchResponse.areMatching(providerSchemaPath,localSchemaPath,"/v1/me", HttpMethod.GET);
        Assert.assertFalse(matchResult.isMatching());
        Assert.assertEquals("GET/v1/me has type mismatch for response type 200. Expected: ref, Actual: string",matchResult.getReasonForNotMatching().get(0));
    }

    @Test
    public void shouldNotMatchWhenProviderSchemaResponseDoesNotContainARefPresentInLocal() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/sampleJson.json";
        String localSchemaPath = "./src/test/resources/consumer/sampleJson.json";

        MatchResult matchResult = SwaggerMatchResponse.areMatching(providerSchemaPath,localSchemaPath,"/pet/{petId}", HttpMethod.GET);
        Assert.assertFalse(matchResult.isMatching());
        Assert.assertEquals("GET/pet/{petId} schema ref root->properties does not contain category field.",matchResult.getReasonForNotMatching().get(0));
        Assert.assertEquals("GET/pet/{petId} does not contain  reference category in provider for reponse type 200.",matchResult.getReasonForNotMatching().get(1));
    }

}
