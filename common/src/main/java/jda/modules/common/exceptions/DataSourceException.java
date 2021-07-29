package jda.modules.common.exceptions;

import java.text.MessageFormat;

/**
 * @overview An exception that is thrown when an error occured with database connection 
 *           or processing
 * 
 * @author dmle
 * 
 */
public class DataSourceException extends ApplicationException {
  
  public static enum Code implements InfoCode {
    /***
     * 0: data source URL/data source name
     * 1: cause message
     */
    FAIL_TO_CONNECT("Lỗi tạo kết nối đến nguồn dữ liệu: {0} ({1})"), //
    /**
     * 0: domain class name 
     */
    FAIL_TO_CREATE_CLASS_STORE("Lỗi tạo kho dữ liệu cho lớp: {0}"), //
    /**
     * 0: domain class
     * 1: domain object
     */
    FAIL_TO_INSERT_OBJECT("Lỗi thêm dòng dữ liệu cho lớp: {0} ({1})"), //
    /**
     * 0: class
     * 1: object (of class {0})
     * 2: new class (class to transform object {1} to)
     */
    FAIL_TO_TRANSFORM_OBJECT("Lỗi chuyển đổi dòng dữ liệu giữa các lớp mẹ-con: {0} ({1}) -> {2}"),
    /**
     * 0: query
     */
    FAIL_TO_EXECUTE_QUERY("Lỗi thực hiện truy vấn dữ liệu: {0}"), //
    /***
     * 0: query
     * 1: object
     * 2: (source) attrib index
     * 3: (source) attrib name
     * 4: attrib value
     */
    FAIL_TO_UPDATE_OBJECT_ATTRIB("Lỗi cập nhật dữ liệu bằng truy vấn: {0} (object: {1}, column ({2}:{3}), value({4}))"),
    
    /**
     * 0: query
     * 1: object
     */
    FAIL_TO_UPDATE_OBJECT_BY_QUERY("Lỗi cập nhật dữ liệu bằng truy vấn: {0} (dòng dữ liệu: {1})"), //
    /**
     * 0: class Name
     * 1: obj | query
     */
    FAIL_TO_DELETE_OBJECT("Lỗi xóa dữ liệu: {0} ({1})"), //
    /**
     * 0: class name 
     */
    FAIL_RESULT_SET("Lỗi xử lí kết quả dữ liệu: {0}"), //
    /**
     * 0: data source URL/data source name
     * 1: cause message  
     */
    FAIL_TO_READ_DATA_SOURCE("Lỗi đọc dữ liệu từ nguồn: {0} ({1})"), 
    /**
     * 0: class
     * 1: table(data store) spec 
     */
    INVALID_DATA_SOURCE_SPEC("Đặc tả dữ liệu nguồn của lớp miền không đúng: lớp: {0}; đặc tả: {1}"),
    /**
     * 0: class(es)
     */
    INVALID_ORDER_BY_CLASS("Ít nhất một trong các lớp miền cho sắp xếp kết quả dữ liệu đọc từ nguồn (order-by) không đúng: {0}"), 
//    /**
//     * 0: query
//     */
//    FAIL_TO_EXECUTE_STATEMENT("Lỗi thực hiện truy vấn dữ liệu: {0}"), 
    //FAIL_TO_CREATE_QUERY("Lỗi tạo truy vấn: {0} (query)"), //
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
  
  public DataSourceException(Code errCode, String msg, Object...args) {
    super(errCode, msg, args);
  }
  
  public DataSourceException(Code errCode, Throwable t, String msg, Object...args) {
    super(errCode,msg,t,args);
  }
  
  // v2.7.3
  public DataSourceException(Code errCode, Object...args) {
    super(errCode, args);
  }
  
  // v2.7.3
  public DataSourceException(Code errCode, Throwable t, Object...args) {
    super(errCode,t,args);
  }
}
