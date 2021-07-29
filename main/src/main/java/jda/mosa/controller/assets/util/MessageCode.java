/**
 * 
 */
package jda.mosa.controller.assets.util;

import java.text.MessageFormat;

import jda.modules.common.exceptions.InfoCode;

/**
 * Message dialog code constants. These codes are used to identify the
 * messages that are being displayed and hence are used by the system to
 * localise the message content.
 */
public enum MessageCode implements InfoCode {
  // confirmation
  CONFIRM_DATA_RESET("Nhập lại dữ liệu?"), 
  CONFIRM_CANCEL_NEW_OBJECT("Hủy tạo mới dữ liệu?"),
  CONFIRM_DELETE_OBJECT("Xóa dữ liệu này?"), 
  /**0: domain class
   * 1: data source type */
  CONFIRM_IMPORT_OBJECTS("Nhập dự liệu: {0} \nsử dụng loại kết nối: {1}?"),
  /**
   * 0: command
   * <br>1: program name
   */
  CONFIRM_TASK_RUN_SETUP("Chạy lệnh cài đặt: {0}, với cấu hình chương trình: {1}?"),
  /**
   * 0: number of objects
   */
  CONFIRM_COPY_OBJECTS("Chép {0} bản ghi dữ liệu?"),
  /**
   * 0: number of objects
   */
  CONFIRM_DELETE_OBJECTS("Xóa {0} bản ghi dữ liệu?"),
  
  CONFIRM_CREATE_NEW_OBJECT("Bạn có muốn tạo mới bằng dữ liệu trên form này không?"),
  
  /**
   * 0: domain class (label)
   */
  CONFIRM_CREATE_OBJECT("Bạn có muốn tạo mới dữ liệu cho {0} không?"),
  
  ////////////////////      INFORMATIONAL     ////////////////
  /**
   * 0: (parent) domain object/domain cls
   */
  PARENT_OBJECT_REQUIRED("Bạn cần có trước một đối tượng: {0}"),
  /** 0: (parent) domain class */
  PARENT_IS_BUSY("Không thể thực thi thao tác này khi form {0} đang bận"),
  /** 0: create new info (e.g. class name, [args...]) */
  OBJECT_CREATED("Tạo mới bản ghi dữ liệu: {0}"),
  /**
   * 0: domain object/ domain class label
   **/
  OBJECT_ADDED("Đã thêm bản ghi dữ liệu: {0}"), // v2.7.2
  //REPORT_OBJECT_CREATED(""), 
  /**0: domain object (updated)/domain class label*/
  OBJECT_UPDATED("Cập nhật bản ghi: {0}"), 
  /**0: domain object (updated)/domain class label*/
  OBJECT_DELETED("Xóa bản ghi: {0}"), 
  QUERY_REQUIRED("Bạn cần tạo một truy vấn"),
  /**
   * 0: name of stuff to print 
   * 
   * @version 3.2c
   */
  OBJECT_PRINT_STARTED("Đang in {0}..."), 
  /**
   * 0: name of stuff that prints 
   * 
   * @version 3.2c
   */
  OBJECT_PRINT_COMPLETED("Đã gửi {0} đến máy in"), 
  ROW_SELECTION_REQUIRED("Bạn cần chọn ít nhất một bản ghi"), 
  /** 0: domain class
   * 1: parent obj */
  NO_CHILD_OBJECTS_FOUND("Không tìm thấy dữ liệu nào về {0} liên quan đến bản ghi {1} hiện tại"),
  /**
   * 0: domain class
   */
  NO_OBJECTS_FOUND("Không tìm thấy dữ liệu nào về: {0}"),
  /**
   * 0: domain class and/or object  
   * 1: function name
   */
  NO_OBJECTS_FOUND_FOR_FUNCTION("Không tìm thấy dữ liệu nào về {0}, thỏa mãn điều kiện: {1}"),
  NO_ACTIVE_CONTAINERS("Bạn chưa chọn form dữ liệu phù hợp"), 
  //SEARCH_NOT_ENABLED(""),
  /**
   * {0}: command
   * */
  SETUP_COMPLETED("Hoàn thành lệnh cài đặt: {0}"),
  /**
   * 0: domain class 
   */
  OBJECTS_IMPORTED("Đã nhập xong dữ liệu cho lớp: {0}"), 
  NO_OBJECTS_SELECTED("Bạn cần chọn ít nhất một bản ghi dữ liệu"),
  /**
   * 0: target object's class (label)
   */
  NO_TARGET_OBJECT_SELECTED("Bạn cần chọn một bản ghi đích: {0}"),
  
  ////////////////////      ERRORS     ////////////////
  /**@version 3.2c*/
  ERROR_NO_DOMAIN_USER("Không có tài khoản đăng nhập nào của người sử dụng"),
  /**
   * 0: module name
   */
  ERROR_INSUFFICIENT_PERMISSION_TO_RUN("Bạn không đủ quyền để chạy mô-đun: {0}"), 
  /**
   * 0: query
   */
  ERROR_NO_QUERY_RESULT("Không tìm thấy kết quả cho truy vấn: {0}"), 
  ERROR_NO_REPORT_RESULT("Không tìm thấy dữ liệu nào cho báo cáo"), //
  //ERROR_INVALID_FIELD_VALUE(""), 
  /**0: language */
  ERROR_NO_LABELS_FOUND("Không tìm thấy nhãn cho ngôn ngữ: {0}"),
  /**0: method name*/
  ERROR_PERFORM_METHOD("Lỗi thực hiện phương thức: {0}"),
  /**0: language */
  ERROR_NO_LANGUAGE_CONFIGURATION_FOUND("Không tìm thấy cấu hình ngôn ngữ: {0}"), 
  /** 0: program node */
  ERROR_RUN_PROGRAM_NODE("Lỗi chạy nút chương trình {0}. Bạn có muốn thử lại không?"), 
  /**0: module name */
  ERROR_RUN_MODULE("Lỗi chạy mô-đun {0}"),
  /**0: module name*/
  ERROR_NO_MODULE_FOUND("Không tìm thấy mô-đun {0}"),
  /**0: command */
  ERROR_HANDLE_COMMAND("Lỗi xử lý lệnh chương trình {0}"), 
  //ERROR_HANDLE_COMMAND_SHUTDOWN(""),
  /** Lỗi chạy nhiệm vụ */
  //ERROR_RUNNING_TASK(""), 
  //ERROR_CHART_NOT_SUITABLE(""), 
  /**0: domain class */
  ERROR_ASSOCIATION_CONSTRAINT_VIOLATED_ON_NEW("Không thể tạo thêm dữ liệu mới cho: {0}"), 
  ERROR_UNDEFINED("Lỗi chương trình"),
  /**0: domain class */
  ERROR_OPEN_FORM("Lỗi mở form dữ liệu: {0}"),
  /**0: domain class */
  //ERROR_OPEN_METADATA("Không thể đọc thông tin dữ liệu của: {0}"), 
  ERROR_NO_SETUP_COMMAND("Cần lệnh cài đặt nhưng không nhập"),
  ERROR_NO_ACTIVE_DATA_CONTAINER("Không có form dữ liệu nào được chọn"),
  /**
   * 0: label
   */
  ERROR_UPDATE_LABEL("Không thể cập nhật nhãn dữ liệu: {0}"),
  /**
   * 0: lang
   * 1: region
   * 2: label
   */
  ERROR_NO_MATCHING_REGION_LABEL_FOUND("Không tìm thấy nhãn dữ liệu của ngôn ngữ {0} cho vùng: {1} (nhãn = {2})"),
  ////////////////////      WARNINGS     ////////////////
  /**0: associated domain class label*/
  WARN_ASSOCIATION_CONSTRAINT_VIOLATED_ON_DELETE("Xóa bản ghi này sẽ làm thiếu dữ liệu của bản ghi {0} hiện tại?"), 
  WARN_CLOSE_SEARCH_RESULT("Xóa kết quả tìm kiếm hiện tại?"),

  /// SYSTEM LOG messages
  /**
   * 0: Main controller (i.e. program) name
   */
  SHUTTING_DOWN_PROGRAM("{0} đang đóng chương trình..."), 
  /**
   * 0: Main controller (i.e. program) name
   */
  CLOSING_MODULES("{0} đang đóng các mô-đun..."),
  /**
   * 0: status value
   */
  STATUS_LINE("...{0}"),  
  ////////////////////      NOT YET DEFINED     ////////////////
  /**
   *  Any message (specified by the programmer) 
   */
  UNDEFINED(""), 
  ;

  private String text;

  private MessageCode(String text) {
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
} /**end {@link MessageCode}*/