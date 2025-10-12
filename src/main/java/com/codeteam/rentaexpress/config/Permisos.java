package com.codeteam.rentaexpress.config;

import jakarta.servlet.annotation.WebFilter;
import org.springframework.stereotype.Component;
import com.codeteam.rentaexpress.models.Usuario;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Component
@WebFilter("/*")
public class Permisos implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI();

        // Rutas públicas (sin necesidad de login)
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (session != null && session.getAttribute("usuario") != null) {
            // Si está logueado, verificar si tiene acceso a la ruta
            if (hasAccess(path, session)) {
                chain.doFilter(request, response);
            } else {
                // No tiene permiso para la ruta
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/acceso_denegado");
            }
        } else {
            // No está logueado, redirigir al login y mostrar mensaje

            httpResponse.sendRedirect(httpRequest.getContextPath() + "/");
            //httpRequest.setAttribute("showtoast", true);
            //chain.doFilter(request, response);

        }
    }

    private boolean isPublicPath(String path) {
        // Agrega aquí las rutas que deben ser públicas
        return path.startsWith("/resources")
                || path.startsWith("/static")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/images")
                || path.equals("/")
                || path.endsWith("/login")
                || path.endsWith("/agregarUsuario")
                || path.endsWith("/logout")
                || path.endsWith("/acerca")
                || path.startsWith("/icons")
                || path.endsWith("/contacto");
    }

    private boolean hasAccess(String path, HttpSession session) {
        Object usuarioObj = session.getAttribute("usuario");
        if (usuarioObj instanceof Usuario) {
            Usuario usuario = (Usuario) usuarioObj;
            String rol = usuario.getRol().getNombre();

            // Rutas solo para ADMIN
            if (path.startsWith("/usuarios")) {
                return "Administrador".equals(rol);
            }
        }
        return true; // El resto de rutas protegidas solo requieren login
    }

    @Override
    public void destroy() {}


}
