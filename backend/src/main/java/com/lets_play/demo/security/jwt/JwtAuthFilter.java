package com.lets_play.demo.security.jwt;
package com.letsplay.demo.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. تحقق واش الـ Header كاين وكيبدا بـ Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // خليه يدوز للفلتر اللي موراه (غالباً غيترفض إلا كان Endpoint محمي)
        }

        // 2. عزل التوكين من الـ Header
        jwt = authHeader.substring(7); // كنحيدو "Bearer " (7 characters)
        
        // 3. استخراج الإيميل من التوكين
        userEmail = jwtTokenProvider.extractUsername(jwt);

        // 4. إذا كان الإيميل موجود، والـ User مازال ما مكونيكطيش فـ SecurityContext
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // جيب الداتا ديال الـ User من الداتابيز
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. تأكد واش التوكين صحيح
            if (jwtTokenProvider.isTokenValid(jwt, userDetails)) {
                
                // 6. خلق Authentication Token لـ Spring
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                
                // زيد تفاصيل الـ Request (بحال الـ IP Address)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 7. سجّل الـ User فـ Spring Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 8. كمّل المسار ديال الـ Request
        filterChain.doFilter(request, response);
    }
}