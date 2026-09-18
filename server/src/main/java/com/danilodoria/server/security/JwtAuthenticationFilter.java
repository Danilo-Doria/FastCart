package com.danilodoria.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// OncePerRequestFilter clase que  viene de Spring y está diseñada para crear filtros HTTP
// Útil para autenticación porque quieres procesar el JWT una vez por petición
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /*
    request: Contiene los datos de la petición del cliente (headers, URL, parámetros).
    response: Permite modificar la respuesta que se enviará al cliente.
    filterChain: Es la cadena de filtros de seguridad. Sirve para pasar el control al siguiente filtro
    si todo está en orden.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            // Si no hay token o no tiene el formato correcto, el filtro no detiene la petición simplemente
            // la deja pasar al siguiente filtro de la cadena sin autenticar al usuario y detiene
            // la ejecución actual del método (return).
            filterChain.doFilter(request, response);
            return;
        }

        // para obtner solo el token (eliminar "Bearer")
        String token = header.substring(7);
        try {
            // jwtService.extractUsername(token): Usa un servicio personalizado (jwtService) para leer el token,
            // descifrarlo/validar su firma, y extraer el nombre de usuario (o sujeto) guardado dentro
            String username = jwtService.extractUsername(token);

            /*
            SecurityContextHolder.getContext().getAuthentication() == null: Verifica si el usuario no está
            ya autenticado en el contexto de seguridad actual de Spring. Si ya estuviera autenticado,
            no hace falta repetir todo el proceso.

            userDetailsService.loadUserByUsername(username): Busca al usuario en la base de datos
            (o la fuente de datos que uses) mediante su nombre de usuario y devuelve un objeto de tipo UserDetails.

            jwtService.isValid(token, user): Verifica que el token JWT coincida con el usuario encontrado y
            que no haya expirado.
             */
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                if (jwtService.isValid(token, user)) {

                    /*
                    UsernamePasswordAuthenticationToken: Si el token es válido, crea un objeto de autenticación
                    con los datos del usuario (user) y sus permisos/roles (user.getAuthorities()).
                    El valor null en el medio representa la contraseña (credentials),
                    que se deja vacía porque ya está validado por el token.

                    setDetails(): Construye y asocia detalles adicionales de la petición web
                    (como la dirección IP o el ID de sesión) al objeto de autenticación.

                    SecurityContextHolder.getContext().setAuthentication(authentication): Guarda al usuario autenticado
                    dentro del contexto de seguridad de Spring. A partir de esta línea,
                    Spring Security sabrá quién es el usuario y qué roles tiene para el resto de la petición.
                     */
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (RuntimeException ignored) {
        }
        filterChain.doFilter(request, response);
    }
}
