# retoBack (Inventario-Ventas)

El reto consiste en el desarrollo de una solucion para gestionar el manejo de inventario y ventas de dichos productos.

Debe contener un C.R.U.D (Create, Read, Update, Delete) para la operacion de inventario y debe permitir el registro de las compras despues de su previa validacion de existencias y cantidades minimas y maximas autorizadas para la venta del producto, tambien se debe contar con un api para poder revisar el historial de las ventas realizadas.

## Conexion con la base de datos

En este caso se utilizo una base de datos no relacional llamada Mongodb.

La conexion es un proceso simple, primero debes crear un cluster en la pagina de Mongodb, si no sabes como hacerlo aqui te dejo un pequeño video 

(https://www.youtube.com/watch?v=EjQfQBf0oCw&ab_channel=FreeCode2009)

despues de tener tu base de datos Creada debes colocar el siguiente comando en el archivo properties del proyecto 

`spring.data.mongodb.uri=mongodb+srv:` 

despues de los dos puntos pones el enlace de conexion brindado por la base de datos con tu informacion y LISTO

las colesciones de productos o compras se crean automaticamente al hacer las peticiones post.

##Pruebas

### A continuacion dejo un enlace del entorno de trabajo de postan con las peticiones :

(https://www.postman.com/winter-satellite-541952/workspace/back-reto/collection/18306598-2018d4ae-8073-4ec0-90ac-5c125f1800c5?action=share&creator=18306598)

El enlace redirecciona a un ambiente de trabajo de postan en el cual esta toda la documentacion necesaria para hacer las pruebas

una vez en el entorno de trabajo se debe hacer Clic en cualqueir coleccion y luego Clic en ver la documentacion:

![postman](https://user-images.githubusercontent.com/91062857/213842754-0761824b-f1dd-48f2-bcf8-3c7eb65d85a5.jpeg)

Te saldra toda la documentacion necesaria :

![documentation](https://user-images.githubusercontent.com/91062857/213842772-947b3c31-e15b-4d5e-94bf-b0a893b2bcac.jpeg)






