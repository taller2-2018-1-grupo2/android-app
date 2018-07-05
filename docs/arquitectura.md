# Stories - Android App
---

## Tecnologías involucradas
---
***Stories*** fue desarrollada utilizando el SDK (Software Development Kit) de Android version 21 y Android Studio v3.1.1, sobre la base de Java 7.x.

Además, las siguientes son algunas librerías de Java que fueron utilizadas para su desarrollo y funcionamiento:
* [CircleImageView](https://github.com/hdodenhof/CircleImageView)
* [Firebase](https://firebase.google.com/docs/android/setup?hl=es-419)
* [Facebook Login](https://developers.facebook.com/docs/facebook-login/android/)
* [Google Play Services](https://developers.google.com/android/guides/overview)
* [GSON](https://github.com/google/gson)
* [AndroidPhotoFilters](https://github.com/emonm/AndroidPhotoFilters)
* [ButterKnife](http://jakewharton.github.io/butterknife/)
* [Dexter](https://github.com/Karumi/Dexter)
* [Picasso](http://square.github.io/picasso/)

## Arquitectura
---
### Estructura General
---
La aplicación se encuentra estructurada en los siguientes paquetes:

* ***/activities***: Aquí se encuentra la logica pertinente a cada actividad (pantalla) de la aplicación . A su vez, en la carpeta **/res/layout** cada una de estas clases tiene una contraparte en XML para definir su aspecto y representación visual.
* ***/adapters***: En este paquete tenemos todas las clases que se ocupan de manejar la definición y actualización de las RecyclerView de la aplicación. Estas incluyen listas de historias subidas a la aplicación, mensajes de chat, etc.
* ***/exceptions***: Todas las clases que definen las excepciones manejadas en nuestra aplicación.
* ***/models***: Clases que definen algunos modelos de datos que se utilizan a lo largo de la aplicación (ej: User, Message, Story, etc.)
* ***/services***: Aquí tenemos todas las clases (servicios) que se ocupan de llevar a cabo las operaciones asincronas de la aplicación, fundamentalmente HTTP Requests al App Server para la obtención de datos necesarios para el funcionamiento de la misma.
* ***/utils***: Como su nombre sugiere, son todas las clases utilitarias que se usan a lo largo de la aplicación.

### Decisiones de Arquitectura
---
#### Activities vs Fragments
---
A lo largo de la aplicación se decidió estructurar las interfaces de usuario en Activities en lugar de Fragments debido a que, más alla de algunas mejoras visuales que el uso de Fragments podía ofrecer, el uso de los mismos implicaba un overhead de tiempo de desarrollo con el que el equipo no contaba y es por esta razón que a lo largo de la aplicación se utilizaron siempre (o mayoritariamente) Activities.

#### Persistencia de Datos
---
Para facilitar la persistencia de algunos datos necesarios de forma global en la aplicación (entre Activities, Services, etc.) se utilizó la clase ***LocalStorage*** (encontrada en el paquete **/utils**). De esta forma, todas las activities pueden acceder a sus métodos y atributos (estáticos) para obtener y modificar estos datos.

#### Uso de Services
---
Para obtener un mayor nivel de reutilización de código y clases más sencillas, se utilizaron **Services** para las tareas asincronas de la aplicación, fundamentalmente las HTTP Requests que la misma necesita realizar al App Server para la obtención y envío de datos.

#### Manejo de imagenes
---
En la mayoría de los casos, salvo excepciones como la subida de historias con sus imagenes o videos asociados (los cuales utilizan Firebase Cloud Storage para su almacenaje), las imagenes fueron codificadas y enviadas al App Server utilizando ***Base64***.

#### Fijado de orientación de pantalla
---
El conjunto de Activities encontrado en la aplicación tiene su orientación fijada en modo ***Portrait*** (vertical), debido a que los cambios de orientación no ofrecían ventajas en el uso de la aplicación y la adaptación de la misma para el uso de ambas orientaciones demandaría una carga de tiempo que era imposible de afrontar para el equipo de desarrollo.

#### Uso de RecyclerView vs ListView
---
A través del proceso de desarrollo, se tomó la decisión de utilizar ***RecyclerView***'s en lugar de ***ListView***'s, ya que las últimas se encuentran casi deprecadas y tienen limitaciones visuales que fueron solucionadas con el advenimiento de las antes mencionadas RecyclerView's.

#### Feedback de carga ante situaciones de demora
---
Se utilizaron ***Snackbars*** para señalizar el proceso de carga de datos en la mayoría de los casos donde se espera una demora producto de dificultades de conexión ante solicitudes HTTP.
En contadas excepciones, se decidió obviar la presencia de un Snackbar debido a la existencia de un formulario. En casos de demora, se bloquea el botón del formulario que sirve para accionar el envío de los datos, dando a entender al usuario que se esta llevando a cabo el proceso de carga de los mismos.

## Diseño
---
### Theme
---
Para simplificar el diseño de interfaces de la aplicación, se utilizó un Theme global para todos los elementos de la misma, ***Material Light***. Asimismo, se realizaron modificaciones sobre el mismo para adaptar a nuestra visión estética y funcional de la aplicación, sobre todo en la gama de colores utilizados.

Un aspecto a tener en cuenta es que se utilizó la versión ***No Action Bar*** del Material Light, ya que la Action Bar de cada actividad fue personalizada y provista por nosotros de manera contextual para lograr una mayor flexibilidad y adaptación a las necesidades puntuales de cada momento.

### Fuentes
---
Se utilizaron 2 (dos) fuentes a lo largo de la aplicación, ambas provistas por **Google Fonts**:
* ***Lobster***: principalmente utilizada para titulos y Action Bar.
* ***Raleway***: utilizada a lo largo del resto de la aplicación (en botones, TextView, EditText, etc.)

### Iconos
---
Los iconos fueron adaptados en formato XML del catalogo de iconos provisto por Android en su librería de base.

## Compatibilidad
---
Un aspecto de la aplicación a tener en cuenta fue la decisión tomada de restringir el uso de la misma a dispositivos con (minimamente) ***API 21***. Esto significa dispositivos que utilicen el sistema operativo ***Android 5.0 (Lollipop)*** en adelante. Esta fue una decisión tomada en base a los [datos](https://www.appbrain.com/stats/top-android-sdk-versions) que muestran la distribución de dispositivos por SDK:

| Android SDK Version        | Market Share           |
| ------------- |:-------------:|
|**7.0-7.1 (Nougat)**     | **31.5%** |
| **6.0 (Marshmallow)**   | **22.9%**      |
| **5.0-5.1 (Lollipop)** | **19.7%**      |
| 4.4 (KitKat)  | 10.5%  |
|  **8.0-8.1 (Oreo)** |  **10.1%** |
|  4.1-4.3 (Jelly Bean) | 4.5%  |
| 4.0 (Ice Cream Sandwich)  | 0.4%  |
| 2.3 (Gingerbread)  | 0.4%  |
| 3.0-3.2 (Honeycomb)  | 0.0% |

Estos datos nos muestran que, con la decisión tomada, nuestra aplicación tiene una cobertura superior al **84%**, lo cual fue analizado en pos de los costos vs beneficios de usar un SDK más antiguo y las limitaciones de librerías que conlleva, y resulto una cobertura suficiente.

