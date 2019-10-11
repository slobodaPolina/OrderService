package entity;

import java.util.EnumSet;
import java.util.Set;

public enum Status {
    COLLECTING {
        @Override
        public Set<Status> nextStatus() {
            return EnumSet.of(PAYED, FAILED);
        }
    },
    PAYED {
        @Override
        public Set<Status> nextStatus() {
            return EnumSet.of(SHIPPING, CANCELLED);
        }
    },
    SHIPPING {
        @Override
        public Set<Status> nextStatus() {
            return EnumSet.of(COMPLETE);
        }
    },
    COMPLETE,
    FAILED,
    CANCELLED;

    public Set<Status> nextStatus() {
        return EnumSet.noneOf(Status.class);
    }
}
