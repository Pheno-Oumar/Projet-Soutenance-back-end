package com.kadi_aon.mon_salon.common.deserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Désérialiseur Jackson tolérant et sécurisé pour {@link LocalDate}.
 * <p>
 * Gère de manière résiliente :
 * <ul>
 *   <li>Chaînes vides ou composées d'espaces (converties en {@code null})</li>
 *   <li>Formats de date ISO standards (ex: "1995-05-20")</li>
 *   <li>Formats de date avec composante temporelle ISO (ex: "1995-05-20T00:00:00.000Z")</li>
 * </ul>
 */
public class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null || text.isBlank()) {
            return null;
        }
        text = text.trim();

        if (text.contains("T")) {
            text = text.split("T")[0];
        }

        try {
            return LocalDate.parse(text);
        } catch (Exception e) {
            try {
                return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
            } catch (Exception e2) {
                try {
                    return OffsetDateTime.parse(p.getText().trim()).toLocalDate();
                } catch (Exception e3) {
                    throw new IOException("Format de date invalide : " + text, e3);
                }
            }
        }
    }
}
