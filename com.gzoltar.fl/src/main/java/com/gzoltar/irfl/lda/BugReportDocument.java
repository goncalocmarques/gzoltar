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
package com.gzoltar.irfl.lda;

import com.gzoltar.irfl.BugReport;
import com.gzoltar.irfl.nlp.NLPParser;
import com.gzoltar.irfl.nlp.ProcessedLine;
import com.gzoltar.irfl.scrapper.WebScrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.gzoltar.irfl.scrapper.ScrapperType.getScrapperFromUrl;


public class BugReportDocument implements Document {
    private final NLPParser parser;

    public BugReportDocument(NLPParser parser) {
        this.parser = parser;
    }

    @Override
    public List<ProcessedLine> createDocuments(Object input) throws IOException {
        if (!(input instanceof String)) {
            throw new IllegalArgumentException("Expected a URL string for BugReportDocument");
        }
        String url = (String) input;
        WebScrapper scrapper = getScrapperFromUrl(url);
        BugReport bugReport = scrapper.getBugReport();
        List<String> tokensToProcess = new ArrayList<>(bugReport.getDescription());
        List<String> processedTokens = parser.processTokens(tokensToProcess);
        List<ProcessedLine> result = new ArrayList<>();
        result.add(new ProcessedLine(url, processedTokens));
        return result;
    }
}
