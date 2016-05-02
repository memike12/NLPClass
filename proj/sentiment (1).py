import csv
import ast

def sentimentAnalysis (companyList):

	##create the positive and negative lexicons
	pos = open('pos.txt', 'r')
	neg = open('neg.txt', 'r')
	posList = pos.readlines()
	negList = neg.readlines()
	posList = map(str.strip, posList)
	negList = map(str.strip, negList)
	
	good=0  ##for positive sentiment
	bad=0  ##for negative sentiment
	toReturn = [[]] ##double array: each company has a sentiment score for each date attached to them
	
	for company in companyList:
		companyData = [] ##array with sentiment scores for each date for a given company
		for date in company:
			for sentence in date[3]:
				for word in sentence:
					if word.lower() in (name.lower() for name in pos):
						good += 1
					elif word.lower() in (name.lower() for name in neg):
						bad += 1
			if good > bad:
				companyData.append(good)
			elif bad > good:
				companyData.append(bad)
			else:
				companyData.append(0)
		toReturn.append(companyData)	
		
	print toReturn



					
