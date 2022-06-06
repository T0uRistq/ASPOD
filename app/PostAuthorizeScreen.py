import json

import pyrebase
from PyQt5.QtWidgets import QMainWindow, QMessageBox
from PyQt5.QtGui import QPixmap,QIcon
from Ui_PostAuthorize import Ui_MainWindow
from jsonadd import addToJson
import sqlite3

from secondScreen import SecondScreen

executors = [{'executor': 'ООО «НИИПГАЗА»',
              'postal': '450059, Россия, Республика Башкортостан, г. Уфа, проспект Октября, дом 43/5, офис Б',
              'cert': '№ ЛНК-053А0002 от 02.03.2021 г'},
             {'executor': 'ООО «Энергоэксперт»',
              'postal': '197342, г. Санкт-Петербург, наб. Черной речки, д.41, к.2, лит. Б, пом.7',
              'cert': '№ 89А112162 от 14.02.2020 г.'}]
clients = [{'client': 'ООО «Газпром трансгаз Казань»',
            'postal': '450059, Россия, Республика Башкортостан, г. Уфа, проспект Октября, дом 43/5, офис Б'},
           {'client': 'ООО «Газпром трансгаз Югорск»', 'postal': '628260, РФ, г. Югорск, ул. Мира, 15'}]
firebaseconfig = {'apiKey': "AIzaSyDQeQ_YV0ZVeLW--dzDt6XntEwcCEGwTrg",
                  'authDomain': "energotemp-9b8c9.firebaseapp.com",
                  'databaseURL': "https://energotemp-9b8c9-default-rtdb.europe-west1.firebasedatabase.app",
                  'projectId': "energotemp-9b8c9",
                  'storageBucket': "energotemp-9b8c9.appspot.com",
                  'messagingSenderId': "622369709896",
                  'appId': "1:622369709896:web:5b1121856ffdff3a4e9d7d"}

fireBaseApp = pyrebase.initialize_app(firebaseconfig)
database = fireBaseApp.database()


class PostAuthorizeScreen(Ui_MainWindow):
    # Own methods
    # def __init__(self):
    fileContent = str()

    def initEventListeners(self):
        print("zero")
        self.newReport.clicked.connect(self.generateNewReport)
        file = open("data/JSONstring.txt", "w+")
        fileContent = file.read()
        fileJson=dict()
        try:
            fileJson = json.loads(fileContent)
        except Exception:
            print("first")
            messageBox = QMessageBox()
            messageBox.setIcon(QMessageBox.Warning)
            messageBox.setWindowTitle("Отправка файла")
            messageBox.setText("Заполните новый отчет, т.к. файл либо пустой либо удален")
            messageBox.setStandardButtons(QMessageBox.Ok)
            messageBox.exec_()
            return
        print(fileJson)
        if "date" in fileJson:
            messageBox = QMessageBox()
            messageBox.setIcon(QMessageBox.Information)
            messageBox.setWindowTitle("Отправка файла")
            messageBox.setText("Отправьте отчет")
            messageBox.setStandardButtons(QMessageBox.Ok)
            messageBox.exec_()
            print("keys")
            self.reportSendButton.setEnabled(True)
        elif len(fileContent) > 0:
            self.continueButton.setEnabled(True)
        self.reportSendButton.clicked.connect(lambda: self.sendReport(fileContent))

    def sendReport(self,file):
        try:
            # Отправка протокола
            database.child("protocols").child("1").set(file)
            messageBox = QMessageBox()
            icon = QIcon("imgs/checked.png")
            messageBox.setIconPixmap(icon.pixmap(60,60))
            messageBox.setWindowTitle("Отправка файла")
            messageBox.setText("Протокол успешно отправлен на сервер")
            messageBox.setStandardButtons(QMessageBox.Ok)
            messageBox.exec_()
        except Exception:
            messageBox = QMessageBox()
            messageBox.setIcon(QMessageBox.Critical)
            messageBox.setWindowTitle("Отправка файла")
            messageBox.setText("Произошла какая-то ошибка при отправке файла")
            messageBox.setStandardButtons(QMessageBox.Ok)
            messageBox.exec_()
    def continueReport(self):
        return

    # def nextScreen(self):
    #     global jsonstring
    #     jsonstring = {"executor": self.comboBox.currentIndex(),"client": self.comboBox_2.currentIndex(),"FIO": self.comboBox_3.currentIndex()}
    #     self.chooseMethods = QMainWindow()
    #     self.chooseMethodsUi = ThirdScreen()
    #     self.chooseMethodsUi.setupUi(self.chooseMethods)
    #     self.chooseMethodsUi.initEventListeners()
    #     self.chooseMethods.show()

    def generateNewReport(self):
        self.chooseExecutors = QMainWindow()
        self.chooseExecutorsUi = SecondScreen()
        self.chooseExecutorsUi.setupUi(self.chooseExecutors)
        self.chooseExecutorsUi.initEventListeners()
        self.chooseExecutors.show()