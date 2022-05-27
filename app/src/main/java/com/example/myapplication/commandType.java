package com.example.myapplication;

public enum commandType {
    VERSION(0),
    MAC(1),
    RANDOM(2),
    FIRST(3),
    BOOT(4),
    PAIR(5),
    UNLOCK(6),
    ADD_FINGER(7),
    VERIFY(8),
    DELETE_FINGER(9),
    EMPTY_FINGER(10),
    RESET(11),
    MORSE(12),
    BATTERY(13),
    TL2TIME(14),
    STATUS(15),
    DFU(16),
    TL1HIS(17),
    TL2HIS(18),
    PACKAGE(23),
    DELETE_MORSE(24),
    FINGER_DATA(26),
    DISCONNECT(28),
    FINGER_QTY(29),
    ENROLL_CANCEL(30),
    FINGER_VERIFY(31),
    OTA(32),
    BOX_BOOT(33),
    BOX_PAIR(34),
    FACTORY(35),
    GET_FINGER(36),
    CLEAR_KEY(37),
    OTA_START(38),
    OTA_END(39),
    ORIGINAL(40),
    VERIFY_OFFLINE(41),
    FACTORY_MOTOR(45),
    FACTORY_BATTERY(46),
    FACTORY_LED(47),
    FACTORY_FINGER(48),
    FACTORY_EMPTY_FINGER(49),
    FACTORY_EXIT(50),
    LOCK_CONNECTED(51),
    DELETE_HIS(52),
    HIS3(54),
    GET_TIME(55),
    SYNC_RECORD(56),
    ERROR(-2),
    UNKNOWN(-1),
    IGNORE(-12),
    INTERNAL_DEVICE_NONE_APP(-3),
    INTERNAL_CMD_NONE_APP(-4),
    INTERNAL_CMD_UNKNOWN_APP(-5),
    INTERNAL_DEVICE_NONE_DEVICE(-6),
    INTERNAL_VERIFY_HAS_ENCRYPT(-7),
    INTERNAL_NO_RANDOM_REGISTERED(-8),
    INTERNAL_VERIFY_GEN_ERROR(-9),
    INTERNAL_DECRYPT_LENGTH(-10),
    INTERNAL_CACHE_LOST(-11);

    public static final commandConverter Companion = new commandConverter();
    public final int e;

    /* compiled from: Proguard */
    /* renamed from: com.mrtan.data.h8$a */
    /* loaded from: classes.dex */
    public static final class commandConverter {

        /* renamed from: a */
        public final commandType intToCommand(int i) {
            switch (i) {
                case -12:
                    return commandType.IGNORE;
                case -11:
                    return commandType.INTERNAL_CACHE_LOST;
                case -10:
                    return commandType.INTERNAL_DECRYPT_LENGTH;
                case -9:
                    return commandType.INTERNAL_VERIFY_GEN_ERROR;
                case -8:
                    return commandType.INTERNAL_NO_RANDOM_REGISTERED;
                case -7:
                    return commandType.INTERNAL_VERIFY_HAS_ENCRYPT;
                case -6:
                    return commandType.INTERNAL_DEVICE_NONE_DEVICE;
                case -5:
                    return commandType.INTERNAL_CMD_UNKNOWN_APP;
                case -4:
                    return commandType.INTERNAL_CMD_NONE_APP;
                case -3:
                    return commandType.INTERNAL_DEVICE_NONE_APP;
                case -2:
                    return commandType.ERROR;
                case -1:
                case 19:
                case 20:
                case 21:
                case 22:
                case 25:
                case 27:
                case 42:
                case 43:
                case 44:
                case 53:
                default:
                    return commandType.UNKNOWN;
                case 0:
                    return commandType.VERSION;
                case 1:
                    return commandType.MAC;
                case 2:
                    return commandType.RANDOM;
                case 3:
                    return commandType.FIRST;
                case 4:
                    return commandType.BOOT;
                case 5:
                    return commandType.PAIR;
                case 6:
                    return commandType.UNLOCK;
                case 7:
                    return commandType.ADD_FINGER;
                case 8:
                    return commandType.VERIFY;
                case 9:
                    return commandType.DELETE_FINGER;
                case 10:
                    return commandType.EMPTY_FINGER;
                case 11:
                    return commandType.RESET;
                case 12:
                    return commandType.MORSE;
                case 13:
                    return commandType.BATTERY;
                case 14:
                    return commandType.TL2TIME;
                case 15:
                    return commandType.STATUS;
                case 16:
                    return commandType.DFU;
                case 17:
                    return commandType.TL1HIS;
                case 18:
                    return commandType.TL2HIS;
                case 23:
                    return commandType.PACKAGE;
                case 24:
                    return commandType.DELETE_MORSE;
                case 26:
                    return commandType.FINGER_DATA;
                case 28:
                    return commandType.DISCONNECT;
                case 29:
                    return commandType.FINGER_QTY;
                case 30:
                    return commandType.ENROLL_CANCEL;
                case 31:
                    return commandType.FINGER_VERIFY;
                case 32:
                    return commandType.OTA;
                case 33:
                    return commandType.BOX_BOOT;
                case 34:
                    return commandType.BOX_PAIR;
                case 35:
                    return commandType.FACTORY;
                case 36:
                    return commandType.GET_FINGER;
                case 37:
                    return commandType.CLEAR_KEY;
                case 38:
                    return commandType.OTA_START;
                case 39:
                    return commandType.OTA_END;
                case 40:
                    return commandType.ORIGINAL;
                case 41:
                    return commandType.VERIFY_OFFLINE;
                case 45:
                    return commandType.FACTORY_MOTOR;
                case 46:
                    return commandType.FACTORY_BATTERY;
                case 47:
                    return commandType.FACTORY_LED;
                case 48:
                    return commandType.FACTORY_FINGER;
                case 49:
                    return commandType.FACTORY_EMPTY_FINGER;
                case 50:
                    return commandType.FACTORY_EXIT;
                case 51:
                    return commandType.LOCK_CONNECTED;
                case 52:
                    return commandType.DELETE_HIS;
                case 54:
                    return commandType.HIS3;
                case 55:
                    return commandType.GET_TIME;
                case 56:
                    return commandType.SYNC_RECORD;
            }
        }
    }

    commandType(int i) {
        this.e = i;
    }

    public final int getValue() {
        return this.e;
    }
}