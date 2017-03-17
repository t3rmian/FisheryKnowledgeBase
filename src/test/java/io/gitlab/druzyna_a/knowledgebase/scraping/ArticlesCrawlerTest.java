package io.gitlab.druzyna_a.knowledgebase.scraping;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 *
 * @author Damian Terlecki
 */
public class ArticlesCrawlerTest {

    private static final int SAME_HOST_DELAY_MS = 1000;
    private final String baseUrl = "https://www.google.pl/search?q=spincasting&oq=spincasting";
    private final List<String> tags = Arrays.asList(new String[]{
        "line", "rod", "reel", "spin casting", "dupaniematego", "fish"
    });
    private final int minimalTagsInArticle = 3;
    private final List<String> articles = Collections.synchronizedList(new LinkedList<>());

    @Test
    public void crawlArticles() throws InterruptedException {
        final Thread thread = new Thread(new Crawler.Builder()
                .setBaseUrl(baseUrl)
                .setPagesDepth(3)
                .setThreadPool(8)
                .setScrapeCommand(new SmartScrapeCommand() {
                    @Override
                    public void scrape(Document document) {
                        Elements divs = document.select("div");
                        Elements innerMostDivs = new Elements();
                        divs.stream().filter(div -> div.select(">div").isEmpty()).forEach(div -> innerMostDivs.add(div));;
                        final List<Element> resultDivs = innerMostDivs.stream().filter(div -> {
                            return tags.stream().filter(tag -> div.text().matches(".*" + tag + ".*")).count() >= minimalTagsInArticle;
                        }).collect(Collectors.toList());
                        resultDivs.forEach(div -> articles.add(div.text()));
                    }

                }).createCrawler()
        );
        thread.start();
        thread.join(30 * 1000);
        articles.forEach(s -> System.out.println(s + "\n\n"));
    }

    public abstract static class SmartScrapeCommand implements ScrapeCommand {

        private final List<String> blackList = Arrays.asList(new String[]{
            "facebook", "twitter", "instagram", ".google", "youtube", "&action=edit"
        });
        private final List<String> visitedUrls = new LinkedList<>();
        private final Map<String, Long> visitedHostTimes = new HashMap<>();
        private final Semaphore sem = new Semaphore(1);

        @Override
        public boolean shouldVisit(String url) {
            if (blackList.stream().filter(i -> url.matches(".*" + i + ".*")).findAny().isPresent()) {
                return false;
            }

            synchronized (visitedUrls) {
                if (visitedUrls.stream().filter(u -> sameUrls(u, url)).findAny().isPresent()) {
                    return false;
                }
                visitedUrls.add(url);
            }

            String urlHost = getHostName(url);
            sem.acquireUninterruptibly();
            return visitedHostTimes.entrySet().stream().filter(e -> sameUrls(e.getKey(), urlHost)).findAny().map(e -> {
                sem.release();
                synchronized (e) {
                    if (Instant.now().getEpochSecond() - e.getValue() < 1000) {
                        try {
                            Thread.sleep(SAME_HOST_DELAY_MS);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ArticlesCrawlerTest.class.getName()).log(Level.SEVERE, null, ex);
                            return false;
                        }
                    }
                    sem.acquireUninterruptibly();
                    visitedHostTimes.put(urlHost, Instant.now().getEpochSecond());
                    sem.release();
                }
                return true;
            }).orElseGet(() -> {
                visitedHostTimes.put(urlHost, Instant.now().getEpochSecond());
                sem.release();
                return true;
            });
        }

        private boolean sameHosts(String url1, String url2) {
            return getHostName(url1).equals(getHostName(url2));
        }

        private String getHostName(String url) {
            try {
                URI uri = new URI(url);
                String hostname = uri.getHost();
                if (hostname != null) {
                    return hostname.startsWith("www.") ? hostname.substring(4) : hostname;
                }
                return hostname;
            } catch (URISyntaxException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return url;
        }

        private boolean sameUrls(String url1, String url2) {
            try {
                return new URI(getHashless(url1)).equals(new URI(getHashless(url2)));
            } catch (NullPointerException | URISyntaxException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            }
        }

        private String getHashless(String url) {
            return url.split("#")[0];
        }
    }

}
