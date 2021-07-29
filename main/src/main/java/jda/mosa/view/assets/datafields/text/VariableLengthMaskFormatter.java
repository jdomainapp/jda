package jda.mosa.view.assets.datafields.text;

import java.text.ParseException;

import javax.swing.text.MaskFormatter;

/**
 * @overview 
 *  Represents a variable-length {@link MaskFormatter} that takes a normal mask string as input but allows the user 
 *  to enter values whose lengths are less than the number of characters in the mask.
 *  
 *  <p>Adapted from the code example given in <a href="https://community.oracle.com/thread/1365095?start=0&tstart=0">https://community.oracle.com/thread/1365095?start=0&tstart=0</a>.
 * @author dmle
 *
 */
public class VariableLengthMaskFormatter extends MaskFormatter {

  private static final long serialVersionUID = -188517481498656333L;

  public VariableLengthMaskFormatter() {
    super();
  }

  public VariableLengthMaskFormatter(String mask) throws ParseException {
    super(mask);
  }

// dmle: commented out as these methods simply forward to super
//    - uncomment them if customisation is needed
//  /**
//   * Override the setMask method
//   */
//  public void setMask(String mask) throws ParseException {
//    super.setMask(mask);
//  }
//
//  /**
//   * Update our blank representation whenever the mask is updated.
//   */
//  public void setPlaceholderCharacter(char placeholder) {
//    super.setPlaceholderCharacter(placeholder);
//  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.text.MaskFormatter#stringToValue(java.lang.String)
   */
  /**
   * @effects 
   *  update mask based on the length of <tt>value</tt> as specified in {@link #getMaskForString(String, String)}; 
   *  return converted value (as expected)
   */
  @Override
  public Object stringToValue(String value) throws ParseException {
    Object rv;

    // Get the mask
    String mask = getMask();

    if (mask != null) {
      // Change the mask based upon the string passed in
      setMask(getMaskForString(mask, value));

      // Using the substring of the given string up to the mask length,
      // convert it to an object
      rv = super.stringToValue(value.substring(0, getMask().length()));

      // Change mask back to original mask
      setMask(mask);
    } else
      rv = super.stringToValue(value);

    // Return converted value
    return rv;
  }

  /**
   * Answer what the mask should be for the given string based on the given
   * mask. This mask is just the subset of the given mask up to the length of
   * the given string or where the first placeholder character occurs in the
   * given string. The underlying assumption here is that the given string is
   * simply the text from the formatted field upon which we are installed.
   *
   * @param value
   *          The string for which to determine the mask
   * @return A mask appropriate for the given string
   */
  protected String getMaskForString(String mask, String value) {
    StringBuffer sb = new StringBuffer();
    int maskLength = mask.length();
    char placeHolder = getPlaceholderCharacter();
    for (int k = 0, size = value.length(); k < size && k < maskLength; k++) {
      if (placeHolder == value.charAt(k)) {
        // break;
        sb.append(placeHolder); // dmle: append(' ');
      } else {
        sb.append(mask.charAt(k));
      }
    }
    return sb.toString();
  }
}
