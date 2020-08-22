package me.bristermitten.pdm;

import me.bristermitten.pdm.logging.PDMLogger;
import me.bristermitten.pdm.logging.PDMLogging;


public class DependencyManager
{

    public static final String PDM_DIRECTORY_NAME = "PluginLibraries";

    private final PDMLogging pdmLogging;
    private final PDMLogger logger;

    public DependencyManager(PDMLogging pdmLogging)
    {
        this.pdmLogging = pdmLogging;
        this.logger = pdmLogging.getLogger(getClass().getName());
    }
}
