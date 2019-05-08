package com.android.mosof.highscore;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Highscore {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @NonNull
    private String player;

    @NonNull
    private Integer tries;

    @NonNull
    private Integer holes;

    @NonNull
    private Integer pins;

    @NonNull
    private Boolean emptyPins;

    @NonNull
    private Boolean duplicatePins;

    public Highscore(@NonNull String player, int tries, int holes, int pins, boolean emptyPins, boolean duplicatePins) {
        this.player = player;
        this.tries = tries;
        this.holes = holes;
        this.pins = pins;
        this.emptyPins = emptyPins;
        this.duplicatePins = duplicatePins;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setPlayer(@NonNull String player) {
        this.player = player;
    }

    @NonNull
    public String getPlayer() {
        return player;
    }

    public void setTries(@NonNull Integer tries) {
        this.tries = tries;
    }

    @NonNull
    public Integer getTries() {
        return tries;
    }

    public void setHoles(@NonNull Integer holes) {
        this.holes = holes;
    }

    @NonNull
    public Integer getHoles() {
        return holes;
    }

    public void setPins(@NonNull Integer pins) {
        this.pins = pins;
    }

    @NonNull
    public Integer getPins() {
        return pins;
    }

    public void setEmptyPins(@NonNull Boolean emptyPins) {
        this.emptyPins = emptyPins;
    }

    @NonNull
    public Boolean getEmptyPins() {
        return emptyPins;
    }

    public void setDuplicatePins(@NonNull Boolean duplicatePins) {
        this.duplicatePins = duplicatePins;
    }

    @NonNull
    public Boolean getDuplicatePins() {
        return duplicatePins;
    }
}
