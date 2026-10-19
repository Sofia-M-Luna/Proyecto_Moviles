# 🏷️ Lista de Productos y Códigos para Escaneo QR
**Catálogo de Repuestos y Productos de Taller Automotriz**  
*Base de datos local inicial (`taller_automotriz_db`)*

Esta lista contiene los **IDs** y **códigos** de los productos registrados en el sistema. Puedes usar cualquiera de los siguientes valores para generar códigos QR (o ingresarlos directamente en la app) y probar el lector QR en el registro de movimientos.

---

## 📋 Tabla de Productos

| ID (Valor para QR) | Código Alternativo | Nombre del Repuesto | Marca | Categoría | Stock Actual | Stock Mínimo | Precio Ref. |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: | :---: |
| **`1`** | `FIL-ACE-001` | Filtro de Aceite Universal | Mann Filter | Filtros | 15 un. | 5 un. | $6.900 |
| **`2`** | `ACE-5W30-001` | Aceite Motor 5W-30 Sintético | Mobil 1 | Aceites | 3 un. | 10 un. | $14.900 |
| **`3`** | `PAS-FRE-001` | Pastillas de Freno Delanteras | Bosch | Frenos | 8 un. | 4 un. | $28.900 |
| **`4`** | `BUJ-NGK-001` | Bujía de Encendido NGK | NGK | Bujías | 0 un. | 8 un. | $8.900 |
| **`5`** | `COR-DIS-001` | Correa de Distribución | Gates | Correas | 2 un. | 3 un. | $45.000 |
| **`6`** | `FIL-AIR-001` | Filtro de Aire Motor | K&N | Filtros | 12 un. | 5 un. | $22.000 |
| **`7`** | `LIQ-REF-001` | Líquido Refrigerante Verde | Prestone | Refrigerantes | 6 un. | 8 un. | $9.900 |
| **`8`** | `AMO-DEL-001` | Amortiguador Delantero | Monroe | Suspensión | 4 un. | 2 un. | $59.900 |

---

## 📱 Formatos Soportados por el Lector QR

El lector QR implementado en `MovementViewModel` soporta cualquiera de estos formatos en el contenido del código QR:

1. **ID Numérico Directo (Recomendado):**
   * Contenido del QR: `1`
   * Contenido del QR: `2`
   * Contenido del QR: `3`
2. **Formato con Etiqueta:**
   * Contenido del QR: `ID: 1`
   * Contenido del QR: `id=2`
3. **Código de Repuesto (Fallback):**
   * Contenido del QR: `FIL-ACE-001`
   * Contenido del QR: `ACE-5W30-001`

---

## 💡 Casos de Prueba Recomendados para Movimientos

* **Prueba de Venta Normal:** Escanear **ID `1`** (Filtro de Aceite) y registrar salida de 2 unidades (Stock proyectado: 13 un.).
* **Prueba de Alerta de Stock Bajo:** Escanear **ID `7`** (Refrigerante) y retirar 1 unidad (Stock proyectado: 5 un. $\le$ mínimo 8 un.).
* **Prueba de Bloqueo por Stock Negativo:** Escanear **ID `4`** (Bujía NGK, stock actual 0 un.) o **ID `5`** (Correa, stock 2 un.) e intentar vender 3 unidades. El sistema impedirá la transacción.
