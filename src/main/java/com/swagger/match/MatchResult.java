package com.swagger.match;

import java.util.List;

/**
 * Created by bsneha on 22/07/17.
 */
public class MatchResult {

    public Boolean isMatching() {
        return isMatching;
    }

    public void setIsMatching(Boolean matching) {
        isMatching = matching;
    }

    private Boolean isMatching;

    public List<String> getReasonForNotMatching() {
        return reasonForNotMatching;
    }

    public void setReasonForNotMatching(List<String> reasonForNotMatching) {
        this.reasonForNotMatching = reasonForNotMatching;
    }

    private List<String> reasonForNotMatching;

    public String getQueryMethodPath() {
        return queryMethodPath;
    }

    public void setQueryMethodPath(String queryMethodPath) {
        this.queryMethodPath = queryMethodPath;
    }

    private String queryMethodPath;
}
