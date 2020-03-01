package ch.snapoli.lab.uaf.uafapi.domain;

public interface ChallengeAwareVisitor<O> {
    O visit(ECSAChallengeRequest rsaPublicPrivateKeyChallengeTypeAware);

    O visit(PocketListChallengeRequest pocketListChallengeTypeAware);


    O visit(ECSAPushingChallengeRequest rsaPublicPrivateKeyPushingChallengeRequest);
}
