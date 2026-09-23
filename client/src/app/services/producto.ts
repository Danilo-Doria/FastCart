import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Producto } from '../models/producto';

// @Injectable: Registra esta clase como un servicio inyectable en Angular.
// providedIn: 'root' asegura que el servicio esté disponible globalmente en toda la aplicación como una única instancia.
@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  // Inyectamos el HttpClient para poder realizar las peticiones HTTP (GET, POST, PUT, DELETE) hacia el backend.
  private http = inject(HttpClient);
  
  private readonly API_URL = `${environment.apiUrl}/productos`;


  // LEER TODOS: Envía una petición GET a la URL base.
  // Retorna un Observable que emitirá un arreglo de objetos tipo 'Producto' cuando el servidor responda.
  // Un Observable en Angular es un objeto que representa un flujo de datos en el tiempo. 
  // Es la herramienta que usa el framework para manejar operaciones asíncronas (cosas que no pasan de inmediato, 
  // como esperar la respuesta de un servidor).
  getProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.API_URL);
  }

  // LEER UNO SOLO: Envía una petición GET agregando el ID a la URL (ej: /productos/5).
  // Retorna un Observable con el objeto 'Producto' específico que coincida con ese ID.
  getProductoById(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.API_URL}/${id}`);
  }

  // CREAR: Envía una petición POST a la URL base mandando los datos del nuevo producto en el cuerpo (body).
  // Aquí es donde el 'id' del producto suele ir vacío (undefined) y el backend se encarga de generarlo.
  crearProducto(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(this.API_URL, producto);
  }

  // ACTUALIZAR: Envía una petición PUT especificando el ID en la URL y los datos modificados en el cuerpo.
  // Sirve para reemplazar por completo o modificar las propiedades de un producto existente.
  actualizarProducto(id: number, producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${this.API_URL}/${id}`, producto);
  }

  // ELIMINAR: Envía una petición DELETE con el ID del producto que se desea borrar.
  // Retorna un Observable de tipo <void> porque habitualmente el servidor solo responde con un estado de éxito (como 200 o 24) sin datos.
  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
