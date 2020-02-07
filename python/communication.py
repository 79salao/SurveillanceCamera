# Salahdin belghiti

import socket

# IP servidor
ipServidor = '192.168.2.82'
# Puerto servidor
puerto = 6666
# Declaración de socket
print("Intentando conectar al servidor...")
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.settimeout(10)
# Método para conectar
try:
    s.connect((ipServidor, puerto))
except:
    print("ERROR No se ha podido conectar al servidor")
    print("Intentando reconectar al servidor...")
    try:
        s.connect((ipServidor, puerto))
    except:
        print("ERROR No se ha podido reconectar al servidor")
        print("AVISO No se podrá mandar avisos al servidor")


# Método para mandar aviso de envio de email a servidor
def mandarEmail():
    global s
    mensaje = 'EMAIL'
    print("Mandando mensaje de aviso para el email...")
    try:
        s.sendall(mensaje.encode('utf-8'))
        print("Se ha mandado el aviso al servidor para mandar email.")
    except:
        print("ERROR MENSAJE DE AVISO DE EMAIL NO ENVIADO ")


# Método para mandar aviso de modificacion de base de datos a servidor
def registrarBD(filename, camera, tiempo):
    global s
    message = filename + "," + camera + "," + str(tiempo)
    print("Mandando mensaje de aviso para base de datos..")
    try:
        s.sendall(message.encode('utf-8'))
        print("Se ha mandado la petición de registro en la base de datos al servidor.")
    except:
        print("ERROR MENSAJE DE AVISO DE BASE DE DATOS NO ENVIADO")
