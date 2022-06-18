import json
import math
from PyQt5 import QtCore
from docxtpl import DocxTemplate
from docxcompose.composer import Composer
from docx import Document
import pandas as pd
import pyrebase

executors = [{'executor' : 'ООО «НИИПГАЗА»', 'postal' : '450059, Россия, Республика Башкортостан, г. Уфа, проспект Октября, дом 43/5, офис Б',
              'cert' : '№ ЛНК-053А0002 от 02.03.2021 г'},
             {'executor' : 'ООО «Энергоэксперт»', 'postal' : '197342, г. Санкт-Петербург, наб. Черной речки, д.41, к.2, лит. Б, пом.7',
              'cert' : '№ 89А112162 от 14.02.2020 г.'}]

firebase = pyrebase.initialize_app({'apiKey': "AIzaSyDQeQ_YV0ZVeLW--dzDt6XntEwcCEGwTrg",
                                    'authDomain': "energotemp-9b8c9.firebaseapp.com",
                                    'databaseURL': "https://energotemp-9b8c9-default-rtdb.europe-west1.firebasedatabase.app",
                                    'projectId': "energotemp-9b8c9",
                                    'storageBucket': "energotemp-9b8c9.appspot.com",
                                    'messagingSenderId': "622369709896",
                                    'appId': "1:622369709896:web:5b1121856ffdff3a4e9d7d"})

clients = [{'client' : 'ООО «Газпром трансгаз Казань»', 'postal' : '450059, Россия, Республика Башкортостан, г. Уфа, проспект Октября, дом 43/5, офис Б'},
    {'client' : 'ООО «Газпром трансгаз Югорск»', 'postal' : '628260, РФ, г. Югорск, ул. Мира, 15'}]

devices = [{'obj_name' : 'Фильтр высокого давления, зав. № F500/1, рег. № 75', 'number' : '136033', 'obj_location' : 'Приозёрное ЛПУМГ, КЦ – 1МГ «Уренгой - Ужгород»', 'concl_num' : '№ ТО-ЭЭ-СРД-0322.08-2021', 'org' : 1},
      {'obj_name' : 'Пылеуловитель зав. № 46301, рег. № 727', 'number' : '135783', 'obj_location' : 'Правохеттинское ЛПУМГ, КЦ – 4МГ «Ямбург – Елец 1»', 'concl_num' : '№ ТО-ЭЭ-СРД-0324.08-2021', 'org' : 1}]

db = firebase.database()

class BuildDoc(QtCore.QObject):
    percentageChanged = QtCore.pyqtSignal(int)
    toColor = False
    tools_df = pd.read_excel('tables/tools.xlsx', dtype=object)

    def __init__(self, parent=None):
        super().__init__(parent)
        self.files_list = list()
        self._percentage = 0

    # Геттер
    @property
    def percentage(self):
        return self._percentage

    # Сеттер
    @percentage.setter
    def percentage(self, value):
        if self._percentage == value:
            return
        self._percentage = value
        self.percentageChanged.emit(self.percentage)

    def buildDoc(self, context, num):
        doc = DocxTemplate('templates/' + ('highlighted/' if self.toColor else '') + 'protocol_' + str(num) + 't.docx')
        doc.render(context)
        doc.save('temp/protocol_' + str(num) + '.docx')
        self.files_list.append('temp/protocol_' + str(num) + '.docx')

    def mergeDoc(self, fileName):
        self.percentage = 50
        master = Document(self.files_list[0])
        composer = Composer(master)
        master.add_page_break()
        file_list_length = len(self.files_list)
        progress_step = math.ceil(50 / file_list_length)
        for i in range(1, file_list_length):
            appendedDoc = Document(self.files_list[i])
            if i < len(self.files_list) - 1: appendedDoc.add_page_break()
            composer.append(appendedDoc)
            self.percentage += progress_step
        composer.save(fileName)
        self.percentage = 100

    def method_equals(self, method, other):
        return True

    def get_tools(self,fio, method):
        res = list()
        for i in range(len(self.tools_df)):
            if self.tools_df.iloc[i][4].strip() == fio and self.method_equals(self.tools_df.iloc[i][15], method):
                res.append({'numberDevice': len(res) + 1, 'c0': self.toosl_df.iloc[i][1], 'c1': self.tools_df.iloc[i][2], 'c2': self.tools_df.iloc[i][8],
                            'c3': self.tools_df.iloc[i][9].strftime('%d.%m.%Y')})
        return res

    def build_reports(self, childToChoose, fileName, to_color):
        self.toColor = to_color

        # Выполнение кода до вызова mergeDoc() - это 50% progressBar
        json_string = json.loads(db.child('protocols').child(childToChoose).get().val())
        progress_step = math.ceil(50 / len(json_string['methods']))
        for i, method in enumerate(json_string['methods']):
            context = {**executors[json_string['executor']], **clients[json_string['client']],
                       **devices[json_string['device']]}
            context['date'] = json_string['date']
            context['p_idx'] = i + 1
            user = db.child('users').get()[json_string['FIO']]
            name = user.key()
            context['tbl_devices'] = self.get_tools(name, 'some_method')
            name = name.split()
            user = user.val()
            context['org_info'] = clients[devices[json_string['device']]['org']]['client']
            context['specialist_position'] = "Специалист ВИК " + json.loads(user[0])["1"] + " уровня"
            context['specialist_qual'] = "Квалификационное удостоверение " + json.loads(user[4])[
                "1"] + ", действительно до " + json.loads(user[2])["1"]
            context['surname_and_initials'] = name[1][0] + '.' + name[2][0] + '. ' + name[0]
            self.buildDoc(context, method)
            self.percentage += progress_step
        self.mergeDoc(fileName)