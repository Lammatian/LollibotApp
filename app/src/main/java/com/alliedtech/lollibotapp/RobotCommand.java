package com.alliedtech.lollibotapp;

public interface RobotCommand {
    //TODO: Document describing protocol
    String COMMAND_MOVE_LINES = "mvl";
    String COMMAND_BATTERY_STATUS = "btr";
    String COMMAND_ROBOT_STATUS = "sts";
    String COMMAND_UPDATE_SCHEDULE = "ups";
    String COMMAND_REMOVE_SCHEDULE = "rms";
    String COMMAND_SET_LINE_COUNT = "snl";
    String COMMAND_MOVE_TO_MIDDLE = "mtm";
    String COMMAND_MOVE_FROM_MIDDLE = "mfm";
    String COMMAND_BATTERY_STATUS_UPDATE = "bsu";
    String COMMAND_STATE_CHANGE = "stc";
    String COMMAND_WARNING = "wng";
}
