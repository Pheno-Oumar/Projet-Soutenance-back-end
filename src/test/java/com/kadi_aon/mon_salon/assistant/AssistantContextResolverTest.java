package com.kadi_aon.mon_salon.assistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.assistant.context.AssistantContext;
import com.kadi_aon.mon_salon.assistant.context.AssistantContextResolver;
import com.kadi_aon.mon_salon.assistant.context.AssistantContextType;
import com.kadi_aon.mon_salon.assistant.dto.AssistantChatRequest;
import com.kadi_aon.mon_salon.assistant.service.SystemPromptBuilder;
import com.kadi_aon.mon_salon.salon.service.ExploreSalonService;

@ExtendWith(MockitoExtension.class)
class AssistantContextResolverTest {

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private ExploreSalonService exploreSalonService;

    @InjectMocks
    private AssistantContextResolver resolver;

    private SystemPromptBuilder promptBuilder;

    @BeforeEach
    void setUp() {
        promptBuilder = new SystemPromptBuilder();
    }

    @Test
    void resolve_visiteurPlateforme_memeAvecSlugSalon() {
        AssistantChatRequest request = new AssistantChatRequest();
        request.setSlugSalon("salon-beaute");

        AssistantContext ctx = resolver.resolve(request, null);

        assertNotNull(ctx);
        assertEquals(AssistantContextType.PLATEFORME_VISITEUR, ctx.getType());

        String prompt = promptBuilder.buildPrompt(ctx);
        assertTrue(prompt.contains("Visiteur explorant la plateforme"));
        assertTrue(prompt.contains("Tu es un expert chevronné"));
    }

    @Test
    void resolve_clientPlateforme_avecCompteConnecte() {
        Principal principal = () -> "client@test.com";
        Compte compte = Compte.builder().id(10L).email("client@test.com").prenom("Aïcha").build();
        when(compteRepository.findByEmail("client@test.com")).thenReturn(Optional.of(compte));

        AssistantChatRequest request = new AssistantChatRequest();
        AssistantContext ctx = resolver.resolve(request, principal);

        assertNotNull(ctx);
        assertEquals(AssistantContextType.PLATEFORME_CLIENT, ctx.getType());
        assertEquals("Aïcha", ctx.getPrenom());

        String prompt = promptBuilder.buildPrompt(ctx);
        assertTrue(prompt.contains("Client connecté sur la plateforme"));
        assertTrue(prompt.contains("Aïcha"));
    }
}
