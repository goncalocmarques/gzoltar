/**
 * Copyright (C) 2020 GZoltar contributors.
 *
 * This file is part of GZoltar.
 *
 * GZoltar is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * GZoltar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with GZoltar. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.gzoltar.irfl.topicModeling;

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
