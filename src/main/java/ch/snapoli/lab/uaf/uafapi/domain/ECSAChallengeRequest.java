package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.*;

import java.util.Random;

@Getter
public class ECSAChallengeRequest extends AbstractStartedChallengeRequest {

    private final Payload payload;

    public ECSAChallengeRequest(Payload payload, ChallengeRequest challengeRequest) {
        super(challengeRequest);
        this.payload = payload;
    }


    @Override
    public <O> O accept(ChallengeAwareVisitor<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Type type() {
        return Type.PUBPRIV;
    }

    @Override
    public ECSAChallengeRequest.Payload challenge() {
        return payload;
    }


    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Payload {
        private final String resource;
        private final int nonce;

        private Payload(String resource){
            this.resource = resource;
            this.nonce = nonce();
        }

        public static Payload of(String resource){
            return new Payload(resource);
        }

        private static int nonce() {
            Random random = new Random();
            return random.nextInt(100000000);
        }

        public Payload rebuild(){
            return new Payload(resource, nonce());
        }

    }



}
