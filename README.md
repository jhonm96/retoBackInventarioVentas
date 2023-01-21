# retoBack (Inventario-Ventas)

El reto consiste en el desarrollo de una solucion para gestionar el manejo de inventario y ventas de dichos productos.

Debe contener un C.R.U.D (Create, Read, Update, Delete) para la operacion de inventario y debe permitir el registro de las compras despues de su previa validacion de existencias y cantidades minimas y maximas autorizadas para la venta del producto, tambien se debe contar contar con un api para poder revisar el historial de las ventas realizadas.

##Conexion con la base de datos

En este caso de la solucion se utilizo una base de datos no relacional llamada Mongodb.

La conexion es un proceso simple, primero debes crear un cluster en la pagina de Mongodb, si no sabes como hacerlo aqui te dejo un pequeño video 
(https://www.youtube.com/watch?v=EjQfQBf0oCw&ab_channel=FreeCode2009)

despues de tener tu sabe de datos Creada debes colocar el siguiente comando en el archivo properties del proyecto 
'spring.data.mongodb.uri=mongodb+srv:' y despues de los dos puntos pones el enlace de conexion brindado por la base de datos con tu informacion y LISTO

las colesciones de productos oc ompras se crean automaticamente al hacer las peticiones post  `aqui va algo`

