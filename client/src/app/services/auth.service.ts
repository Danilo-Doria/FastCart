import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, AuthResponse, RefreshTokenRequest } from '../../models/auth.model';

// @Injectable: Le dice a Angular que esta clase es un "Servicio" que se puede inyectar en otras partes de la app (como componentes o interceptores).
// providedIn: 'root' significa que el servicio es global y habrá una sola instancia viva en toda tu aplicación.
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Inyectamos el cliente HTTP de Angular para poder hacer peticiones a servidores externos (POST, GET, etc.).
  private http = inject(HttpClient);
  
  private readonly API_URL = `${environment.apiUrl}/auth`;

  // ESTADO REACTIVO (SIGNALS)
  // isLoggedIn: Una señal booleana (true/false) que le dice a toda la app en tiempo real si el usuario está logueado.
  // Se inicializa llamando a hasToken() para revisar si el usuario ya tenía sesión guardada de antes.
  // Los símbolos <> en TypeScript se utilizan para definir tipos genéricos (Generics).
  isLoggedIn = signal<boolean>(this.hasToken());

  // INICIO DE SESIÓN
  // Envía las credenciales (usuario/contraseña) al backend. Retorna un Observable (un flujo de datos).
  // pipe() es un método de los Observables que funciona como un canal o tubería. Por sí solo no modifica los datos, 
  // sino que sirve para conectar y encadenar operadores de RxJS en un orden específico.
  // tap() es uno de los operadores que se meten dentro del pipe. Su función principal es mirar el dato que pasa y 
  // ejecutar una acción secundaria (un efecto colateral), pero sin alterar el dato original.
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      // tap() ejecuta una "acción secundaria" sin alterar la respuesta que viene del servidor.
      tap((response) => {
        this.saveSession(response); // Guarda los tokens en el almacenamiento del navegador.
        this.isLoggedIn.set(true);   // Cambia el Signal a true; automáticamente todos los componentes se enteran del cambio.
      })
    );
  }

  // 4. RENOVACIÓN DE TOKEN (REFRESH)
  // El interceptor llama a esta función cuando el Access Token expira.
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken(); // Recupera el token de refresco guardado.
    const body: RefreshTokenRequest = { refreshToken: refreshToken || '' }; // Lo empaqueta en el formato que pide el backend.

    // Envía el refresh token a la API para obtener un combo de tokens totalmente nuevo.
    return this.http.post<AuthResponse>(`${this.API_URL}/refresh`, body).pipe(
      tap((response) => {
        this.saveSession(response); // Reemplaza los tokens viejos guardados por los nuevos recibidos.
      })
    );
  }

  // 5. CIERRE DE SESIÓN
  logout(): void {
    // Borra por completo todos los datos de sesión almacenados en el navegador.
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('token_type');
    
    // Cambia el Signal a false. Si tenías menús que dependían de esto, se ocultarán inmediatamente.
    this.isLoggedIn.set(false);
  }

  // 6. MÉTODOS AUXILIARES DE LECTURA (GETTERS)
  // Recupera el Access Token (el que se envía en cada petición HTTP).
  getAccessToken(): string | null {
    return localStorage.getItem('access_token');
  }

  // Recupera el Refresh Token (el que sirve únicamente para generar nuevos access tokens).
  getRefreshToken(): string | null {
    return localStorage.getItem('refresh_token');
  }

  // Recupera el tipo de token (habitualmente 'Bearer').
  getTokenType(): string | null {
    return localStorage.getItem('token_type');
  }

  // 7. MÉTODOS PRIVADOS INTERNOS
  // Guarda de manera organizada los tres strings clave que devuelve el servidor en el LocalStorage.
  private saveSession(response: AuthResponse): void {
    localStorage.setItem('access_token', response.accessToken);
    localStorage.setItem('refresh_token', response.refreshToken);
    localStorage.setItem('token_type', response.tokenType);
  }

  // ": boolean" se conoce como anotación de tipo de retorno
  private hasToken(): boolean {
    return !!localStorage.getItem('access_token');
  }
}
