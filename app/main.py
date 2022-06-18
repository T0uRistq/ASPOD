import sys
from PyQt5.QtWidgets import QApplication, QMainWindow
import ReportGenerate
import firebaseconnect

app = QApplication(sys.argv)

MainWindow = QMainWindow()
ui = ReportGenerate.ReportGenerate()
# ui = firebaseconnect.AuthWindow()
ui.setupUi(MainWindow)
# Подписаться на события
ui.initEventListeners()

MainWindow.show()
sys.exit(app.exec_())
