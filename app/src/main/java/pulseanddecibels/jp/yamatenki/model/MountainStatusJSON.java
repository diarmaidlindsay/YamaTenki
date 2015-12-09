package pulseanddecibels.jp.yamatenki.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diarmaid Lindsay on 2015/12/09.
 * Copyright Pulse and Decibels 2015
 */
public class MountainStatusJSON {
    private List<StatusArrayElement> list;

    public MountainStatusJSON() {
        list = new ArrayList<>();
    }

    public MountainStatusJSON(List<StatusArrayElement> list) {
        this.list = list;
    }

    public List<StatusArrayElement> getList() {
        return list;
    }
}
