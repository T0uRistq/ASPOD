import sys
import time

import pyrebase
from PyQt5.QtWidgets import QApplication, QMainWindow
import firebaseconnect as pd
import firebaseconnect
import ReportGenerate
import Ui_EnergoExpert


app = QApplication(sys.argv)

MainWindow = QMainWindow()
ui = firebaseconnect.AuthWindow()
ui.setupUi(MainWindow)
# Подписаться на события
ui.initEventListeners()

MainWindow.show()
sys.exit(app.exec_())
