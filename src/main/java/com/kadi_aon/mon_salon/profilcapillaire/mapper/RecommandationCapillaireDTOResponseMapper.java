package com.kadi_aon.mon_salon.profilcapillaire.mapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.kadi_aon.mon_salon.profilcapillaire.dto.RecommandationCapillaireDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.entity.RecommandationCapillaire;


@Component
public class RecommandationCapillaireDTOResponseMapper implements Function<RecommandationCapillaire, RecommandationCapillaireDTOResponse> {

    private final ProduitConseilDTOResponseMapper produitConseilMapper;

    public RecommandationCapillaireDTOResponseMapper(ProduitConseilDTOResponseMapper produitConseilMapper) {
        super();
        this.produitConseilMapper = produitConseilMapper;
    }

    @Override
    public RecommandationCapillaireDTOResponse apply(RecommandationCapillaire r) {
        if (r == null) {
            return null;
        }

        long joursRestants = 0L;
        if (r.getDateRecommandation() != null) {
            LocalDateTime dateFinCooldown = r.getDateRecommandation().plusDays(30);
            if (LocalDateTime.now().isBefore(dateFinCooldown)) {
                joursRestants = Math.max(1L, ChronoUnit.DAYS.between(LocalDateTime.now(), dateFinCooldown));
            }
        }

        return new RecommandationCapillaireDTOResponse(
                r.getId(),
                r.getReference(),
                r.getDateRecommandation(),
                r.getTypeCheveux(),
                r.getTexture(),
                r.getPorosite(),
                r.getLongueur(),
                r.getDensite(),
                r.getCuirChevelu(),
                r.getEtatCheveux(),
                produitConseilMapper.apply(r.getProduitLavage()),
                produitConseilMapper.apply(r.getProduitSoinProfond()),
                produitConseilMapper.apply(r.getProduitHydratation()),
                produitConseilMapper.apply(r.getProduitFinition()),
                r.getScoreLavage(),
                r.getScoreSoinProfond(),
                r.getScoreHydratation(),
                r.getScoreFinition(),
                r.getScoreGlobal(),
                r.getConseilsPersonnalises(),
                joursRestants
        );
    }
}
