package ch.snapoli.lab.uaf.uafapi.delivery;

import ch.snapoli.lab.uaf.uafapi.domain.ChallengeExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
class UafControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldEnrollDevice() throws Exception {
        UafController.EnrollableDeviceRequest enrollableDeviceRequest = createRequest();

        String json = mockMvc.perform(post("/uaf/enrollment/challenge").contentType(APPLICATION_JSON).content(json(enrollableDeviceRequest)))
                .andExpect(status().is(200)).andDo(
                        document("{class-name}/{method-name}-{step}"
                         ,preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())))
                .andReturn().getResponse().getContentAsString();

        ApproveResponseJson approveResponse = object(json, ApproveResponseJson.class);

        UafController.EnrollDeviceChallengeAnswer answer = createAnswer();


        String challengeId = approveResponse.getChallengId().getId();
        mockMvc.perform(put("/uaf/enrollment/challenge/{challengeId}/answer", challengeId).contentType(APPLICATION_JSON).content(json(answer)))
                .andExpect(status().is(200)).andDo(
                        document("{class-name}/{method-name}-{step}",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                         parameterWithName("challengeId").
                                         description("the challenge id")))
                )
                .andDo(print());
    }


    @Test
    void challenge() {

    }

    @Test
    void enrollementVerify() {
    }

    @Data
    public static class ApproveResponseJson {
        private ChallengeIdJson challengId;


        @Data
        private class ChallengeIdJson {
            private String id;
        }
    }

    private UafController.EnrollDeviceChallengeAnswer createAnswer() {
        UafController.EnrollDeviceChallengeAnswer answer = new UafController.EnrollDeviceChallengeAnswer();
        ChallengeExecutor.OTPChallengeAnswer challengeAnswer = new ChallengeExecutor.OTPChallengeAnswer();
        challengeAnswer.setOtp("123456");
        answer.setChallengeAnswer(challengeAnswer);
        UafController.JwkKeyRequest jwkKey = new UafController.JwkKeyRequest();
        jwkKey.setEllipticCurve("secp256r1");
        jwkKey.setKeyType("EC");
        jwkKey.setX("108626610445227267021216753531011922029775994809025001798123780253931362059287");
        jwkKey.setY("13775089719733830772307592798135791007264440289750994841598233847637616238615");
        answer.setJwkKey(jwkKey);
        return answer;
    }

    private UafController.EnrollableDeviceRequest createRequest() {
        UafController.EnrollableDeviceRequest enrollableDeviceRequest = new UafController.EnrollableDeviceRequest();
        enrollableDeviceRequest.setDeviceId("1");
        enrollableDeviceRequest.setClientId("2");
        return enrollableDeviceRequest;
    }

    public String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }


    public <T> T object(String o, Class<T> cls) throws JsonProcessingException {
        return objectMapper.readValue(o, cls);
    }
}