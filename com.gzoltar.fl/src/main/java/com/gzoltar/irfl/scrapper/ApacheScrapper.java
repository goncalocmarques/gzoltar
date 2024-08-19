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
