package com.philiance.otakulinks.jwt;

import com.philiance.otakulinks.configuration.SecurityConfiguration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain ) throws ServletException, IOException {
        String jwt = resolveToken(request);
        Authentication authentication = jwtUtils.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
                

    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7); // Supprime le préfixe "Bearer " du token
        } else {
            // Vous devez retourner quelque chose ici si le token n'est pas trouvé ou ne commence pas par "Bearer "
            return null; // ou une chaîne vide "", ou une autre valeur par défaut selon votre logique d'application.
        }
    }

}
