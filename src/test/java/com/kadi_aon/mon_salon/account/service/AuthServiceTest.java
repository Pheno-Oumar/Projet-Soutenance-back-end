package com.kadi_aon.mon_salon.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.kadi_aon.mon_salon.account.dto.LoginRequestDTO;
import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.entity.RefreshToken;
import com.kadi_aon.mon_salon.account.entity.RolePlateforme;
import com.kadi_aon.mon_salon.account.enums.TypeRolePlateforme;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.account.repository.RefreshTokenRepository;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.RoleSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.RoleSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;
import com.kadi_aon.mon_salon.security.jwt.JwtUtils;
import com.kadi_aon.mon_salon.security.service.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private CompteRepository compteRepository;
    @Mock
    private SalonRepository salonRepository;
    @Mock
    private AffectationSalonRepository affectationSalonRepository;
    @Mock
    private RoleSalonRepository roleSalonRepository;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private Compte adminCompte;
    private Compte clientCompte;
    private Salon salon;

    @BeforeEach
    void setUp() {
        RolePlateforme roleAdmin = RolePlateforme.builder().id(1L).role(TypeRolePlateforme.ADMIN_SYSTEME).build();

        adminCompte = Compte.builder()
                .id(1L)
                .nom("AON")
                .prenom("Amadou")
                .telephone("+22376662725")
                .email("admin@test.com")
                .pays("Mali")
                .statut(true)
                .rolePlateforme(roleAdmin)
                .doitChangerMotDePasse(false)
                .build();

        clientCompte = Compte.builder()
                .id(2L)
                .nom("Diallo")
                .prenom("Aminata")
                .telephone("+22370123456")
                .email("ami@test.com")
                .pays("Mali")
                .statut(true)
                .doitChangerMotDePasse(true)
                .build();

        salon = Salon.builder()
                .id(10L)
                .nom("Kady Chic")
                .slug("kady-chic")
                .statut(true)
                .build();
    }

    @Test
    void loginAdminSysteme_parTelephone_succes() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .telephone("+22376662725")
                .password("123456")
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(adminCompte);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateAccessToken(eq(1L), eq("+22376662725"), eq("ADMIN_SYSTEME"))).thenReturn("fake-jwt-admin");
        when(jwtUtils.getExpirationMs()).thenReturn(60000L);
        when(refreshTokenService.createRefreshToken(adminCompte)).thenReturn(
                RefreshToken.builder().token("fake-refresh-admin").build()
        );

        AuthService.LoginResult result = authService.loginAdminSysteme(request);

        assertNotNull(result);
        assertEquals("fake-jwt-admin", result.getResponseDto().getAccessToken());
        assertEquals("+22376662725", result.getResponseDto().getCompte().getTelephone());
        assertEquals("Mali", result.getResponseDto().getCompte().getPays());
    }

    @Test
    void loginSalon_parTelephone_succes() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .telephone("+22370123456")
                .password("secret123")
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(clientCompte);
        when(salonRepository.findBySlug("kady-chic")).thenReturn(Optional.of(salon));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        RoleSalon roleClient = RoleSalon.builder().id(1L).role(TypeRoleSalon.CLIENT).build();
        AffectationSalon affectation = AffectationSalon.builder()
                .id(100L)
                .compte(clientCompte)
                .salon(salon)
                .roles(new HashSet<>(Collections.singletonList(roleClient)))
                .statut(true)
                .build();

        when(affectationSalonRepository.findByCompteAndSalon(clientCompte, salon)).thenReturn(Optional.of(affectation));
        when(jwtUtils.generateAccessToken(eq(2L), eq("+22370123456"), any())).thenReturn("fake-jwt-client");
        when(jwtUtils.getExpirationMs()).thenReturn(60000L);
        when(refreshTokenService.createRefreshToken(clientCompte)).thenReturn(
                RefreshToken.builder().token("fake-refresh-client").build()
        );

        AuthService.LoginResult result = authService.loginSalon("kady-chic", request);

        assertNotNull(result);
        assertEquals("fake-jwt-client", result.getResponseDto().getAccessToken());
        assertEquals("+22370123456", result.getResponseDto().getCompte().getTelephone());
        assertTrue(result.getResponseDto().getDoitChangerMotDePasse());
    }
}
