import datetime
import pyrebase
import json
from PyQt5.QtGui import QIcon
from PyQt5.QtWidgets import QMessageBox, QMainWindow
import ReportGenerate
from Ui_ControlMethods import Ui_MainWindow

executors = [{'executor': 'ООО «НИИПГАЗА»',
              'postal': '450059, Россия, Республика Башкортостан, г. Уфа, проспект Октября, дом 43/5, офис Б',
              'cert': '№ ЛНК-053А0002 от 02.03.2021 г'},
             {'executor': 'ООО «Энергоэксперт»',
              'postal': '197342, г. Санкт-Петербург, наб. Черной речки, д.41, к.2, лит. Б, пом.7',
              'cert': '№ 89А112162 от 14.02.2020 г.'}]

firebaseconfig = {'apiKey': "AIzaSyDQeQ_YV0ZVeLW--dzDt6XntEwcCEGwTrg",
                  'authDomain': "energotemp-9b8c9.firebaseapp.com",
                  'databaseURL': "https://energotemp-9b8c9-default-rtdb.europe-west1.firebasedatabase.app",
                  'projectId': "energotemp-9b8c9",
                  'storageBucket': "energotemp-9b8c9.appspot.com",
                  'messagingSenderId': "622369709896",
                  'appId': "1:622369709896:web:5b1121856ffdff3a4e9d7d"}

fireBaseApp = pyrebase.initialize_app(firebaseconfig)
database = fireBaseApp.database()

devices = ['136033', '135783']

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
        file = open('data/JSONstring.txt', 'r+')
        json_dict = json.loads(file.read())
        json_dict["methods"] = self.val
        json_dict["device"] = self.comboBox.currentIndex()
        json_dict["date"] = self.dateEdit.date().toPyDate().strftime("%d.%m.%Y")
        file.close()
        file = open('data/JSONstring.txt', 'w')
        json.dump(json_dict, file)
        file.close()

        exit()

        # if "date" in json_string:
        #     messageBox = QMessageBox()
        #     messageBox.setIcon(QMessageBox.Information)
        #     messageBox.setWindowTitle("Отправка файла")
        #     messageBox.setText("Данные сохранены")
        #     messageBox.setStandardButtons(QMessageBox.Ok)
        #     messageBox.exec_()
        #     messageBoxsendReport = QMessageBox()
        #     messageBoxsendReport.setIcon(QMessageBox.Information)
        #     messageBoxsendReport.setWindowTitle("Отправка файла")
        #     messageBoxsendReport.setText("Отправить отчет сейчас?")
        #     messageBoxsendReport.setStandardButtons(QMessageBox.Ok | QMessageBox.Cancel)
        #     returnValue = messageBoxsendReport.exec_()
        #     if returnValue == QMessageBox.Ok:
        #         messageBox = QMessageBox()
        #         icon = QIcon("imgs/checked.png")
        #         messageBox.setIconPixmap(icon.pixmap(60, 60))
        #         messageBox.setWindowTitle("Отправка файла")
        #         messageBox.setText("Протокол успешно отправлен на сервер")
        #         messageBox.setStandardButtons(QMessageBox.Ok)
        #         messageBox.exec_()
        #         db_child_key = len(database.child('protocols').get().val())
        #         database.child("protocols").child(db_child_key).set(json_string)
        #         self.chooseMethods = QMainWindow()
        #         self.chooseMethodsUi = ReportGenerate.ReportGenerate()
        #         self.chooseMethodsUi.setupUi(self.chooseMethods)
        #         self.chooseMethodsUi.initEventListeners()
        #         self.chooseMethods.show()

    # def sendReport(self, info):
    #     print("test")