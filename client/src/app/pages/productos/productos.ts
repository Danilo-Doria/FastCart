import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Producto } from '../../models/producto';
import { ProductoService } from '../../services/producto';
import { AuthService } from '../../services/auth.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProductoDialogComponent } from './producto-dialog/producto-dialog';
import { ConfirmDialogComponent } from '../../components/confirm-dialog/confirm-dialog';

// Angular Material
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatDialogModule
  ],
  templateUrl: './productos.html',
  styleUrl: './productos.css',
})
export class ProductosComponent implements OnInit {
  private productoService = inject(ProductoService);
  private authService = inject(AuthService);
  private router = inject(Router);

  displayedColumns: string[] = ['id', 'name', 'price', 'categoryNombre', 'acciones'];

  // 1. Usamos una Señal para los productos
  productos = signal<Producto[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.productoService.getProductos().subscribe({
      next: (data: any) => {
        const lista = Array.isArray(data) ? data : data?.content || [];

        // 2. Actualizamos el valor de la señal (esto notifica a la plantilla inmediatamente)
        this.productos.set(lista);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar productos:', err);
        this.isLoading.set(false);
        this.errorMessage.set('No se pudieron cargar los productos.');
      },
    });
  }

  eliminarProducto(id: number): void {
  const dialogRef = this.dialog.open(ConfirmDialogComponent, {
    width: '400px',
    data: {
      titulo: '¿Eliminar producto?',
      mensaje: 'Esta acción no se puede deshacer. El producto será eliminado permanentemente de la base de datos.',
      textoBotonConfirmar: 'Sí, eliminar',
      color: 'warn'
    }
  });

  dialogRef.afterClosed().subscribe((confirmado: boolean) => {
    if (confirmado) {
      this.productoService.eliminarProducto(id).subscribe({
        next: () => {
          // Actualizamos la señal eliminando el ítem de la lista local
          this.productos.update(lista => lista.filter(p => p.id !== id));
        },
        error: (err) => {
          console.error('Error al eliminar:', err);
          alert('Ocurrió un error al intentar eliminar el producto.');
        }
      });
    }
  });
}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private dialog = inject(MatDialog);

  abrirModalCrear(): void {
    const dialogRef = this.dialog.open(ProductoDialogComponent, {
      width: '450px',
    });

    dialogRef.afterClosed().subscribe((nuevoProducto: Producto) => {
      if (nuevoProducto) {
        this.productoService.crearProducto(nuevoProducto).subscribe({
          next: () => this.cargarProductos(), // Recarga la lista desde Spring Boot
          error: (err) => alert('Error al crear el producto'),
        });
      }
    });
  }

  abrirModalEditar(producto: Producto): void {
    const dialogRef = this.dialog.open(ProductoDialogComponent, {
      width: '450px',
      data: producto,
    });

    dialogRef.afterClosed().subscribe((productoEditado: Producto) => {
      if (productoEditado && producto.id) {
        this.productoService.actualizarProducto(producto.id, productoEditado).subscribe({
          next: () => this.cargarProductos(),
          error: (err) => alert('Error al actualizar el producto'),
        });
      }
    });
  }
}
