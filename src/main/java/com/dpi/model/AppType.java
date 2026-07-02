package com.dpi.model;

public enum AppType {
    UNKNOWN,
    HTTP,
    HTTPS,
    DNS,
    GOOGLE,
    YOUTUBE,
    FACEBOOK,
    INSTAGRAM,
    TWITTER,
    NETFLIX,
    AMAZON,
    GITHUB,
    DISCORD,
    ZOOM,
    TELEGRAM,
    TIKTOK,
    SPOTIFY,
    MICROSOFT,
    APPLE,
    CLOUDFLARE;

    public static AppType fromSni(String sni) {
        if (sni == null || sni.isEmpty()) return UNKNOWN;

        sni = sni.toLowerCase();

        if (sni.contains("youtube"))     return YOUTUBE;
        if (sni.contains("facebook"))    return FACEBOOK;
        if (sni.contains("instagram"))   return INSTAGRAM;
        if (sni.contains("twitter"))     return TWITTER;
        if (sni.contains("netflix"))     return NETFLIX;
        if (sni.contains("amazon"))      return AMAZON;
        if (sni.contains("github"))      return GITHUB;
        if (sni.contains("discord"))     return DISCORD;
        if (sni.contains("zoom"))        return ZOOM;
        if (sni.contains("telegram"))    return TELEGRAM;
        if (sni.contains("tiktok"))      return TIKTOK;
        if (sni.contains("spotify"))     return SPOTIFY;
        if (sni.contains("microsoft"))   return MICROSOFT;
        if (sni.contains("apple"))       return APPLE;
        if (sni.contains("cloudflare"))  return CLOUDFLARE;
        if (sni.contains("googlevideo"))  return YOUTUBE;
        if (sni.contains("youtube"))      return YOUTUBE;
        if (sni.contains("google"))      return GOOGLE;

        return UNKNOWN;
    }
}