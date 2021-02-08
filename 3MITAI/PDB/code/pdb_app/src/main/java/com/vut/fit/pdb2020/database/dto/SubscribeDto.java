package com.vut.fit.pdb2020.database.dto;

import java.util.List;

public class SubscribeDto {

    public NameProfileTuple target;
    public List<NameProfileTuple> subscriptions;

    public NameProfileTuple getTarget() {
        return target;
    }

    public void setTarget(NameProfileTuple target) {
        this.target = target;
    }

    public List<NameProfileTuple> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<NameProfileTuple> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
