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

import java.io.IOException;

public enum ScrapperType {
    SOURCEFORGE {

        @Override
        public boolean matches(String url) {
            return url.contains("sourceforge.net");
        }

        public WebScrapper getWebScrapper(String url) throws IOException {
            return new SourceForgeScrapper(url);
        }

    },
    GITHUB {

        @Override
        public boolean matches(String url) {
            return url.contains("github.com");
        }

        public WebScrapper getWebScrapper(String url) throws IOException {
            return new GitHubScrapper(url);
        }

    },
    APACHE {

        @Override
        public boolean matches(String url) {
            return url.contains("issues.apache.org");
        }

        public WebScrapper getWebScrapper(String url) throws IOException {
            return new ApacheScrapper(url);
        }

    };
    // TODO: Extend support to include additional websites for hosting bug reports

    public abstract boolean matches(String url);
    public abstract WebScrapper getWebScrapper(String url) throws IOException;

    public static WebScrapper getScrapperFromUrl(String url) throws IOException {
        for (ScrapperType type : values()) {
            if (type.matches(url)) {
                return type.getWebScrapper(url);
            }
        }
        throw new IllegalArgumentException("Unsupported URL: " + url);
    }
}