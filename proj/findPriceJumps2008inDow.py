import mysql.connector
from mysql.connector import errorcode
import os
import Queue

try:
  cnx = mysql.connector.connect(user='m162166',
                                password = 'm162166',
                                host = 'zee.cs.usna.edu',
                                database='m164488')

#check for errors
except mysql.connector.Error as err:
  if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
    print("Something is wrong with your user name or password")
  elif err.errno == errorcode.ER_BAD_DB_ERROR:
    print("Database does not exist")
  else:
    print(err)
  print("<p>Fix your code or Contact system admin</p></body></html>")
  quit()

#create cursor to send queries
cursor = cnx.cursor()

stockFile = open('Dow.txt', 'r')
for symbol in stockFile:

  #this prints out all of the data we have on a stock
  query2 = "SELECT * FROM price WHERE symbol='"+ symbol.strip()+"' AND (date_ex BETWEEN '2008-01-01' AND '2008-12-31');"
  try:
    secondResult = cursor.execute(query2)
  except mysql.connector.Error as err:
    print(err)	

  row2 = cursor.fetchall()
  #a cursor always returns a tuple so you need to go to an index
  x=0
  closeSum = 0
  closeAvg = 0
  closeQ = Queue.Queue()

  #This makes a 5 day SMA of close prices and checks for jumps above or below that
  for (symbol, date_ex, open_p, high_p, low_p, close_p, volume, adj_close_p) in row2:
    closeQ.put((symbol, date_ex, open_p, high_p, low_p, close_p, volume, adj_close_p))
    if closeQ.qsize() > 5:
      closeQ.pop()
    if closeQ.qsize() == 5:
      closeAvg = float((float(closeQ.get([0])[5])+float(closeQ.get([1])[5])+float(closeQ.get([2])[5])+float(closeQ.get([3])[5])+float(closeQ.get([4])[5]))/5)
      if close_p > 1.05*closeAvg: 
        print(symbol + " " + str("%.2f" % (float(close_p)/float(closeAvg+.001)*100)) + "% above average in price on {:%d %b %Y}".format(date_ex, close_p, closeAvg))
      if close_p < .95*closeAvg: 
        print(symbol + " " + str("%.2f" % (float(close_p)/float(closeAvg+.001)*100)) + "% below average in price on {:%d %b %Y}".format(date_ex, close_p, closeAvg))

#close cursor since we don't use it anymore
cursor.close()

#commit the transaction
#cnx.commit()  #this is really important otherwise all changes lost
#close connection
cnx.close()
