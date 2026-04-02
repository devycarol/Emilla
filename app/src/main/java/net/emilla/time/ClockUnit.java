package net.emilla.time;

public enum ClockUnit {
    HOUR {
        @Override
        public int toSeconds(int value) {
            return value * 60 * 60;
        }

        @Override
        public double toSeconds(double value) {
            return value * 60.0 * 60.0;
        }
    },
    MINUTE {
        @Override
        public int toSeconds(int value) {
            return value * 60;
        }

        @Override
        public double toSeconds(double value) {
            return value * 60.0;
        }
    },
    SECOND {
        @Override
        public int toSeconds(int value) {
            return value;
        }

        @Override
        public double toSeconds(double value) {
            return value;
        }
    },
;
    public abstract int toSeconds(int value);
    public abstract double toSeconds(double value);
}
