package ch.snapoli.lab.uaf.uafapi.adapter;

import ch.snapoli.lab.uaf.uafapi.domain.ChallengeExecutor;
import ch.snapoli.lab.uaf.uafapi.domain.OTPValidator;

public class StandardPocketListValidator implements OTPValidator {
    @Override
    public boolean validate(String userId, ChallengeExecutor.OTPChallengeAnswer value) {
        return value.getOtp().equals("123456");
    }

}
