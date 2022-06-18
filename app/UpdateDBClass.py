import json
import math
from PyQt5 import QtCore
from PyQt5.QtWidgets  import QFileDialog, QWidget
import pandas as pd
import pyrebase

firebase = pyrebase.initialize_app({'apiKey': "AIzaSyDQeQ_YV0ZVeLW--dzDt6XntEwcCEGwTrg",
                                    'authDomain': "energotemp-9b8c9.firebaseapp.com",
                                    'databaseURL': "https://energotemp-9b8c9-default-rtdb.europe-west1.firebasedatabase.app",
                                    'projectId': "energotemp-9b8c9",
                                    'storageBucket': "energotemp-9b8c9.appspot.com",
                                    'messagingSenderId': "622369709896",
                                    'appId': "1:622369709896:web:5b1121856ffdff3a4e9d7d"})
  
db = firebase.database()

def add_tools_db(self):
    df = pd.read_excel('tables/tools.xlsx')
    for i in range(len(df)):
        json = "{"
        for j in range(df.iloc[i].size - 1):
            tmp = str(df.iloc[i][j + 1])
            cnt = 0
            for k in range(len(tmp)):
                if (tmp[k] == '"'):
                    if (cnt == 0):
                        tmp = tmp[: k] + '«' + tmp[k + 1:]
                    else:
                        tmp = tmp[: k] + '»' + tmp[k + 1:]
                    cnt ^= 1
            json += '"' + str(j) + '" : "' + tmp + '", '
        json = json[:-2]
        json += "}"
        db.child('tools').child(i + 1).set(json)

def add_users_db(self):
    df = pd.read_excel('tables/qualifications.xlsx').drop(0)
    for row in range(len(df)):
        modulo = row % 5
        if (modulo == 0):
            person = df.iloc[row][1].strip('" ')
        json = "{"
        for i in range(len(df.iloc[row]) - 4):
            json += '"' + str(i) + '" : "'
            if (modulo == 1):
                json += str(df.iloc[row][i + 4]).replace(".", ",")
            elif (modulo == 2):
                if (type(df.iloc[row][i + 4]) == float):
                    json += pd.Timestamp.now().strftime('%d.%m.%Y')
                else:
                    json += (df.iloc[row][i + 4] + pd.DateOffset(years=3)).strftime('%d.%m.%Y')
            else:
                tmp = str(df.iloc[row][i + 4])
                cnt = 0
                for k in range(len(tmp)):
                    if (tmp[k] == '"'):
                        if (cnt == 0):
                            tmp = tmp[: k] + '«' + tmp[k + 1:]
                        else:
                            tmp = tmp[: k] + '»' + tmp[k + 1:]
                        cnt ^= 1
                json += tmp
            json += '", '
        json = json[: -2] + "}"
        db.child('users').child(person).child(modulo).set(json)