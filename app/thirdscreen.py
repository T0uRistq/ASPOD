import datetime
import sys

import pyrebase
from PyQt5.QtGui import QIcon
from PyQt5.QtWidgets import QMessageBox, QMainWindow, QApplication

import ReportGenerate
from Ui_ControlMethods import Ui_MainWindow
from jsonadd import addToJson
import secondScreen
# Const
QTY_OF_CHECKBOXES = 7

executors = [{'executor': 'ООО «НИИПГАЗА»',
              'postal': '450059, Россия, Республика Башкортостан, г. Уфа, проспект Октября, дом 43/5, офис Б',
              'cert': '№ ЛНК-053А0002 от 02.03.2021 г'},
             {'executor': 'ООО «Энергоэксперт»',
              'postal': '197342, г. Санкт-Петербург, наб. Черной речки, д.41, к.2, лит. Б, пом.7',
              'cert': '№ 89А112162 от 14.02.2020 г.'}]
clients = [{'client': 'ООО «Газпром трансгаз Казань»',
            'postal': '450059, Россия, Республика Башкортостан, г. Уфа, проспект Октября, дом 43/5, офис Б'},
           {'client': 'ООО «Газпром трансгаз Югорск»', 'postal': '628260, РФ, г. Югорск, ул. Мира, 15'}]

devices= ["Фильтр высокого давления, инв. № 136033","Пылеуловитель, инв. № 135783"]

firebaseconfig = {'apiKey': "AIzaSyDQeQ_YV0ZVeLW--dzDt6XntEwcCEGwTrg",
                  'authDomain': "energotemp-9b8c9.firebaseapp.com",
                  'databaseURL': "https://energotemp-9b8c9-default-rtdb.europe-west1.firebasedatabase.app",
                  'projectId': "energotemp-9b8c9",
                  'storageBucket': "energotemp-9b8c9.appspot.com",
                  'messagingSenderId': "622369709896",
                  'appId': "1:622369709896:web:5b1121856ffdff3a4e9d7d"}

fireBaseApp = pyrebase.initialize_app(firebaseconfig)
database = fireBaseApp.database()

class ThirdScreen(Ui_MainWindow):
    # Own methods
    val = []
    def initEventListeners(self):
        self.comboBox.addItems(devices)
        self.dateEdit.setDate(datetime.datetime.now())
        self.pushButton.clicked.connect(self.getState)
    def getState(self):
        checkBoxes = [self.checkBox_1,self.checkBox_2,self.checkBox_3,self.checkBox_4,self.checkBox_5,self.checkBox_6,self.checkBox_7]
        for i, item in enumerate(checkBoxes):
            if (item.isChecked()):
                print(i)
                self.val.append(i)
        # Combobox в json
        secondScreen.jsonstring["methods"] = self.val
        self.comboBoxValue = self.comboBox.currentIndex()
        secondScreen.jsonstring["device"] = self.comboBoxValue
        secondScreen.jsonstring["date"] = self.dateEdit.date().toPyDate().strftime("%d.%m.%Y")
        print(secondScreen.jsonstring)
        file = open("data/JSONstring.txt", "w")
        resultJson = addToJson(secondScreen.jsonstring)
        file.write(resultJson)
        if "date" in resultJson:
            messageBox = QMessageBox()
            messageBox.setIcon(QMessageBox.Information)
            messageBox.setWindowTitle("Отправка файла")
            messageBox.setText("Данные сохранены")
            messageBox.setStandardButtons(QMessageBox.Ok)
            messageBox.exec_()
            messageBoxsendReport = QMessageBox()
            messageBoxsendReport.setIcon(QMessageBox.Information)
            messageBoxsendReport.setWindowTitle("Отправка файла")
            messageBoxsendReport.setText("Отправить отчет сейчас?")
            messageBoxsendReport.setStandardButtons(QMessageBox.Ok | QMessageBox.Cancel)
            returnValue = messageBoxsendReport.exec_()
            if returnValue == QMessageBox.Ok:
                messageBox = QMessageBox()
                icon = QIcon("imgs/checked.png")
                messageBox.setIconPixmap(icon.pixmap(60, 60))
                messageBox.setWindowTitle("Отправка файла")
                messageBox.setText("Протокол успешно отправлен на сервер")
                messageBox.setStandardButtons(QMessageBox.Ok)
                messageBox.exec_()
                database.child("protocols").child("1").set(resultJson)
            self.chooseMethods = QMainWindow()
            self.chooseMethodsUi = ReportGenerate.ReportGenerate()
            self.chooseMethodsUi.setupUi(self.chooseMethods)
            self.chooseMethodsUi.initEventListeners()
            self.chooseMethods.show()
    def sendReport(self,info):
        print("test")


