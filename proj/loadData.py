import gzip

fileEnd = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
Months = {}


for ii,files in enumerate(fileEnd):
	filename = './nyt-data/nyt_eng_2008'+files+'.gz'
	f = gzip.open(filename, 'r') 
	split = f.read().split('</DOC>')
	Articles = {}
	for idx,article in enumerate(split):
		Articles[idx] = article
	Months[ii] = Articles


'''
#		want to create a classifier: input is unigram of the article and the target is
		whether or not that specific day went up or down
'''
'''
def get_relevent_articles(company):
	# conver the current ticker to an actual company name
	for m in Months.values():
		for a in m.values():
							




# break the articles up into unigrams
# argument: list of companies/tickers that you want to create features for
def article_unigram(companies):
	# first find articles that only talk about a specific company
	for comp in companies:
		# preprocesses the text and only grab the articles that reference this current company
		articles = get_relevent_articles(comp)

'''

for m in Months.values():
	for d in m.values():
		print d
		break
