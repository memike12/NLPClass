import mysql.connector
import datetime
import unicodedata as ud
import re
import csv
import ast

def getStockJumps(cursor, symbol, beginDate, endDate):
        getData = "SELECT * FROM price WHERE symbol='"+ symbol+"' AND (date_ex BETWEEN '"+str(beginDate)+"' AND '"+str(endDate)+"');"
        cursor.execute(getData)
	stockData = cursor.fetchall()
        fiveDayList = []
        interestingDays = []

        for (symbol, date_ex, open_p, high_p, low_p, close_p, volume,  adj_close_p) in stockData:
                fiveDayList.append((symbol, date_ex, open_p, high_p, low_p, close_p, volume, adj_close_p))
                changeSymbolToName = "Select company from symbol where symbol='"+symbol+"';"
                cursor.execute(changeSymbolToName)
                companyName = cursor.fetchall()
                #print(companyName)
                if len(fiveDayList) == 5:
                        ##########
                        #finds the price jumps
                        closeAvg = float((float(fiveDayList[0][5])+float(fiveDayList[1][5])+float(fiveDayList[2][5])+float(fiveDayList[3][5])+float(fiveDayList[4][5]))/5)
                        if close_p > 1.05*closeAvg: 
                                interestingDays.append(symbol + " #" + companyName[0][0] + "# " + str("%.2f" % (float(close_p)/float(closeAvg+.001)*100-100)) + "% up price {:%Y%m%d}".format(date_ex))
                        if close_p < .95*closeAvg:
                                interestingDays.append(symbol+ " #" + companyName[0][0] + "# " + str("%.2f" % ((float(close_p)/float(closeAvg+.001)*100-100)*-1)) + "% down price {:%Y%m%d}".format(date_ex))
                        ###########
                        #find volume jumps
                        if volume > 0:
                                #5 day volume sum
                                volAvg = float((float(fiveDayList[0][6])+float(fiveDayList[1][6])+float(fiveDayList[2][6])+float(fiveDayList[3][6])+float(fiveDayList[4][6]))/5)
                                #print volAvg
                                if volume > 1.5*volAvg:
                                        interestingDays.append(symbol + " #" + companyName[0][0] + "# " + str("%.2f" % (float(volume)/float(volAvg)*100)) + "% up volume {:%Y%m%d}".format(date_ex))
                        fiveDayList.pop(0)
        return interestingDays

def getCompanyInfo(cursor, symbol, beginDate, endDate):
        getData =  "SELECT * FROM price WHERE symbol='"+ symbol+"' AND (date_ex BETWEEN '"+str(beginDate)+"' AND '"+str(endDate)+"');"
        cursor.execute(getData)
        stockData = cursor.fetchall()
        stockDataStandard = []
        for (symbol, date_ex, open_p, high_p, low_p, close_p, volume,  adj_close_p) in stockData:
                stockDataStandard.append([symbol, "{:%Y%m%d}".format(date_ex), open_p, high_p, low_p, close_p, volume, adj_close_p])
        return stockDataStandard

def findArticles(criticalDays):
        #####
        # Read in files
        fileEnd = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
        Months = {}
        for ii,files in enumerate(fileEnd):
                filename = './nyt_eng_2008'+files
                f = open(filename, 'r') 
                split = f.read().split('</DOC>')
                Articles = []
                for idx,article in enumerate(split):
                        Articles.append(article)
                Months[ii] = Articles
        ticToName= {'MMM': ['3M'], 'KO':['Coca-Cola','Coke'], 'DD':['du Pont de Nemours'], 'JPM':['J. P. Morgan', 'J P Morgan', 'Morgan & Chase', 'Morgan and Chase'], 'MCD':["McDonald's", "McDonalds"], 'AA':['Alcoa'], 'MRK':['Merck'], 'AXP':['American Express'], 'XOM':['Exxon'], 'AIG':['American International'], 'GE':['General Electric', 'GE'], 'MSFT':['Microsoft'], 'GM':['General Motors', 'GM'], 'PFE':['Pfizer'], 'BAC':['Bank of America'], 'HPQ':['Hewlett', 'Hewlett-Packard', 'HP'], 'PG':['Procter and Gamble','Procter & Gamble'], 'BA':['Boeing'], 'HD':['Home Depot'], 'UTX':['United Technologies'], 'CAT':['Caterpillar'], 'INTC':['Intel'], 'VZ':['Verizon'], 'CVX':['Chevron'], 'IBM':['International Business Machines', 'IBM'], 'WMT':['Wal Mart','Wal-Mart'], 'C':['Citigroup'], 'JNJ':['Johnson & Johnson', 'Johnson and Johnson'], 'DIS':['Disney']}
        s ="#"
        CompanyNameAndDate = []
        for stocks in criticalDays:
                StockNameAndDate = []
                for interestingDay in stocks:                        
                        companyName = re.search(re.escape(s)+"(.*?)"+re.escape(s),interestingDay).group(1)
                        interestingDay = interestingDay.split()
                        ticker = interestingDay[0]
                        date = interestingDay[len(interestingDay)-1]
                        year = date[0] + date[1] + date[2] + date[3]
                        month = date[4] + date[5]
                        day = date[6] + date[7]
                        twoWeeks = datetime.timedelta(14)
                        formatedDate = datetime.date(int(year), int(month), int(day))
                        relivantArticles = []
                        print ticker, formatedDate
                        ii = 0
                        while ii < 8:
                                datelookingfor = formatedDate - datetime.timedelta(ii)
                                allMonthsArticles = Months[(datelookingfor.month)-1]
                                for article in allMonthsArticles:
                                        header = article.splitlines()[0]
                                        if len(article) > 1:
                                                y = 0
                                                while len(header) < 24:
                                                        header = article.splitlines()[y]
                                                        y = y + 1
                                                articleDay = header[23] + header[24]
                                                if int(articleDay) == datelookingfor.day:
                                                        sentences = article.split('</P>')
                                                        for sents in sentences:
                                                                Names = ticToName[str(ticker)]
                                                                for name in Names:
                                                                        pattern = re.compile(name)
                                                                        if pattern.search(sents):
                                                                                relivantArticles.append(sents)                               
                                ii+=1
                        StockNameAndDate.append( [ticker, companyName, date, relivantArticles])
                print "Done with ", ticker
                CompanyNameAndDate.append(StockNameAndDate)
        return CompanyNameAndDate

def getCompanyNameFromSymbol(cursor, symbol, beginDate, endDate):
        getData = "SELECT distinct company, symbol FROM symbol WHERE symbol='"+ symbol+"';"
        cursor.execute(getData)
	stockData = cursor.fetchall()
        for symbol in stockData:
                print symbol

def sentimentAnalysis (companyList):
	##create the positive and negative lexicons
	pos = open('pos.txt', 'r')
	neg = open('neg.txt', 'r')
	posList = pos.readlines()
	negList = neg.readlines()
	posList = map(str.strip, posList)
	negList = map(str.strip, negList)
	posLower,negLower  = [], []
	for word in posList:
		posLower.append(word.lower())
	for word in negList:
		negLower.append(word.lower())

	tot = 0
	good=0  ##for positive sentiment
	bad=0  ##for negative sentiment
	toReturn = [[]] ##double array: each company has a sentiment score for each date attached to them
	
	for i,company in enumerate(companyList):
		print i
		companyData = [] ##array with sentiment scores for each date for a given company
		for date in company:
			for article in date[3]:
				good=bad=tot=0
				article = article.replace(',', '').replace('.', '').replace(':','').replace('"','').split()
				for word in article:			
					if word.lower() in posLower:
						good += 1

						tot +=1
					elif word.lower() in negLower:
						bad -= 1
						tot +=1
			if good > abs(bad):
				companyData.append([date[0], date[2], float(good)/float(tot)])
			elif abs(bad) > good:
				companyData.append([date[0], date[2], float(bad)/float(tot)])
			else:
				companyData.append([date[0], date[2], 0])
		toReturn.append(companyData)	
		
	return toReturn

