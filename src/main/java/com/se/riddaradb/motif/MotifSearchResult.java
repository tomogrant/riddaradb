package com.se.riddaradb.motif;

import java.util.List;
import java.util.Set;

public class MotifSearchResult {
    private Integer searchResultId;
    private List<Integer> searchResultPath;

    public MotifSearchResult(Integer searchResultId, List<Integer> searchResultPath) {
        this.searchResultId = searchResultId;
        this.searchResultPath = searchResultPath;
    }

    public Integer getSearchResultId() {
        return searchResultId;
    }

    public void setSearchResultId(Integer searchResultId) {
        this.searchResultId = searchResultId;
    }

    public List<Integer> getSearchResultPath() {
        return searchResultPath;
    }

    public void setSearchResultPath(List<Integer> searchResultPath) {
        this.searchResultPath = searchResultPath;
    }
}
