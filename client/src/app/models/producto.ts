export interface Categoria {
  id: number;
  nombre: string;
}

// el "?" indica que esta propiedad es opcional
export interface Producto {
  id?: number;
  name: string;
  price: number;
  categoryId?: number;
  categoryNombre?: string;
}
