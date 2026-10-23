package com.kadi_aon.mon_salon.security.jwt;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kadi_aon.mon_salon.security.service.UserDetailsImpl;
import com.kadi_aon.mon_salon.security.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        super();
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                String email = jwtUtils.getEmailFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (userDetails.isEnabled()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Blocage obligatoire si le mot de passe initial doit être changé
                    if (userDetails instanceof UserDetailsImpl udi && udi.getCompte() != null
                            && Boolean.TRUE.equals(udi.getCompte().getDoitChangerMotDePasse())) {
                        String uri = request.getRequestURI();
                        boolean isAllowedWhenPasswordChangeRequired =
                                uri.endsWith("/auth/changer-mot-de-passe-initial")
                                || uri.endsWith("/auth/logout")
                                || uri.endsWith("/auth/refresh-token");

                        if (!isAllowedWhenPasswordChangeRequired) {
                            log.warn("Accès bloqué : le compte '{}' doit changer son mot de passe initial avant d'accéder à '{}'",
                                    email, uri);
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                                {
                                  "success": false,
                                  "message": "MOT_DE_PASSE_CHANGE_REQUIS: Vous devez obligatoirement modifier votre mot de passe temporaire avant d'accéder à l'application.",
                                  "code": "MOT_DE_PASSE_CHANGE_REQUIS",
                                  "data": null
                                }
                            """);
                            return;
                        }
                    }
                } else {
                    log.warn("Tentative d'accès avec un compte inactif : {}", email);
                }
            }
        } catch (Exception e) {
            log.error("Impossible de définir l'authentification de l'utilisateur : {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
