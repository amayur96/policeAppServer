import sqlite3
import unicodedata
import xlrd
from datetime import datetime
import random

def checkTableExists(dbcon, tableName):
    found = False
    dbcur = dbcon.cursor()
    dbcur.execute("SELECT name FROM sqlite_master WHERE type='table'")
    result = dbcur.fetchall()
    for counter in range(len(result)):
        tbName = unicodedata.normalize('NFKD', result[counter][0]).encode('ascii','ignore')
        if tbName == tableName:
            found = True

    dbcur.close()
    return found

def createTable():
    c.execute('CREATE TABLE crime '
              '(datetime TEXT, location TEXT, description TEXT,'
              'lat REAL, long REAL, precinct TEXT '
              ' )')

def makeTwoLetters(input):
    #prepend the string with '0' if it is of a
    # single letter, else return the same string
    if len(input)==1:
        return '0'+input
    return input

def readProcessCrimeData():
    workBookCrime = xlrd.open_workbook("/Users/ayanmukhopadhyay/Documents/Vanderbilt/CERL/SurvivalAnalysis/spatioTemporalModelingUpdatedGMM/2009Crime.xlsx")
    currentSheetCrime = workBookCrime.sheet_by_index(0)
    pastCrimeRecords = []
    rowCounter=1
    while rowCounter < (currentSheetCrime.nrows - 1):
        try:
            crimeDay = datetime.strptime(str(currentSheetCrime.cell_value(rowCounter,6)[0:14]),"%Y%m%d %H:%M")
            testYear = "2016"
            testMonth = "08"
            testDay = makeTwoLetters(str(crimeDay.day))
            testHour = makeTwoLetters(str(crimeDay.hour))
            testMinute = makeTwoLetters(str(crimeDay.minute))
            testDatetime = testYear + testMonth + testDay + testHour + testMinute
            long = currentSheetCrime.cell_value(rowCounter,14)
            lat = currentSheetCrime.cell_value(rowCounter, 13)
            if random.uniform(0, 1) > 0.5:
                description = "burglary"
            else:
                description = "motor vehicle theft"

            #get precinct
            if random.uniform(0, 1) > 0.5:
                precinct = "South"
            else:
                precinct = "North"
            pastCrimeRecords.append([testDatetime,"Sample Location",description,lat,long,precinct])

        except TypeError:
            print str(rowCounter) + " avoided in recentCrimeCentroids"
        rowCounter+=1

    return pastCrimeRecords

def insertData():
    for input in inputList:
        c.execute('insert into crime values (?,?,?,?,?,?)', input)
    conn.commit()

dbFileName = "/Users/ayanmukhopadhyay/Documents/workspace/policeAppServer/policeAppServer/Databases/test.db"

conn = sqlite3.connect(dbFileName)
c = conn.cursor()

#check if the crime table exists
if not checkTableExists(conn,"crime"):
    createTable()

#read from excel file
inputList = readProcessCrimeData()

#insert data into the crime database.
insertData()









