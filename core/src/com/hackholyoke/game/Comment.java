package com.hackholyoke.game;

/**
 * Data structure to contain lie or truth.
 */
public class Comment {
    public enum EFFECT {ANGER, HURT, TRUST, NONE}
    public enum TARGET {SELF, SITUATION, OTHER}
    public enum STRENGTH {WEAK, AVERAGE, STRONG}
    // if false, lie
    private final boolean truth;
    private final boolean positive;
    private final TARGET target;
    private final String flavor;
    private final STRENGTH strength;
    private final EFFECT effect;

    public Comment(boolean truth, String target, String flavor, String strength, String effect) {
        this.truth = truth;
        // assigning target
        switch (target) {
            case "SELF":
                this.target = TARGET.SELF;
                break;
            case "SITUATION":
                this.target = TARGET.SITUATION;
                break;
            default:
                this.target = TARGET.OTHER;
                break;
        }
        this.flavor = flavor;
        // assigning strength
        if (strength.contains("STRONG")) {
            this.strength = STRENGTH.STRONG;
        } else if (strength.contains("WEAK")) {
            this.strength = STRENGTH.WEAK;
        } else {
            this.strength = STRENGTH.AVERAGE;
        }
        positive = strength.contains("POS");
        // assigning effect
        switch (effect) {
            case "ANGER":
                this.effect = EFFECT.ANGER;
                break;
            case "HURT":
                this.effect = EFFECT.HURT;
                break;
            case "TRUST":
                this.effect = EFFECT.TRUST;
                break;
            default:
                this.effect = EFFECT.NONE;
                break;
        }
    }

    public Comment(boolean truth, boolean positive, TARGET target, String flavor, STRENGTH strength, EFFECT effect) {
        this.truth = truth;
        this.positive = positive;
        this.target = target;
        this.flavor = flavor;
        this.strength = strength;
        this.effect = effect;
    }

    public Comment copy() {
        return new Comment(truth, positive, target, flavor, strength, effect);
    }

    public boolean isTruth() {
        return truth;
    }

    public String getFlavor() {
        return flavor;
    }

    public EFFECT getEffect() {
        return effect;
    }

    public TARGET getTarget() {
        return target;
    }

    public boolean isPositive() {
        return positive;
    }

    public STRENGTH getStrength() {
        return strength;
    }
}
