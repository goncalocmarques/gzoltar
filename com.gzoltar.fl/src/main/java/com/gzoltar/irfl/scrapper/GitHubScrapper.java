package com.gzoltar.irfl.scrapper;



import com.gzoltar.irfl.BugReport;

import java.io.IOException;
import java.util.List;


public class GitHubScrapper extends WebScrapper {

    private static final String TITLE_CLASS = "js-issue-title markdown-title";
    private static final String DESCRIPTION_CLASS = "user-select-contain";

    public GitHubScrapper(String url) throws IOException {
        super(url);
    }

    @Override
    public BugReport getBugReport() {
        String title = extractTextByClass(TITLE_CLASS).get(0);
        List<String> description = extractParagraphsByClass(DESCRIPTION_CLASS);

        return new BugReport(title, description, doc.location());
    }
}
