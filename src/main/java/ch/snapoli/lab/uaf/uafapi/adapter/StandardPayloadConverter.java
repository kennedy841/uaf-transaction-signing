package ch.snapoli.lab.uaf.uafapi.adapter;

import ch.snapoli.lab.uaf.uafapi.domain.PayloadConverter;
import ch.snapoli.lab.uaf.uafapi.domain.ECSAChallengeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StandardPayloadConverter implements PayloadConverter {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public String convert(ECSAChallengeRequest.Payload payload) {
        return objectMapper.writeValueAsString(payload);
    }
}
