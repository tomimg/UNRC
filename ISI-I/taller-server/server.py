from wsgiref.simple_server import make_server
import json

# Definimos el diccionario que contendrá todas las tareas.
tasks = {}

def app(environ, start_response):
    # Definimos las variables que vamos a usar.
    response = b""
    status = "200 OK"
    headers = [("Content-Type", "application/json; charset=utf-8")]

    # Obtenemos la ruta y la dividimos en partes.
    path = environ["PATH_INFO"].strip("/").split("/")

    match environ["REQUEST_METHOD"]:
        case "GET":
            if len(path) == 1 and path[0] == "tasks":
                response = json.dumps(tasks).encode("utf-8")
            elif len(path) == 2 and path[0] == "tasks":
                id = int(path[1])
                if id in tasks:
                    response = json.dumps(tasks[id]).encode("utf-8")
                else:
                    status404()

        case _:
            status405()

    start_response(status, headers)
    return [response]


def status404():
    status = "404 Not Found"
    headers = [("Content-Type", "text/plain")]
    response = b"404: not found"

def status405():
    status = "405 Method Not Allowed"
    headers = [("Content-Type", "text/plain")]
    response = b"405: method not allowed"

server = make_server("", 9292, app)
try:
    server.serve_forever()
except KeyboardInterrupt:
    server.serve_close()