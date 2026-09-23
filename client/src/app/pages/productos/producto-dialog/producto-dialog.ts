import { Component, Inject, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select'; // <-- Importante
import { MatButtonModule } from '@angular/material/button';
import { CategoryService } from '../../../services/category.service';
import { Category } from '../../../models/category';

@Component({
  selector: 'app-producto-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule, // <-- Agregado
    MatButtonModule
  ],
  templateUrl: './producto-dialog.html'
})
export class ProductoDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<ProductoDialogComponent>);
  private categoryService = inject(CategoryService);

  // Señal para almacenar las categorías
  categorias = signal<Category[]>([]);
  isLoadingCategorias = signal<boolean>(true);

  form: FormGroup = this.fb.group({
    id: [null],
    name: ['', [Validators.required, Validators.minLength(3)]],
    price: [0, [Validators.required, Validators.min(0.01)]],
    categoryId: [null, [Validators.required]] // Almacena el ID seleccionado
  });

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
    if (data) {
      this.form.patchValue({
        id: data.id,
        name: data.name,
        price: data.price,
        categoryId: data.categoryId || data.category?.id
      });
    }
  }

  ngOnInit(): void {
    this.cargarCategorias();
  }

  cargarCategorias(): void {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.categorias.set(data);
        this.isLoadingCategorias.set(false);
      },
      error: (err) => {
        console.error('Error al cargar categorías:', err);
        this.isLoadingCategorias.set(false);
      }
    });
  }

  guardar(): void {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value);
    }
  }

  cancelar(): void {
    this.dialogRef.close();
  }
}