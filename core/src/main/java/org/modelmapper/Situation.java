package org.modelmapper;

import java.util.HashMap;

/**
 * Situation that support condition on map.
 *
 * @param <S> source type
 * @param <D> destination type
 *
 * @author dreamking60
 */

public class Situation<S,D> {
    private HashMap situation;
    private TypeMap way;

    public Situation(HashMap situation, TypeMap way) {
        this.situation = situation;
        this.way = way;
    }

    public HashMap getSituation() {
        return situation;
    }

    public TypeMap getWay() {
        return way;
    }

}
