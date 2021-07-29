package jda.modules.dcsl.syntax;

public class DCSLConstants {

  // DCSL ///
  /** dmle: use this for data-specific domain classes (e.g. Customer) */
  public static final String DEFAULT_SCHEMA = "APP";
  /** dmle: use this for configuration-related system classes (e.g. Mapping) */
  public static final String CONFIG_SCHEMA = "app_config";
  /**
   * dmle: use this for security-related system classes (e.g. DomainUser, Role,
   * etc.)
   */
  public static final String SECURITY_SCHEMA = "app_security";
  /**
   * a constant that represents the special case of the cardinality 'more' or
   * '*' of the many side of an association
   */
  public static final int CARD_MORE = Integer.MAX_VALUE;
  
  /**
   * String version of {@link #CARD_MORE}.
   * <br>IMPORTANT:
   * Remember to change this constant when {@link #CARD_MORE} is renamed!
   */
  public static final String CARD_MORE_STRING = DCSLConstants.class.getSimpleName() + ".CARD_MORE";

  /**
   * a constant that represents the min value of lower-bound cardinality of an
   * association end
   */
  public static final int CARD_MIN = 0;
  /**
   * @version 5.2
   */
  public static final String DEFAULT_DOPT_REQUIRES = "true";
  /**
   * @version 5.2
   */
  public static final String DEFAULT_DOPT_EFFECTS = "true";
  /**
   * @version 5.2
   */
  
  public static final int DEFAULT_DATTR_LENGTH = -1;

  /**
   * default name if the id attribute of the domain class.
   */
  public static final String ATTRIB_ID_DEFAULT_NAME = "id";

  private DCSLConstants() {}
  
  
}
