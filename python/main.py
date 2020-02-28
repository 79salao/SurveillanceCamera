# Salahdin Belghiti

# Imports

import gui
import threading
import time
import stream
import gpiozero

# Variables globales sin inicializar

movimiento = None
detecciones = None
noDetecciones = None
sensor = None
threadsT = None
threadsS = None
segundosDeEspera = None
firstTime = True
# Métodos


def init():
    global movimiento
    global detecciones
    global noDetecciones
    global sensor
    global threadsT
    global threadsS
    global segundosDeEspera
    movimiento = False  # Variable que indica cuándo hay movimiento y cuando no.
    detecciones = 0  # Variable que indica las detecciones que hay, se usa para evitar falsas alarmas.
    noDetecciones = 0  # Variable que indica las veces que no se ha detectado movimiento.
    sensor = gpiozero.MotionSensor(24)  # Variable del sensor de movimiento.
    threadsT = []  # Lista que almacena los threads del temporizador.
    threadsS = []  # Lista que almacena los threads del stream.
    segundosDeEspera = 5  # Numero de segundos y veces que se comprueba que movimiento antes de parar la grabacion.
    # Iniciar thread del detector de movimiento
    threadM = threading.Thread(target=thread_movimiento).start()
    # Iniciar thread del la cámara
    threadRefresh = threading.Thread(target=gui.refresh).start()
    threadServer = threading.Thread(target=gui.checkConnection).start()
    threadMain = threading.Thread(target=main).start()


# Método que comprueba (varias veces) que el sensor está activo


def isActive():
    for i in range(3):
        if not sensor.is_active:
            time.sleep(0.1)
        else:
            return True
    return False


def thread_movimiento():
    global detecciones
    global noDetecciones
    global movimiento
    # Comprueba que hay movimiento
    sensor.wait_for_active()
    for i in range(2):
        sensor.wait_for_active()
    while True:
        # Este condicional comprueba el tamaño del array de threads y lo limpia
        if len(threadsT) or len(threadsS) == 10:
            clearThreads()
        # Este condicional comprueba que el sensor esté activo, y cada vez que detecta
        # movimiento, lo almacena en la variable detecciones
        if sensor.is_active:
            detecciones = detecciones + 1
            time.sleep(0.3)
        # Si no detecta movimiento 5 veces, se restablece el contador de detecciones.
        else:
            detecciones = 0
            time.sleep(3)
        # Si se detecta movimiento 5 veces, la variable movimiento se torna true
        if detecciones == 5:
            # Se restablece el contador de detecciones para la proxima vez
            detecciones = 0
            movimiento = True
            while True:
                # Condicional que comprueba que los videos no exceden el limite de duracion
                if stream.tiempo >= stream.duracionVideos:
                    movimiento = False
                    stream.grabando = False
                    time.sleep(0.2)
                    movimiento = True
                    stream.grabando = True
                    # Nuevo thread de temporizador
                    newThread("tiempo").start()
                # Este condicional comprueba si hay movimiento, si no detecta, aumenta el contador de noDetecciones.
                if not isActive():
                    noDetecciones = noDetecciones + 1
                    time.sleep(1)
                else:
                    noDetecciones = 0
                if noDetecciones == segundosDeEspera:
                    # Se para la grabación
                    stream.stopRecording()
                    # Método que para los threads
                    joinThreads("tiempo")
                    movimiento = False
                    noDetecciones = 0
                    break


# Método del temporizador
def temporizador():
    print("temporizador")
    while stream.grabando:
        time.sleep(1)
        text = "Duration: " + str(stream.tiempo) + " seconds"
        gui.recordingStatusDurationLabel.configure(text=text)
        stream.tiempo = stream.tiempo + 1
        print(stream.tiempo)


# Método que crea un thread y lo retorna
def newThread(type):
    if type == "tiempo":
        stream.tiempo = 0
        threadT = threading.Thread(target=temporizador, daemon=True)
        threadsT.append(threadT)
        return threadT
    elif type == "stream":
        threadS = threading.Thread(target=stream.stream)
        threadsS.append(threadS)
        return threadS


# Método que para los threads
def joinThreads(type):
    if type == "tiempo":
        for i in range(len(threadsT)):
            if threadsT[i].isAlive():
                threadsT[i].join()
    elif type == "stream":
        for i in range(len(threadsS)):
            if threadsS[i].isAlive():
                threadsS[i].join()


# Método que vacía el array de threads
def clearThreads():
    global threadsT
    global threadsS
    threadsT = []
    threadsS = []


def main():
    gui.connect()
    newThread("stream").start()
    while True:
        if movimiento:
            newThread("tiempo").start()
            stream.record()
            while True:
                if movimiento:
                    pass
                else:
                    stream.stopRecording()
                    stream.tiempo = 0
                    stream.parar = True
                    if len(threadsT) > 10:
                        joinThreadsT("tiempo")
                    break


