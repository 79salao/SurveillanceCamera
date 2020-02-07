# Salahdin Belghiti

import communication
import io
import picamera
import logging
import socketserver
from threading import Condition
from http import server
import datetime

# Variable que almacena el estado del puerto 2 de la cámara (puerto de grabaciones)
grabando = False
# Variable que almacena el objeto camera
camera = picamera.PiCamera()
# Variable que almacena el nombre del archivo de grabacion
filename = ""
# Variable de tiempo
tiempo = 0


# Métodos para controlar el streaming

class StreamingOutput(object):
    def __init__(self):
        self.frame = None
        self.buffer = io.BytesIO()
        self.condition = Condition()

    def write(self, buf):
        if buf.startswith(b'\xff\xd8'):
            # New frame, copy the existing buffer's content and notify all
            # clients it's available
            self.buffer.truncate()
            with self.condition:
                self.frame = self.buffer.getvalue()
                self.condition.notify_all()
            self.buffer.seek(0)
        return self.buffer.write(buf)


class StreamingHandler(server.BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == '/stream.mjpg':
            self.send_response(200)
            self.send_header('Age', 0)
            self.send_header('Cache-Control', 'no-cache, private')
            self.send_header('Pragma', 'no-cache')
            self.send_header('Content-Type', 'multipart/x-mixed-replace; boundary=FRAME')
            self.end_headers()
            try:
                while True:
                    with output.condition:
                        output.condition.wait()
                        frame = output.frame
                    self.wfile.write(b'--FRAME\r\n')
                    self.send_header('Content-Type', 'image/jpeg')
                    self.send_header('Content-Length', len(frame))
                    self.end_headers()
                    self.wfile.write(frame)
                    self.wfile.write(b'\r\n')
            except Exception as e:
                logging.warning(
                    'Removed streaming client %s: %s',
                    self.client_address, str(e))
        else:
            self.send_error(404)
            self.end_headers()


class StreamingServer(socketserver.ThreadingMixIn, server.HTTPServer):
    allow_reuse_address = True
    daemon_threads = True


def stream():
    global camera
    global output
    output = StreamingOutput()
    camera.rotation = 180
    camera.framerate = 25
    camera.start_recording(output, format='mjpeg', resize=(480, 320))
    try:
        address = ('', 8000)
        server = StreamingServer(address, StreamingHandler)
        server.serve_forever()
    finally:
        camera.stop_recording(splitter_port=1)


# Métodos para controlar las grabaciones

def record():
    global grabando
    global camera
    global filename
    grabando = True
    x = datetime.datetime.now()
    path = "/home/pi/Desktop/SurveillanceCamera-master/Grabaciones/"
    filename = path + x.strftime("%Y") + "_" + x.strftime("%m") + "_" + x.strftime("%d") + "_" + x.strftime("%X") + ".h264"
    try:
        camera.start_recording(filename, splitter_port=2, resize=(480, 320))
    except:
        print("ERROR NO SE HA PODIDO EMPEZAR LA GRABACION")
    communication.mandarEmail()


def stopRecording():
    global camera
    global grabando
    global tiempo
    if grabando:
        communication.registrarBD(filename, "1", tiempo)
        try:
            camera.stop_recording(splitter_port=2)
        except:
            print("HA HABIDO UN ERROR A LA HORA DE PARAR LA GRABACION")
        grabando = False
        tiempo = 0
