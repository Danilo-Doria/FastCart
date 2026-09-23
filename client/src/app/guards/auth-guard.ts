import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Declaramos una constante que exportamos para poder usarla en nuestro archivo de rutas (app.routes.ts).
// CanActivateFn es el tipo que define a un guardián de protección de rutas moderno en Angular.
export const authGuard: CanActivateFn = (route, state) => {
  
  const authService = inject(AuthService);
  
  // Inyectamos el enrutador de Angular para poder redirigir al usuario a otras páginas por código.
  const router = inject(Router);

  // COMPROBACIÓN DE ACCESO
  // Llamamos a isLoggedIn(). Como es un SIGNAL, se ejecuta usando paréntesis '()'.
  // Al hacer esto, obtenemos directamente su valor actual en milisegundos (true o false).
  if (authService.isLoggedIn()) {
    // Si la señal es 'true', retornamos true. El guardián le dice a Angular: "Adelante, puedes pasar a esta página".
    return true; 
  }

  // REDIRECCIÓN POR FALTA DE PERMISOS
  // Si la señal es 'false', usamos el router para mandar al usuario de vuelta a la pantalla de login.
  router.navigate(['/login']);
  
  // Retornamos false para bloquear por completo el intento original de entrar a la ruta privada.
  return false;
};
