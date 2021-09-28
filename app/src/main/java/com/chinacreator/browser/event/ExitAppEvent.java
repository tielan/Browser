package com.chinacreator.browser.event;

public class ExitAppEvent {
    private boolean exitApp;
    private boolean restartApp;

    public boolean isExitApp() {
        return exitApp;
    }

    public ExitAppEvent(boolean exitApp, boolean restartApp) {
        this.exitApp = exitApp;
        this.restartApp = restartApp;
    }

    public void setExitApp(boolean exitApp) {
        this.exitApp = exitApp;
    }

    public boolean isRestartApp() {
        return restartApp;
    }

    public void setRestartApp(boolean restartApp) {
        this.restartApp = restartApp;
    }
}
