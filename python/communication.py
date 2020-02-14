# Salahdin Belghiti

# Imports

import socket
import time

# IP servidor
ipServidor = '192.168.2.82'
# Puerto servidor
puerto = 6666
# Declaración de socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.settimeout(10)


# Método para mandar aviso de envio de email a servidor
def mandarEmail():
    global s
    mensaje = 'EMAIL'
    try:
        s.sendall(mensaje.encode('utf-8'))
    except:
        pass


# Método para mandar aviso de modificacion de base de datos a servidor
def registrarBD(filename, camera, tiempo):
    global s
    message = "RECORD," + filename + "," + camera + "," + str(tiempo)
    try:
        s.sendall(message.encode('utf-8'))
    except:
        pass


# Método para conectar al servidor
def connectToServer():
    global s
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((ipServidor, puerto))
        return True
    except:
        return False


def disconnect():
    s.shutdown(1)
