import threading
import time
import stream
from radarUltrasonido import ultrasonido_start()

#VARIABLES GLOBALES

movimiento = False

#METODOS THREADS

def thread_movimiento():
    global movimiento
    while True:
        movimiento = ultrasonido_start()
        time.sleep(0.5)

def thread_stream():
    stream.stream()  # iniciar stream y pagina web

#EJECUCION

thread = threading.Thread(target=thread_movimiento()).start() #iniciar detector de movimiento
thread = threading.Thread(target=thread_stream()).start() #iniciar stream


while True:
    if movimiento:
        stream.record()




