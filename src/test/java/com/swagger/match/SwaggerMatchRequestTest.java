package com.swagger.match;

import io.swagger.models.HttpMethod;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by bsneha on 27/07/17.
 */
public class SwaggerMatchRequestTest {

    @Test
    public void shouldBeAbleToCompareResponseSchemaForAllPathsAndReturnErrorIfPathMismatch() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/sampleJson.json";
        String localSchemaPath = "./src/test/resources/consumer/sampleJson.json";

        MatchResult matchResult = SwaggerMatchRequest.areMatching(providerSchemaPath, localSchemaPath);
        Assert.assertFalse(matchResult.isMatching());
        List<String> reasonForNotMatching = matchResult.getReasonForNotMatching();
        Assert.assertEquals("DELETE/store/order/{orderId} not found",reasonForNotMatching.get(0));
        Assert.assertEquals("GET/pet/findByTagsExtra not found",reasonForNotMatching.get(1));
    }

    @Test
    public void shouldBeAbleToCompareResponseSchemaForSinglePathAndReturnErrorIfHTTPMethodNotFound() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/sampleJson.json";
        String localSchemaPath = "./src/test/resources/consumer/sampleJson.json";

        MatchResult matchResult = SwaggerMatchRequest.areMatching(providerSchemaPath, localSchemaPath,"/store/order/{orderId}", HttpMethod.DELETE);
        Assert.assertFalse(matchResult.isMatching());
        List<String> reasonForNotMatching = matchResult.getReasonForNotMatching();
        Assert.assertEquals("DELETE/store/order/{orderId} not found",reasonForNotMatching.get(0));
    }

    @Test
    public void shouldBeAbleToCompareResponseSchemaForSinglePathAndReturnErrorIfPathNotFound() throws IOException {
        String providerSchemaPath = "./src/test/resources/provider/sampleJson.json";
        String localSchemaPath = "./src/test/resources/consumer/sampleJson.json";

        MatchResult matchResult = SwaggerMatchRequest.areMatching(providerSchemaPath, localSchemaPath,"/pet/findByTagsExtra", HttpMethod.GET);
        Assert.assertFalse(matchResult.isMatching());
        List<String> reasonForNotMatching = matchResult.getReasonForNotMatching();
        Assert.assertEquals("Path(s): /pet/findByTagsExtra not found.",reasonForNotMatching.get(0));
    }
}
