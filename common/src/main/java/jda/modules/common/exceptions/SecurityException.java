package jda.modules.common.exceptions;

import java.text.MessageFormat;

public class SecurityException extends ApplicationRuntimeException {
  
  public static enum Code implements InfoCode {
    /**
     * 0: where (e.g. security controller)
     * 1: login name
     */
      FAILED_TO_AUTHENTICATE("Không thể thực hiện xác thực: {0}.login({1})"), //
      USER_NOT_VALID("Tên, mật khẩu không đúng. Vui lòng thử lại."), //
      ROLE_NOT_VALID("Vai trò không khớp với tài khoản"), //
      /**
       * 0: object (e.g class, module) which user does not have permission to access 
       */
      INSUFFICIENT_PERMISSION("Không đủ quyền: {0}"), // 
      NOT_LOGGED_IN("Bảo mật không được kích hoạt hoặc chưa đăng nhập hệ thống"), //
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
  
  public SecurityException(Code errCode, String msg, Object...args) {
    super(errCode, msg, args);
  }

  /**
   * @version 3.1
   */
  public SecurityException(Code errCode, Object...args) {
    super(errCode, args);
  }
  
  public SecurityException(Code errCode, Throwable t, String msg, Object...args) {
    super(errCode,msg,t,args);
  }
}
