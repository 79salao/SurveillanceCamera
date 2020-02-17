# Salahdin Belghiti

# Imports

import tkinter as tk
import tkinter.ttk as ttk
import threading
import stream
import urllib.request
import communication
import time
import webbrowser
import os
import sys
import psutil
import logging
import main
import pickle

# Lineas que cargan la configuracion
if os.path.getsize("config.pickle") > 0:
    config_in = open("config.pickle", "rb")
    confArray = pickle.load(config_in)
else:
    confArray1 = ["Select resolution", "Select FPS", "Select limit (in seconds)"]
    confArray2 = ["Select resolution", "Select FPS"]
    confArray = [confArray1, confArray2]
config_out = open("config.pickle", "wb")


# Método que termina la ejecución de todo el programa
def salir():
    config_out.close()
    exit()
    window.destroy()


# Array que guarda la configuracion de las grabaciones
confArray1 = confArray[0]
# Array que guarda la configuracion del streaming
confArray2 = confArray[1]

# Variables globales
connected = False
tokenServer = True
funcionando = True

# Varbiables de configuracion del gui
window = tk.Tk()
window.title("Surveillance control panel")
window.geometry("460x400")
window.resizable(0, 0)
window.protocol('WM_DELETE_WINDOW', salir)  # root is your root window
tab_control = ttk.Notebook(window)
tab1 = ttk.Frame(tab_control)
tab2 = ttk.Frame(tab_control)
tab_control.add(tab1, text="Status")
tab_control.add(tab2, text="Settings")
tab_control.pack(expand=1, fill="both")


# Método que abre el directo con el navegador por defecto
def openStream():
    webbrowser.open_new_tab("http://192.168.2.119:8080/html/mainpage.html")


def openRecords():
    webbrowser.open_new_tab("http://192.168.2.119:8080/html/mainpage.html")


# Método que saca dialogos
def alert_popup(title, message):
    root = tk.Tk()
    root.title(title)
    w = 300
    h = 130
    sw = root.winfo_screenwidth()
    sh = root.winfo_screenheight()
    x = (sw - w) / 2
    y = (sh - h) / 2
    root.geometry('%dx%d+%d+%d' % (w, h, x, y))
    m = message
    m += '\n'
    w = tk.Label(root, text=m, width=40, height=4)
    w.pack()
    b = tk.Button(root, text="Ok", command=root.destroy, padx=1)
    b.pack()
    window.mainloop()


# Método que aplica la configuracion y la guarda en el archivo
def applyRecords():
    global confArray1
    global config_out
    global confArray
    # Variables que almacenan los datos desde los input del gui
    resolution = comboResRec.get()
    fps = comboResFps.get()
    limit = comboResLim.get()
    # Condicionales que se encargan de que solo se almacenen datos correctos
    if resolution != "Select resolution":
        resolutions = resolution.split("x")
        altoRec = int(resolutions[0])
        anchoRec = int(resolution[1])
        stream.altoRec = altoRec
        stream.anchoRec = anchoRec
        if fps != "Select FPS":
            stream.fps = int(fps)
            if limit == "No limit":
                stream.duracionVideos = 1000000000000000000
                confArray1 = [resolution, int(fps), limit]
                try:
                    confArray = [confArray1, confArray2]
                    pickle.dump(confArray, config_out)
                    config_out.close()
                except:
                    config_out = open("config.pickle", "wb")
                    confArray = [confArray1, confArray2]
                    pickle.dump(confArray, config_out)
                    config_out.close()
            elif limit != "No limit" and limit != "Select limit (in seconds)":
                stream.duracionVideos = int(limit)
                confArray1 = [resolution, int(fps), int(limit)]
                confArray = [confArray1, confArray2]
                try:
                    pickle.dump(confArray, config_out)
                    config_out.close()
                except:
                    config_out = open("config.pickle", "wb")
                    pickle.dump(confArray, config_out)
                    config_out.close()
            elif limit == "Select limit (in seconds)":
                alert_popup("Invalid recording limit", "Please select a limit")
        else:
            alert_popup("Invalid fps", "Please select an fps")
    else:
        alert_popup("Invalid resolution", "Please select a resolution")
    config_out.close()
    alert_popup("Changes applied", "The changes have been change and it will \n be applied in the next recording")


# Metodo que guarda la configuracion del streaming
def applyStream():
    global funcionando
    global config_out
    global confArray2
    global confArray
    funcionando = False
    main.checkThreads()
    resolution = comboStrmRes.get()
    fps = comboStrmFps.get()
    if resolution != "Select resolution":
        resolutions = resolution.split("x")
        altoRec = resolutions[0]
        anchoRec = resolution[1]
        stream.altoRec = int(altoRec)
        stream.anchoRec = int(anchoRec)
        if fps != "Select FPS":
            stream.fps = int(fps)
            try:
                confArray2 = [resolution, int(fps)]
                confArray = [confArray1, confArray2]
                pickle.dump(confArray, config_out)
                config_out.close()
            except:
                config_out = open("config.pickle", "wb")
                confArray = [confArray1, confArray2]
                pickle.dump(confArray, config_out)
                config_out.close()
        else:
            alert_popup("Invalid fps", "Please select an fps")
    else:
        alert_popup("Invalid resolution", "Please select a resolution")
    alert_popup("Changes applied", "Restart needed to apply changes")


# Metodo que comprueba que el streaming esta funcionando
def checkStream():
    time.sleep(5)
    try:
        if urllib.request.urlopen("http://192.168.2.119:8000/stream.mjpg").getcode() == 200:
            return True
        else:
            return False
    except:
        return False


# Metodo que comprueba que el programa esta conectado al servidor
def checkConnection():
    global connected
    while funcionando:
        if tokenServer:
            time.sleep(5)
            try:
                communication.s.sendall("".encode('utf-8'))
                serverStatusStateLabel.configure(text="Connected", fg="green")
                serverStatusButton.configure(text="Disconnect")
                connected = True
            except:
                connected = False
                serverStatusStateLabel.configure(text="Disconnected", fg="red")
                serverStatusButton.configure(text="Connect")


# Método que conecta al servidor
def connect():
    global connected
    global tokenServer
    tokenServer = False
    if not connected:
        if communication.connectToServer():
            serverStatusStateLabel.configure(text="Connected", fg="green")
            serverStatusButton.configure(text="Disconnect")
            connected = True
            tokenServer = True
        else:
            serverStatusStateLabel.configure(text="Disconnected", fg="red")
            serverStatusButton.configure(text="Connect")
            tokenServer = True
            connected = False
    else:
        communication.disconnect()
        serverStatusStateLabel.configure(text="Disconnected", fg="red")
        serverStatusButton.configure(text="Connect")
        tokenServer = True
        connected = False


# Metodo que convierte lo seleccionado a indice numerico para las gui
def getindex(value, limit):
    if limit:
        if value == "Select resolution" or value == "Select FPS":
            return 0
        elif value == "480x320" or value == 15:
            return 1
        elif value == "640x480" or value == 20:
            return 2
        elif value == "800x600" or value == 25:
            return 3
        elif value == "1024x768" or value == 30:
            return 4
        elif value == 35:
            return 5
        elif value == 40:
            return 6
        elif value == 45:
            return 7
    elif not limit:
        if value == "Select resolution" or value == "Select limit (in seconds)":
            return 0
        elif value == 30 or value == "480x320":
            return 1
        elif value == 60 or value == "640x480":
            return 2
        elif value == 90 or value == "800x600":
            return 3
        elif value == 120:
            return 4
        elif value == 180:
            return 5
        elif value == 240:
            return 6
        elif value == 300:
            return 7
        elif value == "No limit":
            return 8


# Metodo que actualiza los label del gui
def refresh():
    while funcionando:
        if stream.grabando:
            recordingStatusStateLabel.configure(text="Recording", fg="green")
        else:
            recordingStatusStateLabel.configure(text="Not recording", fg="red")
        if checkStream():
            streamingStatusStateLabel.configure(text="Streaming", fg="green")
        else:
            streamingStatusStateLabel.configure(text="Not streaming", fg="red")


# Camera settings

####################################################################################################

# Recording

labelRecordResolution = tk.Label(tab2, text="Recording resolution", font=("Arial", 12))
labelRecordFps = tk.Label(tab2, text="Recording fps", font=("Arial", 12))
labelRecordLimit = tk.Label(tab2, text="Recording limit", font=("Arial", 12))
comboResRec = ttk.Combobox(tab2, state="readonly")
comboResFps = ttk.Combobox(tab2, state="readonly")
comboResLim = ttk.Combobox(tab2, state="readonly")
comboResRec["values"] = ("Select resolution", "480x320", "640x480", "800x600", "1024x768")
comboResFps["values"] = ("Select FPS", 15, 20, 25, 30, 35, 40, 45)
comboResLim["values"] = ("Select limit (in seconds)", 30, 60, 90, 120, 180, 240, 300, "No limit")
comboResRec.current(getindex(confArray[0][0], True))
comboResFps.current(getindex(confArray[0][1], True))
comboResLim.current(getindex(confArray[0][2], False))
resBtn = tk.Button(tab2, text="Apply", command=applyRecords)

# Streaming

labelStreamResolution = tk.Label(tab2, text="Streaming resolution", font=("Arial", 12))
labelStreamFps = tk.Label(tab2, text="Streaming fps", font=("Arial", 12))
comboStrmRes = ttk.Combobox(tab2, state="readonly")
comboStrmFps = ttk.Combobox(tab2, state="readonly")
comboStrmRes["values"] = ("Select resolution", "480x320", "640x480", "800x600")
comboStrmFps["values"] = ("Select FPS", 15, 20, 25, 30, 35, 40)
comboStrmRes.current(getindex(confArray[1][0], False))
comboStrmFps.current(getindex(confArray[1][1], True))
strmBtn = tk.Button(tab2, text="Apply", command=applyStream)

###############################################################################################

# Connection status settings

labelStatusTitle = tk.Label(tab1, text="Connections status", font=("Arial", 18))
serverStatusLabel = tk.Label(tab1, text="Server connection status:", font=("Arial", 12))
serverStatusStateLabel = tk.Label(tab1, text="Connecting...", font=("Arial", 12), fg="orange")
serverStatusButton = tk.Button(tab1, text="Connect", font=("Arial", 12), command=connect)
streamingStatusLabel = tk.Label(tab1, text="Straming status:", font=("Arial", 12))
streamingStatusStateLabel = tk.Label(tab1, text="Not streaming", font=("Arial", 12), fg="red")
recordingStatusButton = tk.Button(tab1, text="See records", font=("Arial", 12), command=openRecords)
recordingStatusLabel = tk.Label(tab1, text="Recording status:", font=("Arial", 12))
recordingStatusStateLabel = tk.Label(tab1, text="Not recording", font=("Arial", 12), fg="red")
recordingStatusDurationLabel = tk.Label(tab1, text="Duration: 0 seconds", font=("Arial", 12))
streamingStatusButton = tk.Button(tab1, text="Watch stream", font=("Arial", 12), command=openStream)
restartButton = tk.Button(tab1, text="Restart program", font=("Arial", 12))
emptylabel = tk.Label(tab1)

###############################################################################################

# Grids

# Camera settings

# Recording

labelRecordResolution.grid(column=0, row=1, padx=(30, 20))
comboResRec.grid(column=0, row=2, pady=13, padx=(30, 20))
labelRecordFps.grid(column=0, row=3, pady=13, padx=(30, 20))
comboResFps.grid(column=0, row=4, pady=13, padx=(30, 20))
labelRecordLimit.grid(column=0, row=5, pady=13, padx=(30, 20))
comboResLim.grid(column=0, row=6, pady=13, padx=(30, 20))
resBtn.grid(column=0, row=7, pady=13, padx=(30, 20))

# Streaming

labelStreamResolution.grid(column=1, row=1, padx=(30, 20))
comboStrmRes.grid(column=1, row=2, pady=10, padx=(30, 20))
labelStreamFps.grid(column=1, row=3, pady=10, padx=(30, 20))
comboStrmFps.grid(column=1, row=4, pady=10, padx=(30, 20))
strmBtn.grid(column=1, row=5, pady=10, padx=(30, 20))

# Connection settings

serverStatusLabel.grid(column=0, row=1, padx=40, pady=8)
serverStatusStateLabel.grid(column=0, row=2, padx=40, pady=8)
serverStatusButton.grid(column=0, row=3, padx=40, pady=8)
streamingStatusLabel.grid(column=1, row=1, padx=40, pady=8)
streamingStatusStateLabel.grid(column=1, row=2, padx=40, pady=8)
streamingStatusButton.grid(column=1, row=3, padx=40, pady=8)
recordingStatusLabel.grid(column=0, row=7, padx=40, pady=8)
recordingStatusStateLabel.grid(column=0, row=8, padx=40, pady=8)
recordingStatusDurationLabel.grid(column=0, row=9, padx=40, pady=8)
recordingStatusButton.grid(column=0, row=10, padx=40, pady=8)
