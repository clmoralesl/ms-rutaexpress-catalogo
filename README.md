# ms-rutaexpress-catalogo

Microservicio de dominio responsable de la gestion del catalogo de servicios de ultima milla, calculo de tarifas base y por kilometro/peso, y control atomico y thread-safe de la capacidad vehicular de la flota de couriers PyME para la plataforma RutaExpress.

---

## 1. Caracteristicas Principales

* **Framework:** Spring Boot 3.3.5 / Java 17.
* **Persistencia:** Oracle Cloud Autonomous Database (Esquema `USER_CATALOGO`) con soporte para conexiones seguras mediante Oracle Wallet.
* **Puerto de operacion:** `8082`.
* **Control de Capacidad:** Descuento atomico a nivel de base de datos bajo aislamiento transaccional para evitar condiciones de carrera (*Race Conditions*) en ventas de alta concurrencia.

---

## 2. Endpoints de la API REST

Todos los endpoints estan definidos bajo la convencion de nomenclatura en espanol:

| Metodo | Endpoint | Descripcion | Codigo Exitoso | Codigos de Error |
|---|---|---|---|---|
| `GET` | `/api/catalogo/servicios` | Listado de servicios logisticos activos con tarifas y cupos | `200 OK` | `500 Internal Error` |
| `GET` | `/api/catalogo/servicios/{id}` | Obtener el detalle de un servicio por su identificador | `200 OK` | `404 Not Found` |
| `POST` | `/api/catalogo/servicios` | Registrar un nuevo tipo de servicio logistico | `201 Created` | `400 Bad Request` |
| `PUT` | `/api/catalogo/servicios/{id}` | Actualizar tarifas base, precios por kilo o cupo maximo | `200 OK` | `400 Bad Request`, `404 Not Found` |
| `PATCH` | `/api/catalogo/servicios/{id}/descontar-capacidad` | Reducir en 1 la capacidad disponible ante la aceptacion de un envio | `200 OK` | `404 Not Found`, `409 Conflict` |
| `PATCH` | `/api/catalogo/servicios/{id}/restablecer-capacidad` | Restablecer cupo al maximo diario (cierre diario o cancelaciones) | `200 OK` | `404 Not Found` |
| `GET` | `/api/publico/salud` | Endpoint publico de healthcheck | `200 OK` | - |
| `GET` | `/actuator/health` | Estado de salud estandar de Spring Boot Actuator | `200 OK` | - |

---

## 3. Modelo de Dominio: ServicioCatalogo

| Campo | Tipo | Descripcion | Restricciones |
|---|---|---|---|
| `id` | `Long` | Identificador autogenerado (Identity / Secuencia) | Clave Primaria |
| `codigo` | `String` | Codigo de negocio (`EXPRESS`, `ESTANDAR`, etc.) | Unico, No nulo |
| `nombre` | `String` | Nombre comercial del servicio | Max 100 caracteres |
| `descripcion` | `String` | Descripcion operativa del tipo de entrega | Max 500 caracteres |
| `tipoVehiculo` | `String` | Vehiculo asignado (`MOTO`, `FURGON`, `CAMION`) | No nulo |
| `tarifaBase` | `BigDecimal` | Tarifa minima de partida en CLP | Valor >= 0.0 |
| `precioPorKg` | `BigDecimal` | Recargo por kilogramo excedente en CLP | Valor >= 0.0 |
| `capacidadMaximaDiaria` | `Integer` | Cupo total de encomiendas por jornada | Valor >= 1 |
| `capacidadDisponible` | `Integer` | Cupos restantes en tiempo real | 0 <= disponible <= maxima |
| `activo` | `Boolean` | Indicador de disponibilidad operativa | Por defecto `true` |

---

## 4. Ejecucion de Pruebas Unitarias y de Integracion

```bash
mvn clean test
```

Las pruebas validan:
1. Respuestas de salud publica en `/api/publico/salud` y `/actuator/health`.
2. Listado, creacion y actualizacion de tipos de servicio.
3. Descuento atomico de cupos.
4. Rechazo con `HTTP 409 Conflict` cuando la capacidad disponible es 0.
