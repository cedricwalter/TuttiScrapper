package com.cedricwalter.tutti;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.NotFound;
import com.jaunt.UserAgent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by cedric on 6/14/2017.
 */
public class TuttiScraper {

    private final double conversionRateToBitcoin;
    private final int numberOfPages;
    private final String tuttiUser;
    private final UserAgent translatorUserAgent;
    private final UserAgent tuttiUserAgent;
    String header = "TITLE|	DESCRIPTION| PRICE|	CURRENCY_CODE| QUANTITY| TAGS|	IMAGE1|	IMAGE2|	IMAGE3|	IMAGE4|	IMAGE5";
    private final BufferedWriter writer;
    private CharSequence tags;

    public TuttiScraper(int numberOfPages, String tuttiUser, double conversionRateToBitcoin) throws IOException {
        this.numberOfPages = numberOfPages;
        this.tuttiUser = tuttiUser;
        this.conversionRateToBitcoin = conversionRateToBitcoin;
        tuttiUserAgent = new UserAgent();
        translatorUserAgent = new UserAgent();
        Path path = Paths.get("./listing.txt");
        writer = Files.newBufferedWriter(path);
    }

    public void process() throws Exception {
        try {
            writer.write(header + "\n");

            translatorUserAgent.visit("https://translate.google.ch/");
            for (int page = 0; page < numberOfPages; page++) {
                processTuttiPage(page, translatorUserAgent);
            }
        } finally {
            writer.flush();
            writer.close();
        }
    }

    private void processTuttiPage(int page, UserAgent translatorUserAgent) throws Exception {
        tuttiUserAgent.visit("https://www.tutti.ch/inserent?o=" + page + "&id=" + tuttiUser);
        Elements links = tuttiUserAgent.doc.findEvery("<h3 class=\"in-title\">").findEvery("<a>");  //find search result links
        for (Element link : links) {
            processTuttiItem(translatorUserAgent, link);
        }
    }

    private void processTuttiItem( UserAgent translatorUserAgent, Element link) throws Exception {
        String href = link.getAt("href");
        tuttiUserAgent.visit(href);

        StringJoiner sb = new StringJoiner("| ");

        sb.add(getTitle());
        sb.add(getDescription());
        sb.add(getPriceInBTC());

        sb.add("BTC"); // Currency
        sb.add("1"); // Quantity
        sb.add(getTags()); // Tags

        List<String> images = getImages();
        images.stream().forEach(sb::add);

        writer.append(sb.toString() + "\n");
    }

    private List<String> getImages() throws NotFound {
        Elements thumbs = tuttiUserAgent.doc.findEvery("< class=\"vi-thumb noselect\">");
        List<String> images = new ArrayList<>(5);
        for (Element thumb : thumbs) {
            String src = thumb.getAt("src");
            images.add(src.replaceAll("/thumbs/", "/images/"));
        }
        if (images.size() < 5) {
            for (int i = images.size(); i < 5; i++) {
                images.add("");
            }
        }
        return images;
    }

    private String getTitle() throws NotFound {
        Element titleDiv = tuttiUserAgent.doc.findFirst("<div class=\"vi-title cf\">");
        List<Element> titleElements = titleDiv.getChildElements();

        return titleElements.get(0).getText();
    }

    private String getDescription() throws Exception {
        Element descriptionDiv = tuttiUserAgent.doc.findFirst("<div class=\"info-column\">");
        List<Element> childElements = descriptionDiv.getChildElements();
        Element descriptionElement = childElements.get(1);
        String description = "\"" + descriptionElement.getText().trim() + "\"";

        return description;
    }

    private String getPriceInBTC() throws NotFound {
        String price;
        try {
            Element locationElementDiv = tuttiUserAgent.doc.findFirst("<div class=\"vi-location\">");
            List<Element> locationElements = locationElementDiv.getChildElements();
            Element locationElement = locationElements.get(0);
            String priceInCHF = locationElement.getChildElements().get(3).getText().trim();
            String cleanedPrice = priceInCHF.replaceAll(".-", "");

            double btc = Integer.parseInt(cleanedPrice) * conversionRateToBitcoin;
            price = String.valueOf(btc);
        } catch (Exception e) {
            return "0.0";
        }
        return price;
    }

    public String getTags() throws NotFound {
        Element breadCrumb = getBreadCrumb();
        List<Element> childElements = breadCrumb.getChildElements();

        String category = "";
        String subcategory = "";
        for (Element childElement : childElements) {
            String aClass = childElement.getAt("class");
            if (aClass.equals("breadcrumb_li bc_vi_cat")) {
                category = childElement.getChildElements().get(0).getText().trim();
            }

            if (aClass.equals("breadcrumb_li bc_vi_subcat last_crumb")) {
                subcategory = childElement.getChildElements().get(0).getText().trim();
            }
        }

        String s = category + "," + subcategory;
        return s.replaceAll("&amp;", "&");
    }

    public Element getBreadCrumb() throws NotFound {
        Iterator var3 = tuttiUserAgent.doc.findEach("<ul class>").iterator();

        while(var3.hasNext()) {
            Element next = (Element) var3.next();
            String aClass = next.getAt("class");
            if(aClass.equals("breadcrumbs_view_ad")) {
                return next;
            }
        }

        return null;
    }


}
