package com.maxdev.kchan.security;

/**
 * Created by ytati
 * on 23.03.2024.
 */
public class SimpleSaltApplier implements SaltApplier {

    SimpleSaltMode defaultSaltMode = SimpleSaltMode.DUPLICATE_TO_BOTH_ENDS;

    @Override
    public String applySalt(String password, String salt, Integer saltMode) {
        SimpleSaltMode mode = SimpleSaltMode.valueOf(saltMode);
        return switch (mode) {
            case NO_SALT -> password;
            case DUPLICATE_TO_BOTH_ENDS -> salt + password + salt;
            case SPLIT_SALT_TO_BOTH_ENDS -> salt.substring(0, salt.length() / 2)
                    + password
                    + salt.substring(salt.length() / 2);
        };
    }

    @Override
    public void setDefaultSaltMode(Integer saltMode) {
        defaultSaltMode = SimpleSaltMode.valueOf(saltMode);
    }

    public enum SimpleSaltMode {
        NO_SALT(0),
        DUPLICATE_TO_BOTH_ENDS(1),
        SPLIT_SALT_TO_BOTH_ENDS(2);
        private final int index;

        SimpleSaltMode(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static SimpleSaltMode valueOf(int index) {
            return switch (index) {
                case 0 -> NO_SALT;
                case 1 -> DUPLICATE_TO_BOTH_ENDS;
                case 2 -> SPLIT_SALT_TO_BOTH_ENDS;
                default -> throw new IllegalArgumentException("other salt modes aren't processed by SimpleSaltApplier");
            };
        }

    }
}
