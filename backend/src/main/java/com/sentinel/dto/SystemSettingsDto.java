package com.sentinel.dto;

public class SystemSettingsDto {

    private String aiProvider;
    private String aiModel;
    private boolean hasApiKey;
    private String maskedApiKey;
    private String storageLocation;
    private int maxFileSizeMb;
    private boolean isDemoMode;
    private String databaseProfile;
    private String appVersion;

    public SystemSettingsDto() {}

    public String getAiProvider() { return aiProvider; }
    public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }

    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }

    public boolean isHasApiKey() { return hasApiKey; }
    public void setHasApiKey(boolean hasApiKey) { this.hasApiKey = hasApiKey; }

    public String getMaskedApiKey() { return maskedApiKey; }
    public void setMaskedApiKey(String maskedApiKey) { this.maskedApiKey = maskedApiKey; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public int getMaxFileSizeMb() { return maxFileSizeMb; }
    public void setMaxFileSizeMb(int maxFileSizeMb) { this.maxFileSizeMb = maxFileSizeMb; }

    public boolean isDemoMode() { return isDemoMode; }
    public void setDemoMode(boolean demoMode) { isDemoMode = demoMode; }

    public String getDatabaseProfile() { return databaseProfile; }
    public void setDatabaseProfile(String databaseProfile) { this.databaseProfile = databaseProfile; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
}
