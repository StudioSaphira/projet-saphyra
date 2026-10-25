package net.saphyra.common.constants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Global constants used throughout Saphyra. */
public final class ModConstants {

    /** Mod identifier. */
    public static final String MOD_ID = "saphyra";

    /** Mod logger. */
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();

    /** Prevent instantiation. */
    private ModConstants() {}
}
