from wsgiref.simple_server import make_server
import json

# Definimos el diccionario que contendrá todas las tareas.
tasks = {}

# Definimos el último id utilizado.
last_id = 0

def app(environ, start_response):
    # Globalizamos la variable previamente definida.
    global last_id
    
    # Definimos las variables que vamos a usar.
    response = b""
    status = "200 OK"
    headers = [("Content-Type", "application/json; charset=utf-8")]

    # Obtenemos la ruta y la dividimos en partes.
    path = environ["PATH_INFO"].strip("/").split("/")

    match environ["REQUEST_METHOD"]:
        # Caso 1: Request GET
        case "GET":
            if len(path) == 1 and path[0] == "tasks":
                response = json.dumps(tasks).encode("utf-8")
            elif len(path) == 2 and path[0] == "tasks":
                id = int(path[1])
                if id in tasks:
                    response = json.dumps(tasks[id]).encode("utf-8")
                else:
                    status, headers, response = status404()

        # Caso 2: Request POST
        case "POST":
            # Leemos el tamaño de la entrada.
            content_length = int(environ.get("CONTENT_LENGTH"))
            
            # Leemos la entrada.
            input = environ["wsgi.input"].read(content_length)
            new_task = json.loads(input)

            # Guardamos la tarea.
            tasks[last_id] = new_task

            # Agregamos el id a la tarea y actualizamos el último id.
            new_task["id"] = last_id
            last_id += 1

            status = "201 Created"
            response = json.dumps(new_task).encode("utf-8")

        # Caso 3: Request PATCH
        case "PATCH":
            #Obtenemos el id de la tarea y verificamos si existe en nuestra lista de tareas.
            id = int(path[1])
            if id in tasks:
                # Leemos el tamaño de la entrada.
                content_length = int(environ.get("CONTENT_LENGTH"))
                
                # Leemos la entrada y actualizamos la tarea.
                input = environ["wsgi.input"].read(content_length)
                updated_task = json.loads(input)
                tasks[id].update(updated_task)

                response = json.dumps(tasks[id]).encode("utf-8")
            # Si no está, devolvemos 404.
            else:
                status, headers, response = status404()

        # Caso 4: Request DELETE
        case "DELETE":
            #Obtenemos el id de la tarea y verificamos si existe en nuestra lista de tareas.
            id = int(path[1])
            if id in tasks:
                # Si existe, la eliminamos.
                del tasks[id]
                status, headers, response = status204()
            # Si no está, devolvemos 404.
            else:
                status, headers, response = status404()
            
        case _:
            status, headers, response = status405()

    start_response(status, headers)
    return [response]

def status204():
    return "204 No Content", [("Content-Type", "text/plain")], b"204: no content"

def status404():
    return "404 Not Found", [("Content-Type", "text/plain")], b"404: not found"

def status405():
    return "405 Method Not Allowed", [("Content-Type", "text/plain")], b"405: method not allowed"

with make_server("", 9292, app) as server:
    print("Listening on http://localhost:9292")
    server.serve_forever()