package jda.modules.common.exceptions;

import java.text.MessageFormat;

public class ConstraintViolationException extends ApplicationRuntimeException {
  public static enum Code implements InfoCode {
    /**Dữ liệu nhập không đúng: {0}*/
    INVALID_VALUE("Dữ liệu nhập không đúng: {0}"), //
    /**"Kiểu dữ liệu không đúng {0} (cần kiểu {1})"*/
    INVALID_DATA_TYPE("Kiểu dữ liệu không đúng {0} (cần kiểu {1})"), 
    /**
     * 0: object action
     * 1: associated object
     * 2: association name
     * 3: min card
     * 4: max card
     * 5: current link count
     */
    CARDINALITY_CONSTRAINT_NOT_SATISFIED("Tác vụ ({0}: {1}) không thỏa mãn ràng buộc số lượng dữ liệu: {2} [{3},{4}] (hiện tại: {5})"), 
    INVALID_UPDATE_OF_MIXED_ATTRIBUTES(""), 
    /**
     * 0: attribute name
     * 1: attribute value
     */
    INVALID_VALUE_NOT_UNIQUE("{0}: Giá trị không duy nhất: {1}"), 
    /**
     * 0: attribute name
     * 1: attribute value
     */
    INVALID_VALUE_NOT_SPECIFIED_WHEN_REQUIRED("{0}: Giá trị bắt buộc nhưng không nhập hoặc không nhập đúng: {1}"),
    /**
     * 0: attribute name
     * 1: attribute value
     * 2: attribute value length 
     * 3: expected attribute length
     */
    INVALID_ATTRIBUTE_LENGTH("{0}: Độ dài giá trị không hợp lệ: {1} (dài: {2}, yêu cầu: {3})"), 
    /**
     * 0: attribute name
     * 1: attribute value
     */
    INVALID_NUMERIC_VALUE("{0}: Giá trị không phải dạng số: {1}"), 
    /**
     * 0: attribute name
     * 1: attribute value
     */
    INVALID_BOOLEAN_VALUE("{0}: Giá trị không phải luận lí (có/không): {1}"), 
    /**
     * 0: attribute name
     * 1: attribute value
     */
    INVALID_COLOR_VALUE("{0}: Giá trị không phải dạng mầu sắc: {1}"), 
    /**
     * 0: attribute name
     * 1: attribute value
     */
    INVALID_FONT_VALUE("{0}: Giá trị không phải dạng phông chữ: {1}"), 
    /**
     * 0: attribute name
     * 1: attribute value
     */
    INVALID_DATE_VALUE("{0}: Giá trị không phải dạng ngày: {1}"),
    /**
     * 0: attribute name
     * 1: attribute value
     */
    INVALID_DATE_VALUE_NOT_IN_RANGE("{0}: Ngày không thỏa mãn điều kiện khoảng giá trị: {1}"),
    /**
     * 0: attribute name
     * 1: attribute value
     */
    INVALID_ENUM_VALUE("{0}: Giá trị không phải dạng hằng (enum): {1}"),
    /**
     * 0: attribute name
     * 1: attribute value
     * 2: attribute's min value 
     */
    INVALID_VALUE_LOWER_THAN_MIN("{0}: Giá trị không hợp lệ: {1} (< {2})"), 
    /**
     * 0: attribute name
     * 1: attribute value
     * 2: attribute's max value 
     */
    INVALID_VALUE_HIGHER_THAN_MAX("{0}: Giá trị không hợp lệ: {1} (> {2})"), 

    /***
     * 0: attribute name
     * 1: attribute value
     * 2: text format (if available)
     */
    INVALID_FORMAT_RAW_TEXT_VALUE("{0}: Giá trị chuỗi ký tự không hợp lệ: {1} (y/c format: {2})"),
    /**
     * 0: attribute name
     * 1: bounded attribute name
     */
    INVALID_TEXT_FIELD_BOUNDED_AND_EDITABLE("{0}: Trường dữ liệu gắn nguồn (bounded: {1}) lại có thể sửa được"),
    
    /**
     * 0: the object,  
     * 1: the rule that is violated (e.g. "value in [1,2,3]") */
    OBJECT_STATE_VIOLATES_RULE("Trạng thái đối tượng ({0}) không hợp lệ đối với luật: {1}"), 
    ;   
    
    private String text;
    
    private Code(String text) {
      this.text = text;
    }
    
    @Override
    public String getText() {
      return text;
    }

    /**The {@link MessageFormat} object for formatting {@link #text} using context-specific data arguments*/
    private MessageFormat messageFormat;
    
    @Override
    public MessageFormat getMessageFormat() {
      if (messageFormat == null) {
        messageFormat = new MessageFormat(text);
      }
      
      return messageFormat;
    }
  };
  
  public ConstraintViolationException(Code errCode, String msg, Object...args) {
    super(errCode, msg, args);
  }
  
  public ConstraintViolationException(Code errCode, Throwable t, String msg, Object...args) {
    super(errCode,msg,t,args);
  }
  
  // v2.7.3
  public ConstraintViolationException(Code errCode, Object...args) {
    super(errCode, args);
  }
  
  // v2.7.3
  public ConstraintViolationException(Code errCode, Throwable t, Object...args) {
    super(errCode,t,args);
  }
  
  // v3.3: used to pass a customised (domain-specific) error code 
  public ConstraintViolationException(InfoCode errCode, Throwable t, Object...args) {
    super(errCode,t,args);
  }
  
  // v3.3: used to pass a customised (domain-specific) error code 
  public ConstraintViolationException(InfoCode errCode, Object...args) {
    super(errCode,args);
  }

}
