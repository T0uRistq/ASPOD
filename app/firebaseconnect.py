import pyrebase
import json
from PyQt5.QtWidgets import QMainWindow
import sqlite3
from secondScreen import SecondScreen
from PostAuthorizeScreen import PostAuthorizeScreen
from Ui_AuthWindow import Ui_MainWindow

firebaseconfig = {'apiKey': "AIzaSyDQeQ_YV0ZVeLW--dzDt6XntEwcCEGwTrg",
                  'authDomain': "energotemp-9b8c9.firebaseapp.com",
                  'databaseURL': "https://energotemp-9b8c9-default-rtdb.europe-west1.firebasedatabase.app",
                  'projectId': "energotemp-9b8c9",
                  'storageBucket': "energotemp-9b8c9.appspot.com",
                  'messagingSenderId': "622369709896",
                  'appId': "1:622369709896:web:5b1121856ffdff3a4e9d7d"}
fireBaseApp= pyrebase.initialize_app(firebaseconfig)
database = fireBaseApp.database()

class AuthWindow (Ui_MainWindow):
    # Own methods
    def authorize(self, login):
        # # TODO: Сделать вход по паролю
        # # Переделать чуть код после изменения данных на firebase
        # con = sqlite3.connect("data/users.db")
        # cur = con.cursor()
        # cur.execute("SELECT user FROM USERSTABLE WHERE user LIKE '" + login + "'")
        # rows = cur.fetchall()
        # print(rows)
        # cur.close()
        # print("check2")
        # if len(rows) > 0:
        self.label_3.setText("Авторизация прошла успешно!")
        self.postAuthorize= QMainWindow()
        self.postAuthorizeUi = PostAuthorizeScreen()
        self.postAuthorizeUi.setupUi(self.postAuthorize)
        self.postAuthorizeUi.initEventListeners()
        self.postAuthorize.show()
        return
        # else:
        #     self.label_3.setText("Неверный логин, попробуйте снова")

    def initEventListeners(self):
        self.pushButton.clicked.connect(
            lambda: self.authorize(self.lineEdit.text()))
        self.lineEdit.textChanged.connect(self.clearAuthStatusLabel)
        self.update_btn.mousePressEvent = self.updateDB
        return

    def clearAuthStatusLabel(self):
        self.label_3.clear()

    def updateDB(self,event):
        con = sqlite3.connect("data/users.db")
        cur = con.cursor()
        allUsers = database.child("users").get()
        print(allUsers)
        # Временно
        cur.execute("DROP TABLE IF EXISTS USERSTABLE")
        cur.execute("CREATE TABLE USERSTABLE (id INTEGER PRIMARY KEY , user TEXT, pass_hash TEXT);")
        for i in range(len(allUsers.val())):
            cur.execute(f"INSERT INTO USERSTABLE VALUES ('{i}','{allUsers[i].key()}','string')")
        con.commit()
        con.close()
        print("Done")