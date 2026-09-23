import { Routes } from '@angular/router';
import { authGuard } from './guards/auth-guard';


export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { 
    path: 'login', 
    loadComponent: () => import('./pages/login/login').then(m => m.LoginComponent) 
  },
  { 
    path: 'productos', 
    loadComponent: () => import('./pages/productos/productos').then(m => m.ProductosComponent),
    canActivate: [authGuard] // Protegido con AuthGuard
  },
  { path: '**', redirectTo: 'login' }
];