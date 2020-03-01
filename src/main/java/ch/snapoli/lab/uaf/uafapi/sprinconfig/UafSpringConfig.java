package ch.snapoli.lab.uaf.uafapi.sprinconfig;


import ch.snapoli.lab.uaf.uafapi.domain.ChallengeExecutor.OTPChallengeAnswer;
import ch.snapoli.lab.uaf.uafapi.domain.PocketListChallengeExecutor;
import ch.snapoli.lab.uaf.uafapi.domain.usecase.CreateResourceChallengeUseCase;
import ch.snapoli.lab.uaf.uafapi.domain.usecase.StartDeviceEnrollmentUseCase;
import ch.snapoli.lab.uaf.uafapi.domain.usecase.VerifyChallengeUseCase;
import ch.snapoli.lab.uaf.uafapi.domain.usecase.VerifyDeviceEnrollmentUseCase;
import ch.snapoli.lab.uaf.uafapi.adapter.*;
import ch.snapoli.lab.uaf.uafapi.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class UafSpringConfig {

    @Bean
    public StartDeviceEnrollmentUseCase startDeviceEnrollmentUseCase(EnrollDeviceChallengeRepository challengeRepository,
                                                                     ChallengeExecutorFactory factory){
        return new StartDeviceEnrollmentUseCase(challengeRepository, factory);
    }


    @Bean
    public VerifyDeviceEnrollmentUseCase verifyDeviceEnrollmentUseCase(EnrollDeviceChallengeRepository challengeRepository,
                                                                       PublicKeyGenerator publicKeyGenerator,
                                                                       PublicKeyStorageRepository publicKeyStorageRepository,
                                                                       ChallengeExecutorFactory factory){
        return new VerifyDeviceEnrollmentUseCase(challengeRepository, factory, publicKeyGenerator, publicKeyStorageRepository);
    }

    @Bean
    public EnrollDeviceChallengeRepository challengeRepository(){
        return new InMemoryEnrollDeviceChallengeRepository();
    }

    @Bean
    public PublicKeyGenerator publicKeyGenerator(){
        return new ECPublicKeyGenerator();
    }

    @Bean
    public PublicKeyStorageRepository publicKeyStorageRepository(){
        return new InMemoryPublicKeyStorageRepository();
    }

    @Bean
    public ChallengeExecutor<OTPRequest, OTPChallengeAnswer> challengeTypeFactoryForPocketList(OptGenerator pocketListGenerator, OTPValidator pocketListValidator){
        return new PocketListChallengeExecutor(pocketListGenerator, pocketListValidator);
    }

    @Bean
    public CreateResourceChallengeUseCase createResourceChallengeUseCase(ResourceChallengeStorage payloadStorage, ChallengeExecutorFactory challengeExecutorFactory, PublicKeyStorageRepository publicKeyStorageRepository, DeviceRepository deviceRepository){
        return new CreateResourceChallengeUseCase(payloadStorage, challengeExecutorFactory, new StandardChallengeSupportedVerifier(publicKeyStorageRepository, deviceRepository));
    }

    @Bean
    public ChallengeExecutorFactory challengeExecutorFactory(OptGenerator pocketListGenerator,
                                                             OTPValidator pocketListValidator,
                                                             SignatureValidator signatureValidator,
                                                             PayloadConverter payloadConverter,
                                                             PublicKeyStorageRepository publicKeyStorageRepository,
                                                             DeviceRepository deviceRepository,
                                                             PushECSAChallengeExecutor.CnsDelivery cnsDelivery,
                                                             ObjectMapper objectMapper){
        return new ChallengeExecutorFactory(pocketListGenerator, pocketListValidator, signatureValidator, payloadConverter, publicKeyStorageRepository, deviceRepository, cnsDelivery, objectMapper);
    }

    @Bean
    public PushECSAChallengeExecutor.CnsDelivery cnsDelivery(){
        return new PushECSAChallengeExecutor.CnsDelivery() {
            @Override
            public void send(String pushId, String message, Map<String, String> headers) {
                log.info("send {} {}", pushId, message);
            }
        };
    }

    @Bean
    public SignatureValidator signatureValidator(){
        return new ECSignatureValidator();
    }


    @Bean
    public VerifyChallengeUseCase verifyChallengeUseCase(ResourceChallengeStorage challengeStorage, ChallengeExecutorFactory challengeExecutorFactory){
        return new VerifyChallengeUseCase(challengeStorage, challengeExecutorFactory, 3);
    }

    @Bean
    public OptGenerator pocketListGenerator(){
        return new OptGenerator() {
            @Override
            public Otp generate(String userId) {
                return new Otp(1, 1);
            }
        };
    }

    @Bean
    public OTPValidator pocketListValidator(){
        return new StandardPocketListValidator();
    }

}
