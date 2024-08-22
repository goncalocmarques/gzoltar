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
package com.gzoltar.irfl.topicModeling.scrapper;

import com.gzoltar.irfl.topicModeling.BugReport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class WebScrapper {

    protected final Document doc;

    public WebScrapper(String url) throws IOException {
        this.doc = Jsoup.connect(url).get();
    }

    public abstract BugReport getBugReport() throws IOException;

    protected List<String> extractTextById(String id) {
        Element element = doc.getElementById(id);
        if (element != null) {
            return element.getAllElements().eachText();
        }
        return Collections.emptyList();
    }

    protected List<String> extractTextByClass(String className) {
        return extractTextByClassInternal(className, false);
    }

    protected List<String> extractParagraphsByClass(String className) {
        return extractTextByClassInternal(className, true);
    }

    private List<String> extractTextByClassInternal(String className, boolean firstOnly) {
        Elements elements = doc.getElementsByClass(className);
        if (elements.isEmpty()) {
            return Collections.emptyList();
        }
        if (firstOnly) {
            Element firstElement = elements.first();
            if (firstElement != null) {
                return firstElement.getAllElements().eachText();
            } else {
                return Collections.emptyList();
            }
        } else {
            return elements.eachText();
        }
    }
}
