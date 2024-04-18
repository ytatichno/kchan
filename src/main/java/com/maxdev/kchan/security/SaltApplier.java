package com.maxdev.kchan.security;

/**
 * Created by ytati
 * on 23.03.2024.
 */
public interface SaltApplier {
    public String applySalt(String password, String salt, Integer saltMode);
    public void setDefaultSaltMode(Integer saltMode);
}
