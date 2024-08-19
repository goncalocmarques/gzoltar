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
