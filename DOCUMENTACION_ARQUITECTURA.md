# 🚗 Documentación de Arquitectura y Guía Técnica del Proyecto
**Sistema Móvil de Gestión de Inventario, Ventas y Reposición Automotriz**  
*Taller Mecánico Puente Alto · MVP en Kotlin (Jetpack Compose + MVVM + Clean Architecture)*

---

## 1. 📌 Resumen Ejecutivo del Proyecto

Este proyecto es una aplicación móvil nativa para Android diseñada específicamente para la operativa diaria de un taller mecánico automotriz en Puente Alto. Permite resolver tres problemáticas críticas:
1. **Control de stock en tiempo real:** Registro de repuestos, lubricantes y consumibles con validación estricta que impide existencias negativas.
2. **Registro ágil de movimientos:** Operaciones de venta en mostrador, consumo en servicios de taller, recepciones de compras y ajustes de inventario físico ejecutadas de forma atómica.
3. **Módulo inteligente de reposición:** Detección de productos en stock crítico o agotados, cálculo automático de pedidos por fórmula (`TargetStock - CurrentStock`), agrupación por proveedor y generación de borradores de correo mediante Intents nativos sin almacenar credenciales externas.

---

## 2. 🏛️ Arquitectura General del Software

La aplicación sigue los principios de **Clean Architecture** combinados con el patrón **MVVM (Model-View-ViewModel)** y **Unidirectional Data Flow (UDF)** recomendado por Google.

```
┌─────────────────────────────────────────────────────────────┐
│                          UI LAYER                           │
│  Jetpack Compose Screens + ViewModels (StateFlow) + Theme   │
└──────────────────────────────┬──────────────────────────────┘
                               │ Observa UiState / Emite Eventos
┌──────────────────────────────▼──────────────────────────────┐
│                        DOMAIN LAYER                         │
│  Modelos Puros + Casos de Uso (Use Cases) + Interfaces Repo │
└──────────────────────────────┬──────────────────────────────┘
                               │ Implementa Interfaces / Acceso Datos
┌──────────────────────────────▼──────────────────────────────┐
│                         DATA LAYER                          │
│  Room Database (Offline-First) + DAOs + Retrofit + Repos    │
└─────────────────────────────────────────────────────────────┘
                               ▲
                               │ Proveedor de dependencias
┌──────────────────────────────┴──────────────────────────────┐
│                           DI (Hilt)                         │
└─────────────────────────────────────────────────────────────┘
```

### Principios de Diseño Aplicados:
- **Offline-First:** Room Database actúa como la única fuente de la verdad (SSOT). Toda la UI reacciona a `Flow` emitidos desde la base de datos local.
- **Inversión de Dependencias (DIP):** La capa de Dominio no conoce a Room, SQLite ni Retrofit; solo define interfaces de repositorios que la capa de Datos implementa.
- **Seguridad Basada en Roles (RBAC):** Roles diferenciados (`OWNER` / Propietario vs `OPERATOR` / Operador) para bloquear acciones críticas como eliminación de productos o modificación de stock mínimo.
- **Atomicidad Transaccional:** Uso de transacciones en Room para garantizar que la actualización del stock y el guardado del movimiento ocurran en conjunto o se reviertan.

---

## 3. 📂 Estructura Detallada de Paquetes (`Package Structure`)

Todo el código fuente principal reside en `app/src/main/java/com/example/dsy1105_006d_lc7/`:

```text
com.example.dsy1105_006d_lc7/
│
├── InventoryApp.kt                     # Entrada @HiltAndroidApp de la aplicación
├── MainActivity.kt                     # Actividad única con @AndroidEntryPoint y Compose setup
│
├── data/                               # CAPA DE DATOS (Persistencia y Red)
│   ├── local/
│   │   ├── AppDatabase.kt              # Definición de la Base de Datos Room y TypeConverters
│   │   ├── dao/
│   │   │   ├── ProductDao.kt           # Consultas SQL y transacciones atómicas para productos
│   │   │   ├── MovementDao.kt          # Consultas para historial de movimientos
│   │   │   ├── LocationDao.kt          # Consultas para ubicaciones en bodega
│   │   │   └── SupplierDao.kt          # Consultas para proveedores
│   │   └── entities/
│   │       ├── ProductEntity.kt        # Entidad Room de productos
│   │       ├── InventoryMovementEntity.kt # Entidad Room de movimientos
│   │       ├── LocationEntity.kt       # Entidad Room de ubicaciones físicas
│   │       ├── SupplierEntity.kt       # Entidad Room de proveedores
│   │       └── Converters.kt           # Convertidores de tipos para Room (List<String>)
│   ├── remote/
│   │   ├── api/
│   │   │   └── ApiService.kt           # Definición de endpoints REST con Retrofit
│   │   └── dto/
│   │       └── ProductDto.kt           # Data Transfer Objects para serialización JSON
│   └── repository/
│       ├── Mappers.kt                  # Extension functions para mapear Entity <-> Domain Model
│       ├── ProductRepositoryImpl.kt    # Implementación concreta del repositorio de productos
│       ├── MovementRepositoryImpl.kt   # Implementación concreta del repositorio de movimientos
│       ├── LocationRepositoryImpl.kt   # Implementación concreta del repositorio de ubicaciones
│       └── SupplierRepositoryImpl.kt   # Implementación concreta del repositorio de proveedores
│
├── domain/                             # CAPA DE DOMINIO (Reglas de negocio puras)
│   ├── model/
│   │   ├── Product.kt                  # Modelo de dominio limpio para producto
│   │   ├── InventoryMovement.kt        # Modelo de dominio para movimiento de stock
│   │   ├── Location.kt                 # Modelo de dominio para ubicación de bodega
│   │   ├── Supplier.kt                 # Modelo de dominio para proveedor
│   │   ├── ReplenishmentReport.kt      # Modelos para órdenes y reportes de reposición
│   │   └── UserSession.kt              # Sesión de usuario y enums de roles con permisos
│   ├── repository/
│   │   ├── ProductRepository.kt        # Interfaz del repositorio de productos
│   │   ├── MovementRepository.kt       # Interfaz del repositorio de movimientos
│   │   ├── LocationRepository.kt       # Interfaz del repositorio de ubicaciones
│   │   └── SupplierRepository.kt       # Interfaz del repositorio de proveedores
│   └── usecase/
│       ├── RegisterMovementUseCase.kt  # Validación de stock no negativo y registro atómico
│       ├── GetLowStockProductsUseCase.kt # Filtrado reactivo de productos críticos
│       ├── BuildSupplierEmailIntentUseCase.kt # Construcción de Intent de correo prellenado
│       └── SearchProductsUseCase.kt    # Búsqueda reactiva de productos por texto
│
├── ui/                                 # CAPA DE PRESENTACIÓN (Jetpack Compose)
│   ├── theme/
│   │   ├── Color.kt                    # Paleta automotriz, colores semánticos de stock
│   │   ├── Type.kt                     # Escala tipográfica Material 3
│   │   └── Theme.kt                    # Tema claro y oscuro dinámico del sistema
│   ├── navigation/
│   │   ├── Screen.kt                   # Rutas tipo-seguras con parámetros
│   │   └── AppNavigation.kt            # NavHost principal, Bottom Navigation Bar y Drawer
│   ├── components/
│   │   ├── Badges.kt                   # Badges de stock, badges de movimientos y StockLevelBar
│   │   ├── Cards.kt                    # Tarjetas de productos, métricas de dashboard
│   │   ├── SearchBar.kt                # Barra de búsqueda animada con debounce
│   │   └── BarcodeScannerModal.kt      # Modal de escaneo de código de barras/QR interactivo
│   └── screens/
│       ├── auth/
│       │   ├── LoginScreen.kt          # Pantalla de inicio de sesión con Material 3
│       │   └── LoginViewModel.kt       # Manejo de credenciales y roles Propietario/Operador
│       ├── product_list/
│       │   ├── ProductListScreen.kt    # Lista principal con dashboard y métricas
│       │   └── ProductListViewModel.kt # StateFlow con búsqueda y conteos reactivos
│       ├── product_detail/
│       │   ├── ProductDetailScreen.kt  # Ficha técnica, precios, margen e historial
│       │   └── ProductDetailViewModel.kt # Carga de relaciones (producto, proveedor, movimientos)
│       ├── product_entry/
│       │   ├── AddEditProductScreen.kt # Formulario de creación/edición con escáner integrado
│       │   └── AddEditProductViewModel.kt # Validaciones y persistencia con restricción de roles
│       ├── movement/
│       │   ├── RegisterMovementScreen.kt # Registro ágil de entradas, ventas y consumos
│       │   └── MovementViewModel.kt    # Calculadora de balance y alertas preventivas
│       ├── replenishment/
│       │   ├── ReplenishmentScreen.kt  # Agrupación por proveedor y pedidos
│       │   └── ReplenishmentViewModel.kt # Cálculo de cantidades y preparación de mailto:
│       └── location/
│           ├── LocationScreen.kt       # Mapa de ubicaciones en bodega ("BOD-A-E1-R1-P1")
│           └── LocationViewModel.kt    # Filtros por zona (Bodega A, B, Taller)
│
├── di/                                 # INYECCIÓN DE DEPENDENCIAS (Hilt)
│   ├── AppModule.kt                    # Proveedores generales de repositorios
│   ├── DatabaseModule.kt               # Configuración de Room, DAOs y prepoblación inicial
│   └── NetworkModule.kt                # Proveedor de Retrofit, OkHttp y Moshi
│
└── util/                               # UTILIDADES DEL SISTEMA
    └── StockNotificationHelper.kt      # Gestor de notificaciones push locales para stock crítico
```

---

## 4. 🧩 Detalle de Qué Encontrar en Cada Módulo

### 4.1. Capa de Datos (`data/`)
| Archivo | Qué contiene / Para qué sirve |
| :--- | :--- |
| `AppDatabase.kt` | Clase abstracta de Room con versión de esquema y TypeConverters. Define los 4 DAOs principales. |
| `ProductDao.kt` | Queries SQL para consultar repuestos por categoría, proveedor, stock bajo (`currentStock <= minStock`), y método transaccional para actualizar inventario. |
| `MovementDao.kt` | Queries para registrar y ordenar cronológicamente los movimientos de cada repuesto. |
| `LocationDao.kt` | Queries para consultar ubicaciones físicas de bodega y filtrar las disponibles. |
| `SupplierDao.kt` | Queries para listar proveedores activos y consultar información de contacto. |
| `ProductEntity.kt` | Entidad Room que mapea la tabla `products` con claves foráneas lógicas `supplierId` y `locationId`. |
| `DatabaseModule.kt` | Inicialización de la base de datos Room e inserción de datos iniciales de demostración (repuestos reales de taller: aceites sintéticos, pastillas cerámicas, filtros Mann, amortiguadores Monroe). |

### 4.2. Capa de Dominio (`domain/`)
| Archivo | Qué contiene / Para qué sirve |
| :--- | :--- |
| `Product.kt` | Data class limpia con propiedades calculadas: `isLowStock`, `isOutOfStock`, `replenishmentQuantity` y `profitMargin`. |
| `InventoryMovement.kt` | Modelo para transacciones con el enum `MovementTypeDomain` (`ENTRY`, `SALE`, `SERVICE_CONSUMPTION`, `ADJUSTMENT`, `RETURN`). |
| `UserSession.kt` | Modelo de sesión que define los permisos de los roles: `UserRole.OWNER` (acceso completo) y `UserRole.OPERATOR` (restringido sin acceso a borrado ni alteración de stock mínimo). |
| `RegisterMovementUseCase.kt` | **Regla de oro de inventario:** Comprueba que `resultingStock >= 0`. Si un usuario intenta registrar una venta o consumo mayor al stock actual, lanza una excepción clara impidiendo la operación. |
| `BuildSupplierEmailIntentUseCase.kt` | Genera un `Intent` nativo con `Intent.ACTION_SENDTO` y URI `mailto:`. Llena el destinatario, asunto y cuerpo con formato tabular legible. |
| `GetLowStockProductsUseCase.kt` | Emite una lista reactiva (`Flow<List<Product>>`) con todos los repuestos que necesitan reposición urgente. |

### 4.3. Capa de Presentación (`ui/`)
| Pantalla / Componente | Descripción y Funcionalidad |
| :--- | :--- |
| `LoginScreen.kt` | Pantalla de bienvenida con degradado y tarjeta de autenticación. Permite ingresar con credenciales de prueba (`admin` / `admin123` para Propietario, `operador` / `op123` para Operador). |
| `ProductListScreen.kt` | Vista principal con panel superior de métricas (Total, Bajo Stock, Agotados), barra de búsqueda en tiempo real y lista de tarjetas con barras de nivel. |
| `ProductDetailScreen.kt` | Vista exhaustiva de un repuesto. Muestra margen comercial (costo vs venta), proveedor, posición en bodega y tabla cronológica de todos los movimientos históricos. |
| `AddEditProductScreen.kt` | Formulario ergonómico con botón de escáner de código de barras, selectores desplegables de categoría, unidad de medida, proveedor y estante. |
| `RegisterMovementScreen.kt` | Diseñado para registrar operaciones con una sola mano en el taller: incluye steppers (-1, +1, +5, +10), calculadora en tiempo real de stock resultante y alerta visual inmediata si el resultado sería negativo. |
| `ReplenishmentScreen.kt` | Agrupa automáticamente los repuestos críticos por proveedor, muestra el total de inversión estimada y permite pulsar "Generar Borrador Email" para abrir la app de correo del dispositivo. |
| `LocationScreen.kt` | Mapa de estantes y repisas del taller con etiquetas de ocupación y repuestos almacenados en cada posición. |
| `BarcodeScannerModal.kt` | Simulación interactiva de escáner con visor láser animado, botones de prueba rápida y campo de entrada manual. |

---

## 5. ⚖️ Reglas de Negocio Implementadas

### Regla 1: Control Estricto de Stock No Negativo
En `RegisterMovementUseCase.kt`:
```kotlin
val newStock = when (movementType) {
    MovementTypeDomain.ENTRY, MovementTypeDomain.RETURN -> previousStock + quantity
    MovementTypeDomain.SALE, MovementTypeDomain.SERVICE_CONSUMPTION -> previousStock - quantity
    MovementTypeDomain.ADJUSTMENT -> quantity
}

if (newStock < 0) {
    return Result.failure(
        IllegalStateException("Stock insuficiente. Disponible: $previousStock, solicitado: $quantity")
    )
}
```
Esto asegura que ni ventas ni consumos en servicios puedan dejar existencias en números negativos.

### Regla 2: Fórmula Inteligente de Reposición Sugerida
Para evitar desabastecimiento de piezas con alta rotación:
$$\text{Cantidad Sugerida} = \max(\text{TargetStock} - \text{CurrentStock}, 1)$$
Donde $\text{TargetStock}$ es por defecto $\text{Stock Mínimo} \times 2$ (o el parámetro configurable del producto).

### Regla 3: Privacidad y Envío No Intrusivo de Correos
La aplicación **no** pide contraseñas de correo ni envía correos en segundo plano (lo cual violaría políticas de seguridad y requeriría credenciales SMTP inseguras en el cliente). En su lugar, usa un Intent implícito del sistema operativo:
```kotlin
Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:")
    putExtra(Intent.EXTRA_EMAIL, arrayOf(supplier.email))
    putExtra(Intent.EXTRA_SUBJECT, "Solicitud de Reposición - Taller Automotriz Puente Alto")
    putExtra(Intent.EXTRA_TEXT, cuerpoTabulado)
}
```

### Regla 4: Notificaciones Locales de Stock Crítico
En `StockNotificationHelper.kt`, cuando un movimiento deja un repuesto con existencias iguales o inferiores al stock mínimo, se emite una notificación local de alta prioridad en el canal `stock_alerts_channel`, alertando al operario para solicitar reposición inmediata.

---

## 6. 🧭 Guía de Rutas de Navegación (`Screen.kt`)

| Ruta | Parámetros | Destino |
| :--- | :--- | :--- |
| `login` | Ninguno | `LoginScreen` |
| `product_list` | Ninguno | `ProductListScreen` (Pestaña 1) |
| `movement?productId={productId}` | `productId` (Opcional, default -1) | `RegisterMovementScreen` (Pestaña 2) |
| `replenishment` | Ninguno | `ReplenishmentScreen` (Pestaña 3) |
| `location` | Ninguno | `LocationScreen` (Pestaña 4) |
| `product_detail/{productId}` | `productId` (Long obligatorio) | `ProductDetailScreen` |
| `product_entry?productId={productId}` | `productId` (Opcional para edición) | `AddEditProductScreen` |

---

## 7. 🎓 Preguntas Frecuentes para Estudio y Evaluaciones Técnicas

### P1: ¿Por qué se utilizó Room en lugar de guardar datos únicamente en una API remota?
**Respuesta:** En talleres mecánicos suele haber zonas con poca conectividad (fosas, galpones o bodegas subterráneas). El patrón **Offline-First** garantiza que el inventario se pueda consultar y modificar sin interrupciones, garantizando disponibilidad y rapidez.

### P2: ¿Cómo se comunican el ViewModel y la UI en Jetpack Compose?
**Respuesta:** El ViewModel expone un `StateFlow<UiState>` inmutable. La UI de Compose se suscribe a este estado mediante `collectAsStateWithLifecycle()`, lo que asegura que la vista se recomponga solo cuando el estado cambia y detiene la recolección cuando la pantalla pasa a segundo plano, optimizando batería y memoria.

### P3: ¿Dónde se encuentran las validaciones de negocio?
**Respuesta:** En la capa de dominio (`domain/usecase/`), específicamente en `RegisterMovementUseCase.kt`. Al desacoplar las reglas de los ViewModels y de los DAOs, las reglas son 100% testeables con pruebas unitarias puras sin necesidad de emuladores ni mocks de Android.

### P4: ¿Cómo se maneja la seguridad de roles?
**Respuesta:** El modelo `UserSession` encapsula `UserRole` (`OWNER` vs `OPERATOR`). Los composables y ViewModels consultan propiedades booleanas como `userRole.canDeleteProducts` o `userRole.canModifyMinStock` para habilitar u ocultar componentes en pantalla.

---

## 8. 🛠️ Comandos de Compilación y Ejecución

* **Compilar código Kotlin y verificar tipos:**
  ```powershell
  .\gradlew.bat compileDebugKotlin
  ```
* **Generar APK instalable:**
  ```powershell
  .\gradlew.bat assembleDebug
  ```
* **Ubicación del APK resultante:**
  `app/build/outputs/apk/debug/app-debug.apk`
