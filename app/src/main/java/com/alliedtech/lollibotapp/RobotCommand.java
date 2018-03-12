package com.alliedtech.lollibotapp;

public interface RobotCommand {
    //TODO: Document describing protocol
    String OUT_COMMAND_MOVE_LINES = "mvl";
    String OUT_COMMAND_BATTERY_STATUS = "btr";
    String OUT_COMMAND_ROBOT_STATUS = "sts";
    String OUT_COMMAND_UPDATE_SCHEDULE = "ups";
    String OUT_COMMAND_REMOVE_SCHEDULE = "rms";
    String OUT_COMMAND_SET_LINE_COUNT = "snl";
    String OUT_COMMAND_MOVE_TO_MIDDLE = "mtm";
    String OUT_COMMAND_MOVE_FROM_MIDDLE = "mfm";
    String IN_COMMAND_BATTERY_STATUS_UPDATE = "bsu";
    String IN_COMMAND_STATE_CHANGE = "stc";
    String IN_COMMAND_WARNING = "wng";
    String IN_COMMAND_SCHEDULE_START = "scs";
    String IN_COMMAND_SCHEDULE_END = "sce";
    String IN_COMMAND_SCHEDULE_DAY = "scd";
}
