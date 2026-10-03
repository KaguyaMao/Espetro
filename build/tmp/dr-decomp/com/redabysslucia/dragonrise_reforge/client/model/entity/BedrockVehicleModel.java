/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockBone
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockModel
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockModelPOJO
 */
package com.redabysslucia.dragonrise_reforge.client.model.entity;

import com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockBone;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockModelPOJO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BedrockVehicleModel
extends BedrockModel {
    public static final Pattern WHEEL_PATTERN = Pattern.compile("^wheel(?<direction>[LR]).*$");
    public static final Pattern SHELL_PATTERN = Pattern.compile("^shell(?<id>\\d+)$");
    public static final Pattern TRACK_PATTERN = Pattern.compile("^track(?<type>Mov|Rot)(?<direction>[LR])(?<id>\\d+)$");
    public static final Pattern FLARE_PATTERN = Pattern.compile("^flare.*");
    public static final Pattern DOG_TAG_PATTERN = Pattern.compile("^.*_dogTag$");
    public List<BedrockBone> leftWheels = new ArrayList<BedrockBone>();
    public List<BedrockBone> rightWheels = new ArrayList<BedrockBone>();
    public List<BedrockBone> leftWheelsTurn = new ArrayList<BedrockBone>();
    public List<BedrockBone> rightWheelsTurn = new ArrayList<BedrockBone>();
    public List<BedrockBone> shell = new ArrayList<BedrockBone>();
    public List<BedrockBone> leftTrackMove = new ArrayList<BedrockBone>();
    public List<BedrockBone> leftTrackRot = new ArrayList<BedrockBone>();
    public List<BedrockBone> rightTrackMove = new ArrayList<BedrockBone>();
    public List<BedrockBone> rightTrackRot = new ArrayList<BedrockBone>();
    public List<BedrockBone> flareBones = new ArrayList<BedrockBone>();
    public List<BedrockBone> dogTagBones = new ArrayList<BedrockBone>();

    public BedrockVehicleModel(BedrockModelPOJO pojo) {
        super(pojo);
    }

    public void init() {
        HashMap map = this.getBoneMap();
        HashMap<Integer, BedrockBone> shellMap = new HashMap<Integer, BedrockBone>();
        HashMap<Integer, BedrockBone> leftTrackMoveMap = new HashMap<Integer, BedrockBone>();
        HashMap<Integer, BedrockBone> leftTrackRotMap = new HashMap<Integer, BedrockBone>();
        HashMap<Integer, BedrockBone> rightTrackMoveMap = new HashMap<Integer, BedrockBone>();
        HashMap<Integer, BedrockBone> rightTrackRotMap = new HashMap<Integer, BedrockBone>();
        for (Map.Entry entry : map.entrySet()) {
            Matcher dogTagMatcher;
            Matcher flareMatcher;
            Matcher trackMatcher;
            Matcher shellMatcher;
            String name = (String)entry.getKey();
            BedrockBone bone = (BedrockBone)entry.getValue();
            Matcher wheelMatcher = WHEEL_PATTERN.matcher(name);
            if (wheelMatcher.matches()) {
                boolean left = "L".equals(wheelMatcher.group("direction"));
                boolean turn = name.endsWith("Turn");
                if (left) {
                    if (turn) {
                        this.leftWheelsTurn.add(bone);
                    } else {
                        this.leftWheels.add(bone);
                    }
                } else if (turn) {
                    this.rightWheelsTurn.add(bone);
                } else {
                    this.rightWheels.add(bone);
                }
            }
            if ((shellMatcher = SHELL_PATTERN.matcher(name)).matches()) {
                int index = Integer.parseInt(shellMatcher.group("id"));
                shellMap.put(index, bone);
            }
            if ((trackMatcher = TRACK_PATTERN.matcher(name)).matches()) {
                boolean isRot = "Rot".equals(trackMatcher.group("type"));
                boolean isL = "L".equals(trackMatcher.group("direction"));
                int index = Integer.parseInt(trackMatcher.group("id"));
                if (isRot) {
                    if (isL) {
                        leftTrackRotMap.put(index, bone);
                    } else {
                        rightTrackRotMap.put(index, bone);
                    }
                } else if (isL) {
                    leftTrackMoveMap.put(index, bone);
                } else {
                    rightTrackMoveMap.put(index, bone);
                }
            }
            if ((flareMatcher = FLARE_PATTERN.matcher(name)).matches()) {
                this.flareBones.add(bone);
            }
            if (!(dogTagMatcher = DOG_TAG_PATTERN.matcher(name)).matches()) continue;
            this.dogTagBones.add(bone);
        }
        this.shell.addAll(shellMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
        this.leftTrackMove.addAll(leftTrackMoveMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
        this.leftTrackRot.addAll(leftTrackRotMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
        this.rightTrackMove.addAll(rightTrackMoveMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
        this.rightTrackRot.addAll(rightTrackRotMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
    }
}

