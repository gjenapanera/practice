package com.mgm.dmp.common.model;

public enum ReservationState {
    Saved, Booked, Cancelled;

    public static ReservationState valueOf(com.mgmresorts.aurora.common.ReservationState state) {
        if (null != state) {
            switch (state) {
            case Saved:
                return Saved;
            case Booked:
                return Booked;
            case Cancelled:
                return Cancelled;
            default:
                break;
            }
        }
        return null;
    }
}
