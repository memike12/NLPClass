import mysql.connector
import datetime
import findMovementFunct as funct
import numpy as np
import pickle

cnx = mysql.connector.connect(user='m162166',
                                password = 'm162166',
                                host = 'zee.cs.usna.edu',
                                database='m164488')

#create cursor to send queries
cursor = cnx.cursor()

#From the file specified, find all the interesting stock days
stockTickerFile = open('Dow2008.txt', 'r')
beginDate = datetime.date(2008, 01, 01)
endDate = datetime.date(2008, 12, 31)
indexInfo = []
interestingDays = []
sentList = []
'''
for i, ticker in enumerate(stockTickerFile):
        ####
        # This gets the interesting days and returns them as a list interestingDays[ticker][day]string. To pull things from the string you should split first eg. interestingDays[1][1].split()[0]
        ##### Actual format for the string after split is:
        ####### 0=ticker,1=%, 2=up or down, 3=price or vol, 4=Date
        companyInterestingDays =  np.asarray(funct.getStockJumps(cursor, ticker.strip(), beginDate, endDate))
        interestingDays.append(companyInterestingDays)

        ####
        # This gets all of the stock data for the date range and uses the indexInfo list and makes a list for each ticker and then a list inside that for each day so indexInfo[ticker][day's data][open, close, vol, ect.]
        ###### Actual format for the day's data is:
        ####### 0=Ticker, 1=Date, 2=Open Price, 3=High Price, 4=Low Price, 5=Close Price, 6=Volume, 7=Adj Close Price
        #company = np.asarray(funct.getCompanyInfo(cursor, ticker.strip(), beginDate, endDate))
        #indexInfo.append(company)

        ####
        #This takes the interestingDays list found in getStockJumps and returns articles relivant to those days
        #funct.findArticles(interestingDays)
        #funct.getCompanyNameFromSymbol(cursor, ticker.strip(), beginDate, endDate)


#####
# Finds the articles that talk about the company in the week proceeding the jump 
print "Done finding good days"
sentList = funct.findArticles(interestingDays)
print "done"

ArticlesForDays = open('relivantArticles.txt', 'wb')
print "writing"
pickle.dump(sentList, ArticlesForDays)
print "done"

#####
# Uses the articles found for each jump and finds the sentiment of the articles
sent = []
print "Sentalizing"
ArticlesForDays = open('relivantArticles.txt', 'rb')


sentList = pickle.load(ArticlesForDays)
sent = funct.sentimentAnalysis(sentList)

#save the sentiment list so we don't ever have to compute that again
ArticlesForDays = open('sentScore.txt', 'wb')
pickle.dump(sent, ArticlesForDays)
'''

#opens the sentiment of the days
ArticlesForDays = open('sentScore.txt', 'rb')
sent = pickle.load(ArticlesForDays)

cursor.close()

cnx.close()




