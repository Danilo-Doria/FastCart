import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { catchError, switchMap, throwError } from 'rxjs';

// Declaramos una constante que exportamos para poder registrarla en la configuración global de la app.
// HttpInterceptorFn define que esta función es un interceptor funcional (el estándar moderno de Angular).
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  
  // INYECCIÓN DE DEPENDENCIAS: Traemos el servicio que maneja la autenticación y tokens.
  const authService = inject(AuthService);
  
  // Obtenemos el token de acceso actual (JWT) guardado en la app (localStorage).
  const token = authService.getAccessToken();
  
  // Obtenemos el tipo de token (por defecto casi siempre es 'Bearer').
  const tokenType = authService.getTokenType() || 'Bearer';

  // Creamos una copia de la petición original. Por defecto, asumimos que se enviará sin modificar.
  let authReq = req;

  // ADICIÓN DEL TOKEN: Si el usuario ya inició sesión y hay un token disponible...
  if (token) {
    // Las peticiones HTTP en Angular son inmutables (no se pueden modificar directamente).
    // Por eso usamos .clone() para crear una copia exacta e inyectarle las cabeceras de autorización.
    authReq = req.clone({
      setHeaders: {
        Authorization: `${tokenType} ${token}` // Agrega la cabecera: "Authorization: Bearer <tu_token>"
      }
    });
  }

  // ENVÍO Y MANEJO DE ERRORES: Enviamos la petición modificada al siguiente paso con next(authReq).
  // Usamos .pipe() para interceptar la RESPUESTA del servidor en caso de que ocurra un error.
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      
      // DETECCIÓN DE TOKEN EXPIRADO: Si el servidor responde con 401 (No Autorizado)...
      // ...y la petición NO era para intentar loguearse o para refrescar el token (para evitar bucles infinitos).
      if (error.status === 401 && !req.url.includes('/auth/login') && !req.url.includes('/auth/refresh')) {
        
        // Llamamos al servicio para ejecutar la lógica de renovación (usa el Refresh Token).
        return authService.refreshToken().pipe(
          
          // switchMap toma la respuesta exitosa de la renovación y "cambia" el flujo para reintentar la petición original.
          switchMap((response) => {
            // Clonamos la petición original del usuario, pero ahora con el NUEVO token que nos dio el servidor.
            const newReq = req.clone({
              setHeaders: {
                Authorization: `${response.tokenType || 'Bearer'} ${response.accessToken}`
              }
            });
            // Reintentamos enviar la petición. Para el usuario, la app funcionó sin interrupciones.
            return next(newReq);
          }),
          
          // Si el Refresh Token también expiró o falló, significa que la sesión caducó por completo.
          catchError((refreshError) => {
            // Cerramos la sesión del usuario (borra datos, redirige al login, etc.).
            authService.logout();
            // Propagamos el error para que la app sepa que falló de forma definitiva.
            return throwError(() => refreshError);
          })
        );
      }
      
      // Si el error fue otro (un 500, 404, etc.), simplemente dejamos pasar el error sin hacer nada especial.
      return throwError(() => error);
    })
  );
};
