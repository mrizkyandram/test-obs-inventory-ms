Inventory Management System
Inventory Management System adalah aplikasi backend berbasis Spring Boot yang menyediakan RESTful API untuk mengelola data inventaris, item, dan pemesanan (order). Aplikasi ini mendukung operasi CRUD lengkap dengan dukungan pagination menggunakan Spring Data JPA.

Fitur
Inventory API

Get inventory by ID

List all inventory with pagination

Create new inventory

Update existing inventory

Delete inventory

Item API

Get item by ID

List all items with pagination

Create new item

Update existing item

Delete item

Order API

Get order by ID

List all orders with pagination

Create new order

Update existing order

Delete order

Struktur Endpoint
Semua endpoint berada di bawah prefix /api/.

Endpoint	Method	Description
/api/inventory	GET	Get all inventory items
/api/inventory/{id}	GET	Get inventory by ID
/api/inventory	POST	Create inventory
/api/inventory/{id}	PUT	Update inventory by ID
/api/inventory/{id}	DELETE	Delete inventory by ID

| /api/items | GET | Get all items |
| /api/items/{id} | GET | Get item by ID |
| /api/items | POST | Create item |
| /api/items/{id} | PUT | Update item by ID |
| /api/items/{id} | DELETE | Delete item by ID |

| /api/orders | GET | Get all orders |
| /api/orders/{id} | GET | Get order by ID |
| /api/orders | POST | Create order |
| /api/orders/{id} | PUT | Update order by ID |
| /api/orders/{id} | DELETE | Delete order by ID |

Teknologi yang Digunakan
Java 11+
