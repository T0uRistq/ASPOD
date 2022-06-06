@echo on
call %userprofile%\miniconda3\Scripts\activate.bat %userprofile%\miniconda3
python %0\..\main.py
pause