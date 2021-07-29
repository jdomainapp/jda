package jda.modules.common;

import jda.modules.common.types.Null;


public class CommonConstants {

  // COMMON //
  /**
   * The Null String (specified by unicode)
   */
  public static final String NullString = "\u2400";
  
  /** the <tt>"null"</tt> string value (used in parsing text data) */
  public static final String NullValue = "null";

  /** the <tt>""</tt> (empty) string */
  public static final String EmptyString = "";

  /** the {@link Null} class */
  public static final Class<Null> NullType = Null.class;

  /** the empty <tt>String[]</tt> array */
  public static final String[] EmptyArray = {};

  public static final double DEFAULT_MIN_VALUE = Double.NEGATIVE_INFINITY;
  public static final double DEFAULT_MAX_VALUE = Double.POSITIVE_INFINITY;

  private CommonConstants() {}
  
  
}
