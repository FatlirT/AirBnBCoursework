/**
 * This interface is used by the panels in the GUI.
 * When a window is "popped out", it will disable shared state so it won't update or be updated by the rest of the GUI.
 * 
 * @author Skye Macdonald (k19015078)
 * @version 2020-03-29
 */
public interface SharedStateController {
    /**
     * Disable sharing of state.
     * This normally involves disabling reading or writing to the shared state (static fields) of a Controller,
     * with the exception of Initialisation.
     */
    void disableSharedState();
}
