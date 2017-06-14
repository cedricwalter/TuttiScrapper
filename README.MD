# TuttiScrapper

TuttiScrapper for Tutti.ch free classified ads. Tutti.ch has no API to get your ads. 
This code go through HTML and output a CSV that can be imported later in OpenBazaar. 

# Usage
1. Go to page "Alle Inserate von xxxx" where xxxx is your pseudo. E.g https://www.tutti.ch/inserent?id=0000000000000000000 Note the number 0000000000000000
2. At the bottom of the page click "»" and note the last page number. E.g 44
3. Check the conversion rate with https://www.google.ch/search?q=1+CHF+in+BTC E.g 0.00037 Bitcoin for 1 CHF

call the program with

`java -jar TuttiScrapper-1.0.jar 44 0000000000000000 0.00037`

CSV is outputted to console

## TODO
* use google translate to support description in multiple language
* add support for tags based on tutti categories
* encoding issue in scraper