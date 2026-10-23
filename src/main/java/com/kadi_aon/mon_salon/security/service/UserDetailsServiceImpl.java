package com.kadi_aon.mon_salon.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;

import lombok.RequiredArgsConstructor;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CompteRepository compteRepository;

    public UserDetailsServiceImpl(CompteRepository compteRepository) {
        super();
        this.compteRepository = compteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Identifiant vide.");
        }
        String trimmed = username.trim();
        String compact = trimmed.replaceAll("\\s+", "");

        Compte compte = compteRepository.findByIdentifiant(trimmed)
                .or(() -> compteRepository.findByIdentifiant(compact))
                .or(() -> {
                    if (compact.startsWith("+")) {
                        return compteRepository.findByIdentifiant(compact.substring(1));
                    } else {
                        return compteRepository.findByIdentifiant("+" + compact);
                    }
                })
                .orElseThrow(() -> new UsernameNotFoundException("Compte non trouvé avec l'identifiant : " + username));
        return new UserDetailsImpl(compte);
    }
}
