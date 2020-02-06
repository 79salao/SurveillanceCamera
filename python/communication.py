import socket

# IP servidor
ipServidor = '192.168.2.82'
# Puerto servidor
puerto = 6666
# Declaración de socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Método para conectar
s.connect((ipServidor, puerto))


# Método para mandar aviso de envio de email a servidor
def mandarEmail():
    global s
    mensaje = 'EMAIL'
    s.sendall(mensaje.encode('utf-8'))
    print("Se ha mandado el aviso al servidor para mandar email.")


# Método para mandar aviso de modificacion de base de datos a servidor
def registrarBD(filename, camera, tiempo):
    global s
    message = filename + "," + camera + "," + str(tiempo)
    s.sendall(message.encode('utf-8'))
    print("Se ha mandado la petición de registro en la base de datos al servidor.")
