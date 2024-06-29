# OurProject
Protección: SQLI y XSS
Forma consulta din:  spring 325 query by example
Usar con example lo q pasa el usuario
Sanitizar lo del usuario prepared statement

Texto enriquecido (con formato) (revisar guardado y protección)

10-Buscar por ciertos parámetros que da el usuario (no todos)
Mínimo 2 filtros - combinatoria

Usar como BBDD MySQL

Relaciones nm buscar por usuario y unirlo a la clase

Simular q hay solo un usuario

Probar a borrar una clase con usuarios
Arquitectura diapo 141


12-Poder encontrar el archivo según el nombre con el que se subió
Guardar en el sistema de ficheros con le nombre original

Protegerse de como recuperamos el fichero

Solución: antes de hacer el transfer -> hacemos:

var pathImage = imagePath.normalize().toAbsolutePath().toString();
var pathFolder = IMAGES_FOLDER.toAbsolutePath().toString();
if(pathImage.startsWith(pathFolder)){}else{}

!!!!!!!! completar el xss