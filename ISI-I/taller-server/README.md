# Servidor WSGI - Ingeniería de Software I
En esta tarea del taller, implementamos un servidor WSGI que nos permite administrar una lista de tareas, mediante el uso de los verbos HTTP: GET, POST, PATCH y DELETE.

## Diferencia entre los verbos HTTP

**GET**: Nos permite obtener información del servidor, el cual no debe modificar el estado del servidor. 

**POST**: Con este verbo podemos crear un nuevo recurso en el servidor, cada vez que enviamos esta petición.

**PATCH**: Esta request nos posibilita la modificación parcial de un recurso que existe en el servidor. Solo se modifican los campos que enviamos en el cuerpo de la petición.

**DELETE**: Nos concede la facultad de eliminar un recurso existente en el servidor.

## ¿Por qué POST no es idempotente?

Entendemos por **idempotente** a un recurso cuando al ejecutarlo varias veces se produce el mismo resultado.
Particularmente con la request POST, cada vez que la utilizamos creamos un recurso nuevo en el servidor, por lo tanto, **no** es idempotente.