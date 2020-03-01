package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.*;

public interface ChallengeExecutor<REQUEST extends ChallengeRequest, RESPONSE extends ChallengeExecutor.ChallengeAnswer> {

    public StartedChallengeRequest start(REQUEST request);

    public boolean resolve(StartedChallengeRequest startedChallenge, RESPONSE response) throws ChallengeFailException;

    public static class ChallengeFailException extends RuntimeException {

    }


    public static interface ChallengeAnswer {
        public <O> O accept(ChallengeAnswerVisitor<O> visitor);

        public interface ChallengeAnswerVisitor<O> {

            O visit(OTPChallengeAnswer otpChallengeAnswer);

            O visit(RsaChallengeAnswer rsaChallengeAnswer);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class OTPChallengeAnswer implements ChallengeAnswer {
        private String otp;

        @Override
        public <O> O accept(ChallengeAnswerVisitor<O> visitor) {
            return visitor.visit(this);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class RsaChallengeAnswer implements ChallengeAnswer {
        private final String signaturePayloadBase64;

        @Override
        public <O> O accept(ChallengeAnswerVisitor<O> visitor) {
            return visitor.visit(this);
        }
    }
}
