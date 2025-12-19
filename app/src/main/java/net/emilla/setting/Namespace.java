package net.emilla.setting;

import android.content.ContentResolver;
import android.provider.Settings;

enum Namespace {
    GLOBAL {
        @Override
        public int getInt(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return Settings.Global.getInt(cr, name);
        }

        @Override
        public long getLong(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return Settings.Global.getLong(cr, name);
        }

        @Override
        public float getFloat(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return Settings.Global.getFloat(cr, name);
        }

        @Override
        public String getString(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return require(Settings.Global.getString(cr, name));
        }

        @Override
        public boolean putInt(ContentResolver cr, String name, int value) {
            try {
                return Settings.Global.putInt(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }

        @Override
        public boolean putLong(ContentResolver cr, String name, long value) {
            try {
                return Settings.Global.putLong(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }

        @Override
        public boolean putFloat(ContentResolver cr, String name, float value) {
            try {
                return Settings.Global.putFloat(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }

        @Override
        public boolean putString(ContentResolver cr, String name, String value) {
            try {
                return Settings.Global.putString(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }
    },
    SYSTEM {
        @Override
        public int getInt(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return Settings.System.getInt(cr, name);
        }

        @Override
        public long getLong(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return Settings.System.getLong(cr, name);
        }

        @Override
        public float getFloat(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return Settings.System.getFloat(cr, name);
        }

        @Override
        public String getString(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return require(Settings.System.getString(cr, name));
        }

        @Override
        public boolean putInt(ContentResolver cr, String name, int value) {
            try {
                return Settings.System.putInt(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }

        @Override
        public boolean putLong(ContentResolver cr, String name, long value) {
            try {
                return Settings.System.putLong(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }

        @Override
        public boolean putFloat(ContentResolver cr, String name, float value) {
            try {
                return Settings.System.putFloat(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }

        @Override
        public boolean putString(ContentResolver cr, String name, String value) {
            try {
                return Settings.System.putString(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }
    },
    SECURE {
        @Override
        public int getInt(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return Settings.Secure.getInt(cr, name);
        }

        @Override
        public long getLong(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return Settings.Secure.getLong(cr, name);
        }

        @Override
        public float getFloat(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return Settings.Secure.getFloat(cr, name);
        }

        @Override
        public String getString(ContentResolver cr, String name)
            throws Settings.SettingNotFoundException {

            return require(Settings.Secure.getString(cr, name));
        }

        @Override
        public boolean putInt(ContentResolver cr, String name, int value) {
            try {
                return Settings.Secure.putInt(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }

        @Override
        public boolean putLong(ContentResolver cr, String name, long value) {
            try {
                return Settings.Secure.putLong(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }

        @Override
        public boolean putFloat(ContentResolver cr, String name, float value) {
            try {
                return Settings.Secure.putFloat(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }

        @Override
        public boolean putString(ContentResolver cr, String name, String value) {
            try {
                return Settings.Secure.putString(cr, name, value);
            } catch (SecurityException e) {
                return false;
            }
        }
    };

    private static String require(String s) throws Settings.SettingNotFoundException {
        if (s == null) {
            throw new Settings.SettingNotFoundException("String setting not found");
        }
        return s;
    }

    public abstract int getInt(ContentResolver cr, String name)
        throws Settings.SettingNotFoundException;
    public abstract long getLong(ContentResolver cr, String name)
        throws Settings.SettingNotFoundException;
    public abstract float getFloat(ContentResolver cr, String name)
        throws Settings.SettingNotFoundException;
    public abstract String getString(ContentResolver cr, String name)
        throws Settings.SettingNotFoundException;

    public final boolean getBoolean(ContentResolver cr, String name)
        throws Settings.SettingNotFoundException {

        return switch (getInt(cr, name)) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new Settings.SettingNotFoundException("Not a boolean setting");
        };
    }

    public abstract boolean putInt(ContentResolver cr, String name, int value);
    public abstract boolean putLong(ContentResolver cr, String name, long value);
    public abstract boolean putFloat(ContentResolver cr, String name, float value);
    public abstract boolean putString(ContentResolver cr, String name, String value);

    public final boolean putBoolean(ContentResolver cr, String name, boolean value) {
        return putInt(cr, name, value ? 1 : 0);
    }

    public final boolean delete(ContentResolver cr, String name) {
        return putString(cr, name, null);
    }

}
