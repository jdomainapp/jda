package jda.modules.common.exceptions;

import java.text.MessageFormat;

/**
 * OVERVIEW: a run-time exception that is (not expected to be) thrown 
 * by a method that could not perform its operation due to lack of support
 * 
 * @author dmle
 *
 */
public class NotImplementedException extends ApplicationRuntimeException {
  
  public static enum Code implements InfoCode {
    /**Không hỗ trở phương thức {0}.{1} (<i>class_name</i>.<i>method_name</i>)*/
    METHOD_NOT_IMPLEMENTED("Không hỗ trở phương thức {0}.{1}"), //
    /**Tính năng không được hỗ trợ: {0} */
    FEATURE_NOT_SUPPORTED("Tính năng không được hỗ trợ: {0}"), 
    /**"Không hỗ trợ kiểu dữ liệu: {0}", type*/
    DATA_TYPE_NOT_SUPPORTED("Không hỗ trợ kiểu dữ liệu: {0}"), // 
    MODULE_DEPENDENCY_GRAPH_NOT_SUPPORTED(""),  //
    /**
     * 0: id
     **/
    OBJECT_ID_NOT_SUPPORTED("Không hỗ trợ mã đối tượng: {0}"), 
    /**
     * 0: data source type
     */
    OSM_Type_Not_Supported("Không hỗ trợ loại lưu trữ dữ liệu: {0}"), 
    //
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
  
  // v2.7.3
  public NotImplementedException(Code errCode, Object...args) {
    super(errCode, args);
  }
  
  // v2.7.3
  public NotImplementedException(Code errCode, Throwable t, Object...args) {
    super(errCode,t,args);
  }
  
  public NotImplementedException(String msg, Object...args) {
    super(Code.FEATURE_NOT_SUPPORTED, msg, args);
  }
  
  public NotImplementedException(Code errCode, String msg, Object...args) {
    super(errCode, msg, args);
  }
  
  public NotImplementedException(Code errCode, Throwable t, String msg, Object...args) {
    super(errCode,msg,t,args);
  }
}
