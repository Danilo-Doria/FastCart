import { Component, signal } from '@angular/core';

// RouterOutlet: El contenedor dinámico para cargar diferentes páginas (vistas) sin recargar el navegador.
import { RouterOutlet } from '@angular/router';

// DECORADOR (@Component): Configura y le da "superpoderes" a nuestra clase de TypeScript.
@Component({
  selector: 'app-root',     // Es el nombre de la etiqueta HTML personalizada para usar este componente
  imports: [RouterOutlet],  // Aquí registramos 'RouterOutlet' para que el HTML de este componente tenga permiso de usarlo.
  templateUrl: './app.html',// La ruta del archivo HTML que define el diseño visual de este componente.
  styleUrl: './app.css'     // La ruta del archivo CSS que define los estilos de este componente.
})

// CLASE: Aquí va la lógica y los datos que usará tu interfaz visual.
export class App {
  // protected: Significa que esta variable solo se puede ver dentro de esta clase y en su archivo HTML (app.html).
  // readonly: Protege la variable para que no la destruyas o reemplaces por accidente con otro tipo de dato.
  // signal('client'): Crea una "Señal" reactiva que inicia con el texto 'client'. 
  // Angular vigilará esta variable y, si cambia en el futuro, actualizará la pantalla al instante.
  protected readonly title = signal('client');
}
