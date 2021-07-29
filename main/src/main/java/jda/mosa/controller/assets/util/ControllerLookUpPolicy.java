/**
 * 
 */
package jda.mosa.controller.assets.util;

/* v2.5.4: added look up option */
/**
 * Controller look-up policy constants that determine how controllers are
 * looked up by the look-up methods
 */
public enum ControllerLookUpPolicy {
  /** the first matching controller */
  First,
  /** all matching controllers */
  All,
  /** the viewer-type controller only */
  ViewerOnly,
  /**
   * the primary controller only (i.e. the controller whose module is the
   * primary module for the domain class to which it is associated)
   */
  PrimaryOnly
}