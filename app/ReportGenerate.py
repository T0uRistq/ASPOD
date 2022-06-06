import ast
import time
import pyrebase
import os
from PyQt5.QtGui import QIcon

from PyQt5.QtWidgets import QMainWindow, QListWidgetItem, QMessageBox, QFileDialog

import threading

from PyQt5 import QtCore, QtWidgets

from BuildDocClass import BuildDoc

executors = [{'executor' : 'ООО «НИИПГАЗА»', 'postal' : '450059, Россия, Республика Башкортостан, г. Уфа, проспект Октября, дом 43/5, офис Б',
                          'cert' : '№ ЛНК-053А0002 от 02.03.2021 г'},
        {'executor' : 'ООО «Энергоэксперт»', 'postal' : '197342, г. Санкт-Петербург, наб. Черной речки, д.41, к.2, лит. Б, пом.7',
                          'cert' : '№ 89А112162 от 14.02.2020 г.'}]
clients = [{'client' : 'ООО «Газпром трансгаз Казань»', 'postal' : '450059, Россия, Республика Башкортостан, г. Уфа, проспект Октября, дом 43/5, офис Б'},
        {'client' : 'ООО «Газпром трансгаз Югорск»', 'postal' : '628260, РФ, г. Югорск, ул. Мира, 15'}]
from Ui_ServerForm import Ui_MainWindow
firebaseconfig = {'apiKey': "AIzaSyDQeQ_YV0ZVeLW--dzDt6XntEwcCEGwTrg",
                  'authDomain': "energotemp-9b8c9.firebaseapp.com",
                  'databaseURL': "https://energotemp-9b8c9-default-rtdb.europe-west1.firebasedatabase.app",
                  'projectId': "energotemp-9b8c9",
                  'storageBucket': "energotemp-9b8c9.appspot.com",
                  'messagingSenderId': "622369709896",
                  'appId': "1:622369709896:web:5b1121856ffdff3a4e9d7d"}

fireBaseApp= pyrebase.initialize_app(firebaseconfig)
database = fireBaseApp.database()
class ReportGenerate(QtCore.QObject, Ui_MainWindow):
    fileName = str()
    finished = QtCore.pyqtSignal()

    def initEventListeners(self):
        sp_retain = self.progressBar.sizePolicy()
        sp_retain.setRetainSizeWhenHidden(True)
        self.progressBar.setSizePolicy(sp_retain)
        self.progressBar.setVisible(False)
        self.pushButton.clicked.connect(self.onClickHandlerGetListsOfReports)
        self.pushButton_2.clicked.connect(self.onClickHandlerGenerateReport)
        self.finished.connect(self.on_finished)

    def on_finished(self):
        messageBox = QMessageBox()
        icon = QIcon("imgs/" + ("success" if self.success else "failure") + ".png")
        messageBox.setIconPixmap(icon.pixmap(60, 60))
        messageBox.setWindowTitle("Создание отчета")
        messageBox.setText("Отчет успешно создан!" if self.success else "Пожалуйста, выберите отчет из списка!")
        messageBox.setStandardButtons(QMessageBox.Ok)
        messageBox.exec_()
        self.progressBar.setVisible(False)

    def onClickHandlerGetListsOfReports(self):
       if threading.active_count() == 1:   # Если запущен только первичный поток, то запускаем еще один для получения данных
           self.pushButton.setEnabled(False)
           threading.Thread(target=self.getListOfReports).start()

    def onClickHandlerGenerateReport(self):
        self.success = len(self.listWidget.selectedItems()) # it is success that we have selected item and can proceed
        if threading.active_count() == 1 and self.success:
            self.pushButton_2.setEnabled(False)
            self.fileName = QFileDialog.getSaveFileName(None, "Сохранить протокол как", os.path.expanduser('~/Documents/Protocol.docx'), "*.docx \n *.doc")[0]
            self.generateReportThread = threading.Thread(target=self.generateReport, daemon=True)
            self.generateReportThread.start()
        else self.finished.emit()

    def getListOfReports(self):
        self.listWidget.clear()
        data = database.child("protocols").get().val()
        for item in data:
            dictionary = ast.literal_eval(item)
            item = QListWidgetItem()
            item.setIcon(QIcon('.\imgs\word.png'))
            item.setText(dictionary['date'])
            self.listWidget.addItem(item)
        self.pushButton.setEnabled(True)

    def generateReport(self):
        docBuilderInstance = BuildDoc()
        docBuilderInstance.percentageChanged.connect(self.progressBar.setValue)
        docBuilderInstance.percentage = 1
        self.progressBar.setVisible(True)
        child = str(self.listWidget.indexFromItem(self.listWidget.currentItem()).row())
        toColor = self.checkColor.isChecked()
        docBuilderInstance.build_reports(child, self.fileName, toColor)
        self.pushButton_2.setEnabled(True)
        self.finished.emit()


