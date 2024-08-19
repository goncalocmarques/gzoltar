package com.gzoltar.irfl.scrapper;



import com.gzoltar.irfl.BugReport;

import java.io.IOException;
import java.util.List;

public class SourceForgeScrapper extends WebScrapper {

    private static final String TITLE_CLASS = "dark title";
    private static final String DESCRIPTION_ID = "markdown_content";

    public SourceForgeScrapper(String url) throws IOException {
        super(url);
    }

    @Override
    public BugReport getBugReport() {
        String title = extractTextByClass(TITLE_CLASS).get(0);
        List<String> description = extractParagraphsByClass(DESCRIPTION_ID);

        return new BugReport(title, description, doc.location());
    }
}
