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


#We can use this to figure things out but I think using bigPriceJumps2008inDow.txt will be easier
#because it gives us the date too
#stockFile = open('Dow.txt', 'r')
priceJumps = open('bigPriceJumps2008inDow.txt', 'r')
lastTicker = ""
for line in priceJumps:
  words = line.split()
  if words[0] != lastTicker:
    lastTicker = words[0]
    query2 = "SELECT company FROM symbol WHERE symbol='"+ words[0]+"';"
    try:
      secondResult = cursor.execute(query2)
    except mysql.connector.Error as err:
      print(err)	

    row2 = cursor.fetchall()

    for company in row2:
      print(company[0])
  else:
    pass
exit()

#close cursor since we don't use it anymore
cursor.close()

#close connection
cnx.close()
