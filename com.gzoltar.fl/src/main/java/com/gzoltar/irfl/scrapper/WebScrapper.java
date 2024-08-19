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
package com.gzoltar.irfl.scrapper;


import com.gzoltar.irfl.BugReport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class WebScrapper {

    protected Document doc;

    public WebScrapper(String url) throws IOException {
        this.doc = Jsoup.connect(url).get();
    }

    public abstract BugReport getBugReport() throws IOException;

    protected List<String> extractTextById(String id) {
        return Optional.of(Objects.requireNonNull(Objects.requireNonNull(doc.getElementById(id)).getAllElements().eachText())).orElse(List.of());
    }

    protected List<String> extractTextByClass(String className) {
        return extractTextByClass(className, false);
    }

    protected List<String> extractParagraphsByClass(String className) {
        return extractTextByClass(className, true);
    }

    private List<String> extractTextByClass(String className, boolean firstOnly) {
        return firstOnly ?
                Objects.requireNonNull(doc.getElementsByClass(className).first()).getAllElements().eachText() :
                doc.getElementsByClass(className).eachText();

    }
}
