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