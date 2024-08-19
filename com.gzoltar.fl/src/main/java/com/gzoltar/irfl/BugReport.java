package com.gzoltar.irfl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BugReport {
    private String title;
    private List<String> description;
    private String url;

    public BugReport(String title, List<String> description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    Map<String, String> toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description.toString());
        map.put("url", url);
        return map;
    }
}
