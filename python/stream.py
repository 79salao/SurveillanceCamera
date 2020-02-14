# Salahdin Belghiti

# Imports

import communication
import io
import picamera
import logging
import socketserver
from threading import Condition
from http import server
import datetime
import gui

# Variable que almacena el estado del puerto 2 de la cámara (puerto de grabaciones)
grabando = False
# Variable que almacena el objeto camera
camera = None
# Variable que almacena el nombre del archivo de grabacion
filename = ""
# Variable de tiempo
tiempo = 0
# Variable para controlar la duración de los vídeos (En segundos)
duracionVideos = 60
altoRec = 480
anchoRec = 320
altoStrm = 480
anchoStrm = 320
fps = 25
# HTML simple para visualizar el stream
PAGE = """\
<html>
<head>
<title>Raspberry Pi - Surveillance Camera</title>
</head>
<body>
<center><h1>Raspberry Pi - Surveillance Camera</h1></center>
<center><img src="stream.mjpg" width="640" height="480"></center>
</body>
</html>
"""

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
        if self.path == '/':
            self.send_response(301)
            self.send_header('Location', '/index.html')
            self.end_headers()
        elif self.path == '/index.html':
            content = PAGE.encode('utf-8')
            self.send_response(200)
            self.send_header('Content-Type', 'text/html')
            self.send_header('Content-Length', len(content))
            self.end_headers()
            self.wfile.write(content)
        elif self.path == '/stream.mjpg':
            self.send_response(200)
            self.send_header('Age', 0)
            self.send_header('Cache-Control', 'no-cache, private')
            self.send_header('Pragma', 'no-cache')
            self.send_header('Content-Type', 'multipart/x-mixed-replace; boundary=FRAME')
            self.end_headers()
            try:
                while gui.funcionando:
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


address = ('', 8000)
server = StreamingServer(address, StreamingHandler)

# Metodo para iniciar el streaming
def stream():
    global server
    global camera
    global output
    # Config de la camara en stream
    camera = picamera.PiCamera()
    output = StreamingOutput()
    camera.rotation = 180
    camera.framerate = fps
    camera.start_recording(output, format='mjpeg', resize=(altoStrm, anchoStrm))
    try:
        server.serve_forever()
    finally:
        try:
            camera.stop_recording(splitter_port=1)
        except:
            pass



# Métodos para controlar las grabaciones

def record():
    global grabando
    global camera
    global filename
    grabando = True
    x = datetime.datetime.now()
    filename = x.strftime("%Y") + "_" + x.strftime("%m") + "_" + x.strftime("%d") + "_" + x.strftime(
        "%X") + ".h264"
    try:
        camera.start_recording(filename, splitter_port=2, resize=(altoRec, anchoRec))
        communication.mandarEmail()
    except:
        pass


def stopRecording():
    global camera
    global grabando
    global tiempo
    if grabando:
        communication.registrarBD(filename, "1", tiempo)
        try:
            camera.stop_recording(splitter_port=2)
        except:
            pass
        finally:
            grabando = False
            tiempo = 0




