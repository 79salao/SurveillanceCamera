# Salahdin Belghiti

# Imports

import threading
import time
import stream
import gpiozero

# Variables globales

movimiento = False  # Variable que indica cuándo hay movimiento y cuando no.
detecciones = 0  # Variable que indica las detecciones que hay, se usa para evitar falsas alarmas.
noDetecciones = 0  # Variable que indica las veces que no se ha detectado movimiento.
sensor = gpiozero.MotionSensor(24)  # Variable del sensor de movimiento.
threads = []  # Lista que almacena los threads del temporizador.
duracionVideos = 60  # Variable para controlar la duración de los vídeos (En segundos)
segundosDeEspera = 5  # Numero de segundos y veces que se comprueba que movimiento antes de parar la grabacion.


# Métodos

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
    global duracionVideos
    print("Thread movimiento iniciado.")
    print("Sensor de movimiento iniciado.")
    # Comprueba que hay movimiento
    if isActive():
        while True:
            # Este condicional comprueba el tamaño del array de threads y lo limpia
            if len(threads) == 10:
                clearThreads()
            # Este condicional comprueba que el sensor esté activo, y cada vez que detecta
            # movimiento, lo almacena en la variable detecciones
            if sensor.is_active:
                detecciones = detecciones + 1
                print("Se ha detectado movimiento " + str(detecciones) + "veces...")
                time.sleep(0.3)
            # Si no detecta movimiento 5 veces, se restablece el contador de detecciones.
            else:
                print("No se ha vuelto a detectar movimiento...")
                detecciones = 0
                time.sleep(3)
            # Si se detecta movimiento 5 veces, la variable movimiento se torna true
            if detecciones == 5:
                # Se restablece el contador de detecciones para la proxima vez
                detecciones = 0
                print("Movimiento detectado 5 veces, iniciando...")
                movimiento = True
                while True:
                    # Condicional que comprueba que los videos no exceden el limite de duracion
                    if stream.tiempo >= duracionVideos:
                        print("Se ha alcanzado el limite de duración, se reiniciara la grabacion.")
                        movimiento = False
                        time.sleep(0.2)
                        movimiento = True
                        # Nuevo thread de temporizador
                        newThread()
                    # Este condicional comprueba si hay movimiento, si no detecta, aumenta el contador de noDetecciones.
                    if not isActive():
                        noDetecciones = noDetecciones + 1
                        time.sleep(1)
                        print("No se ha detectado movimiento " + str(noDetecciones) + "/" + str(segundosDeEspera))
                    else:
                        noDetecciones = 0
                    if noDetecciones == segundosDeEspera:
                        # Se para la grabación
                        stream.stopRecording()
                        print("Se parará el vídeo por falta de movimiento.")
                        # Método que para los threads
                        joinThreads()
                        movimiento = False
                        noDetecciones = 0
                        break


# Método del temporizador
def temporizador():
    while stream.grabando:
        time.sleep(1)
        print(stream.tiempo)
        stream.tiempo = stream.tiempo + 1


# Método que crea un thread y lo retorna
def newThread():
    stream.tiempo = 0
    threadT = threading.Thread(target=temporizador, daemon=True)
    threads.append(threadT)
    return threadT


# Método que para los threads
def joinThreads():
    for i in range(len(threads)):
        if threads[i].isAlive():
            threads[i].join()


# Método que vacía el array de threads
def clearThreads():
    global threads
    threads = []
    print("Se han limpiado los threads.")


# Flujo de ejecución principal

# Iniciar thread del detector de movimiento
threadM = threading.Thread(target=thread_movimiento).start()
# Iniciar thread del la cámara
threadS = threading.Thread(target=stream.stream).start()  # iniciar stream
# Iniciar programa
while True:
    if movimiento:
        newThread().start()
        stream.record()
        while True:
            if movimiento:
                pass
            else:
                stream.stopRecording()
                stream.tiempo = 0
                print("Grabacion terminada.")
                stream.parar = True
                if len(threads) > 10:
                    joinThreads()
                newThread()
                break
