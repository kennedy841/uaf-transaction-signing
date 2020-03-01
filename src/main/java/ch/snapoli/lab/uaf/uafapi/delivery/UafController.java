package ch.snapoli.lab.uaf.uafapi.delivery;

import ch.snapoli.lab.uaf.uafapi.domain.PushECSARequest.PushRequest;
import ch.snapoli.lab.uaf.uafapi.domain.usecase.VerifyChallengeUseCase;
import ch.snapoli.lab.uaf.uafapi.domain.*;
import ch.snapoli.lab.uaf.uafapi.domain.usecase.CreateResourceChallengeUseCase;
import ch.snapoli.lab.uaf.uafapi.domain.usecase.StartDeviceEnrollmentUseCase;
import ch.snapoli.lab.uaf.uafapi.domain.usecase.VerifyDeviceEnrollmentUseCase;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static ch.snapoli.lab.uaf.uafapi.domain.StartedChallengeRequest.Type.*;

@RestController
@RequestMapping(path = "/uaf/")
public class UafController {


    @Autowired
    private StartDeviceEnrollmentUseCase startdeviceEnrollmentUseCase;

    @Autowired
    private VerifyChallengeUseCase verifyChallengeUseCase;

    @Autowired
    private CreateResourceChallengeUseCase createResourceChallengeUseCase;

    @Autowired
    private VerifyDeviceEnrollmentUseCase verifyDeviceEnrollmentUseCase;

    @Autowired
    private ResourceChallengeStorage challengeStorage;


    @PostMapping(path = "/enrollment/challenge")
    public ResponseEntity<StartDeviceEnrollmentUseCase.ApproveResponse> deviceenrollement(@RequestBody EnrollableDeviceRequest enrollableDevice){

        StartDeviceEnrollmentUseCase.ApproveResponse response = startdeviceEnrollmentUseCase.enroll(
                        new StartDeviceEnrollmentUseCase.CreateRequest(new EnrollableDevice(enrollableDevice.deviceId), new OTPRequest(enrollableDevice.deviceId, enrollableDevice.clientId)));

        return ResponseEntity.ok(response);
    }


    @PutMapping(path = "/enrollment/challenge/{challengeId}/answer")
    public ResponseEntity<?> deviceEnrollementChallenge(@PathVariable String challengeId, @RequestBody @Valid EnrollDeviceChallengeAnswer enrollDeviceChallengeAnswer){

        VerifyDeviceEnrollmentUseCase.ApproveRequest.JwkKey jwkKey = new VerifyDeviceEnrollmentUseCase.ApproveRequest.JwkKey(enrollDeviceChallengeAnswer.getJwkKey().keyType,
                enrollDeviceChallengeAnswer.getJwkKey().ellipticCurve,
                enrollDeviceChallengeAnswer.getJwkKey().getX(), enrollDeviceChallengeAnswer.getJwkKey().getY());

        VerifyChallengeResponse verify = verifyDeviceEnrollmentUseCase.verify(new VerifyDeviceEnrollmentUseCase.ApproveRequest(new EnrollDeviceChallengeRepository.ChallengeId(challengeId), enrollDeviceChallengeAnswer.challengeAnswer, jwkKey));
        return ResponseEntity.ok(verify);
    }


    @PostMapping(path = "/challenge")
    public ResponseEntity<CreateResourceChallengeUseCase.CreateResourceChallengeResponse> challenge(@RequestBody CreateChallengeRequest challengeRequest){

        CreateResourceChallengeUseCase.CreateResourceChallengeResponse createResourceChallengeResponse =
                createResourceChallengeUseCase.create(new CreateResourceChallengeUseCase.CreateResourceChallengeRequest(createChallengeRequest(challengeRequest)));

        return ResponseEntity.ok(createResourceChallengeResponse);
    }

    @GetMapping(path = "/challenge/{challengeId}")
    public ResponseEntity<?> getchallenge( @PathVariable String challengeId){
        Optional<StartedChallengeRequest> body = challengeStorage.get(challengeId);
        return body.map(v -> ResponseEntity.ok(v)).orElse(ResponseEntity.notFound().build());
    }

    private ChallengeRequest createChallengeRequest(CreateChallengeRequest challengeRequest) {
        if(challengeRequest.type == StartedChallengeRequest.Type.POCKETLIST){
            return new OTPRequest(challengeRequest.clientId, challengeRequest.deviceId);
        }

        if(challengeRequest.type == StartedChallengeRequest.Type.PUSHPUBPRIV){
            return new PushECSARequest(challengeRequest.deviceId, challengeRequest.resourceRef, challengeRequest.clientId,
                    new PushRequest(challengeRequest.getPushRequest().getMessage(), challengeRequest.getPushRequest().getAdditionalMessage()));
        }
        return new ECSARequest(challengeRequest.deviceId, challengeRequest.resourceRef, challengeRequest.clientId);
    }

    @PutMapping(path = "/challenge/{challengeId}/answer")
    public ResponseEntity<VerifyChallengeResponse> challengeVerify(@PathVariable String challengeId, @RequestBody AnswerChallengeRequest answerChallengeRequest) throws UnsupportedEncodingException {

        ChallengeExecutor.ChallengeAnswer response = createResponse(answerChallengeRequest);

        VerifyChallengeResponse verify = verifyChallengeUseCase.verify(challengeId, response);
        return ResponseEntity.ok(verify);
    }

    private ChallengeExecutor.ChallengeAnswer createResponse(AnswerChallengeRequest answerChallengeRequest) throws UnsupportedEncodingException {
        if(answerChallengeRequest.type== POCKETLIST){
            return new ChallengeExecutor.OTPChallengeAnswer(answerChallengeRequest.challengeAnswer);
        }
        else
            return new ChallengeExecutor.RsaChallengeAnswer(answerChallengeRequest.challengeAnswer);
    }


    @Data
    public static class EnrollableDeviceRequest {
        private String deviceId;
        private String clientId;
    }

    @Data
    public static class CreateChallengeRequest {
        private String deviceId;
        private String clientId;
        private StartedChallengeRequest.Type type;
        private String resourceRef;
        private PushRequestDTO pushRequest;

        @Data
        public static class PushRequestDTO {
            private String message;
            private List<String> additionalMessage;
        }
    }

    @Data
    public static class AnswerChallengeRequest {
        private StartedChallengeRequest.Type type;
        private String challengeAnswer;
    }


    @Data
    public static class EnrollDeviceChallengeAnswer {

        @NotNull
        private ChallengeExecutor.OTPChallengeAnswer challengeAnswer;

        @NotNull
        private JwkKeyRequest jwkKey;
    }

    @Data
    public static class JwkKeyRequest {
        private String keyType = "EC";
        private String ellipticCurve  = "P-256";

        @NotNull
        private String x;

        @NotNull
        private String y;
    }







}
