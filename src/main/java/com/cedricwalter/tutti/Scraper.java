package com.cedricwalter.tutti;

/**
 * Created by cedric on 6/14/2017.
 */
public class Scraper {

    public static void main(String[] args) throws Exception {
        int numberOfPages = Integer.valueOf(args[0]);
        String tuttiUser = args[1];
        double conversionRateToBitcoin = Double.parseDouble(args[2]);

        new TuttiScraper(numberOfPages, tuttiUser, conversionRateToBitcoin).process();
    }

}
