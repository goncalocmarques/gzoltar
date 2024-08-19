package com.gzoltar.irfl.scrapper;


import com.gzoltar.irfl.BugReport;

import java.io.IOException;
import java.util.List;


public class ApacheScrapper extends WebScrapper {

    private static final String TITLE_ID = "summary-val";
    private static final String DESCRIPTION_CLASS = "user-content-block";

    public ApacheScrapper(String url) throws IOException {
        super(url);
    }

    @Override
    public BugReport getBugReport() {
        String title = extractTextById(TITLE_ID).get(0);
        List<String> description = extractParagraphsByClass(DESCRIPTION_CLASS);

        return new BugReport(title, description, doc.location());
    }
}
