package jda.modules.common.exceptions;

import java.text.MessageFormat;

/**
 * OVERVIEW: a run-time exception that is (not expected to be) thrown 
 * by a method that could not perform its operation due to input 
 * data errors.
 * 
 * @author dmle
 *
 */
public class NotPossibleException extends ApplicationRuntimeException {
  public static enum Code implements InfoCode {
    /**
     * 0: class
     */
    CLASS_NOT_WELL_FORMED("Lớp không được định nghĩa đúng: {0}"), //
    /**
     * 0: region
     */
    CONFIGURATION_NOT_WELL_FORMED("Lỗi cấu hình của vùng: {0}"), //
    CONFIGURATION_VALUE_ERROR(""), //
    /**Chưa đăng ký cấu hình chương trình*/
    CONFIGURATION_SCHEMA_NOT_REGISTERED("Chưa đăng ký cấu hình chương trình"),
    FAIL_TO_PERFORM_REPORT(""), //
    /** 
     * 0: class/object
     * 1: method name
     * 2: arg(s) (comma-separated)
     */
    FAIL_TO_PERFORM_METHOD("Không thể thực hiện phương thức {0}.{1}({2})"), //
    /**
     * 0: cause/subject of the method
     */
    FAIL_TO_PERFORM_DB("Lỗi thao tác trên nguồn dữ liệu: {0}"), //
    FAIL_TO_CONNECT_DB("Không thể kết nối tới nguồn dữ liệu"), //
    /**
     * 1: methodName
     * 2: args 
     * 3: return or reason
     */
    FAIL_TO_PERFORM("Không thể thực thi thao tác: {0}({1}) {2}"), //
    FAIL_TO_INITIALISE_RUN_TREE(""), //
    /**when the state of a report instance does not allow an operation to be performed
     * on it*/
    INVALID_REPORT_STATE(""), //
    /**the specified language is not supported by the JVM*/
    LANGUAGE_NOT_SUPPORTED(""), //
    /**
     * 0: cls*/
    CLASS_NOT_REGISTERED("Lớp chưa được đăng ký trong hệ thống: {0}"),  
    /**the specified attribute (field) is not defined correctly*/
    FIELD_NOT_WELL_FORMED("Thuộc tính không được định nghĩa đúng: {0}"),  
    /**
     * 0: table name
     * 1: column name
     * 2: column number
     * 3: data type
     * */
    FAIL_TO_READ_TABLE_COLUMN_VALUE("Lỗi đọc dữ liệu cột: {0}.{1} (sốTT: {2}, kiểu {3})"), 
    /** bound attributes expected but not found*/
    NO_BOUND_ATTRIBUTES("Không tìm thấy thuộc tính ràng buộc nào."),
    /** 0: argument (list)*/
    INVALID_ARGUMENT("Dữ liệu đầu vào không đúng: {0}"),
    /**
     * 0: actual type
     * 1: expected type
     */
    INVALID_RETURN_TYPE("Loại dữ liệu đầu ra không đúng: {0} (y/c: {1})"),
    /**
     * 0: MCC name
     * 1: reason for the not-wellformedness
     */
    MODULE_NOT_WELL_FORMED("{0}: MCC không được định nghĩa đúng: {1}"),  
    /**
     * 0: type_name 
     * 1: args
     */
    FAIL_TO_CREATE_OBJECT("Không thể tạo đối tượng lớp: {0}({1})"),      
    /**
     * no args required
     */
    FAIL_TO_POST_CREATE_OBJECT("Lỗi tiến hành nhiệm vụ sau tạo đối tượng"),
    /**
     * no args required
     */
    FAIL_TO_POST_UPDATE_OBJECT("Lỗi tiến hành nhiệm vụ sau cập nhật đối tượng"),
    /**
     * no args required
     */
    FAIL_TO_POST_DELETE_OBJECT("Lỗi tiến hành nhiệm vụ sau khi xóa đối tượng"),
    /**
     * 0: class name/object
     * 1: attribute
     * 2: value
     */
    FAIL_TO_UPDATE_OBJECT_ATTRIBUTE("Không thể cập nhật giá trị thuộc tính của đối tượng: {0}.{1} = {2}"),
    /** fail to create a program module */
    FAIL_TO_CREATE_MODULE(""),  
    /**Không thể tạo trình điểu khiển {0}*/
    FAIL_TO_CREATE_CONTROLLER("Không thể tạo trình điểu khiển {0}"), 
    /** data field does not support value formatting */
    /**
     *  0: data field 
     *  1: value
     *  2: format mask string
     */
    CANNOT_FORMAT_VALUE("Không thể format giá trị ở trường dữ liệu: {0} = {1} (mask: {2})"),  
    /** Invalid value used in the source data table of a chart */
    INVALID_CHART_DATA(""), 
    /** source table has no data for chart */
    NO_CHART_DATA(""), 
    /**
     * 0: parent domain class (label if available)
     * */
    NO_PARENT_OBJECT("Cần một đối tượng {0}, nhưng không có"),  
    NO_ACTIVE_DATA_CONTAINER("Không có giao diện mô đun nào được chọn"),
    NO_SERVER_PROTOCOL("Giao thức dữ liệu máy chủ không được định nghĩa"),
    /**
     * 0 : language
     * 1 : super class name
     */
    NO_LANGUAGE_AWARE_SUBTYPE_DEFINED("Không tìm thấy lớp con theo ngôn ngữ: {0}, của lớp: {1}"),
    /** source table does not have enough columns for chat*/
    NOT_ENOUGH_CHART_DATA_COLUMNS(""),  
    /** chart area is not suitable for chat */
    INVALID_CHART_DATA_AREA(""),  
    /** asociated object is not of the correct type */
    INVALID_TARGET_ASSOCIATE_OBJECT_TYPE(""),  
    INVALID_EDITING_ROW_BUFFER(""),  //
    /**No bounded data source
     * 0: class
     * 1: method
     * 2: args
     * */
    DATA_SOURCE_NOT_BOUNDED("Không thể thực hiện phương thức {0}.{1}({2}): Không có liên kết tới nguồn dữ liệu"), 
    /** data source is empty */
    DATA_SOURCE_IS_EMPTY("Nguồn không có dữ liệu"),  
    DATA_SOURCE_NOT_CONNECTED("Không có kết nối tới nguồn dữ liệu"),
    //
    /**
     * 0: class
     * 1: object, attribute or "-" if not applicable
     * 2: val
     */
    INVALID_OBJECT_ID_TYPE("Mã đối tượng không hợp lệ {0}<{1}>:{2} (cần kiều Comparable)"), //
    /**
     * 0: child object
     * 1: parent class name
     */
    INVALID_PARENT_OBJECT("Dữ liệu con ({0}) không chứa đúng dữ liệu mẹ ({1})"),
    
    /**Không thể mở dòng dữ liệu đầu tiên*/
    FAIL_TO_MOVE_PREVIOUS("Không thể mở dòng dữ liệu đầu tiên"), 
    /**Không thể mở dòng dữ liệu liền sau*/
    FAIL_TO_MOVE_NEXT("Không thể mở dòng dữ liệu liền sau"), 
    /**Không thể mở dòng dữ liệu liền trước*/
    FAIL_TO_MOVE_FIRST("Không thể mở dòng dữ liệu liền trước"),
    /**Không thể xem dữ liệu*/
    FAIL_TO_BROWSE_ALL("Không thể duyệt toàn bộ dữ liệu"), 
    /**Lỗi mởi dữ liệu {0}*/
    FAIL_TO_OPEN_OBJECT_FORM("Lỗi mởi dữ liệu {0}"),  
    /**Mô-đun không được cấu hình với các mô-đun con: {0}*/
    MODULE_HAS_NO_CHILDREN("Mô-đun không được cấu hình với các mô-đun con: {0}"),  
    /**Tên file không hợp lệ: {0}*/
    FILE_NAME_NOT_WELL_FORMED("Tên file không hợp lệ: {0}"),  
    /**Loại ảnh không hợp lệ: {0}*/
    INVALID_IMAGE_TYPE("Loại ảnh không hợp lệ: {0}"),
    /**0: invalid specification element*/
    INVALID_OSM_PROTOCOL_SPECIFICATION("Đặc tả giao thức nguồn dữ liệu không hợp lệ: {0}"),
    INVALID_PORT("Cổng ứng dụng không hợp lệ: {0}"), 
    /**Không thể tạo ảnh từ: {0}*/
    FAIL_TO_CONVERT_IMAGE("Không thể tạo ảnh từ: {0}"),  
    /**Không tìm thấy mô tả cấu hình mô-đun của lớp {0}*/
    MODULE_DESCRIPTOR_NOT_DEFINED("Không tìm thấy mô tả cấu hình mô-đun của lớp: {0}"),  
    /**Không thể tạo nội dung văn bản {0} cho {1}*/
    FAIL_TO_CREATE_DOCUMENT("Không thể tạo nội dung văn bản {0} cho {1}"),  
    /**Lỗi đọc phông chữ {0} từ tệp {1}*/
    FAIL_TO_LOAD_FONT("Lỗi đọc phông chữ {0} từ tệp {1}"),  
    /**Không thể ghi ra tệp {0}*/
    FAIL_TO_WRITE_TO_FILE("Không thể ghi ra tệp {0}"),  
    /**Lỗi cài đặt chương trình sử dụng tham số {0}*/
    FAIL_TO_SET_UP("Lỗi cài đặt chương trình sử dụng tham số {0}"),  
    /**Lỗi lớp dữ liệu đầu vào*/
    INVALID_INPUT_CLASSES_ARGUMENT("Lỗi lớp dữ liệu đầu vào: {0}"), 
    NO_INPUT_MODULE("Không có mô-đun đầu vào"),
    /**
     * 0: folder
     */
    FAIL_TO_CREATE_FOLDER("Không thể tạo thư mục (folder): {0}"),
    /**
     * 0: file
     */
    FAIL_TO_CREATE_FILE("Không thể tạo tệp (file): {0}"),
    /**
     * 0: source
     * 1: target
     */
    FAIL_TO_COPY_FILE("Không thể copy tệp (file): {0} -> {1}"),
    /**
     * 0: source
     * 1: target
     */
    FAIL_TO_COPY_DIR("Không thể copy thư mục (directory): {0} -> {1}"),
    FAIL_TO_SAVE_GUI_CONFIG("Không thể lưu cấu hình giao diện: {0}"), 
    DATA_DOCUMENT_EMPTY("Văn bản không có nội dung"), 
    /**
     * {0}: url*/
    FAIL_TO_DISPLAY_URL("Không thể hiển thị nội dung đường dẫn (URL): {0}"), 
    FAIL_TO_START_DB_SERVER("Không thể chạy tiến trình máy chủ dữ liệu"), 
    FAIL_TO_USE_PORT("Cổng ứng dụng đang bị sử dụng: {0}"), 
    NO_DOCUMENT_TEMPLATE("Mẫu văn bản xuất không được định nghĩa"), 
    /**0: filter class name*/
    FAIL_TO_CREATE_REPORT_OUTPUT_FILTER("Không thể tạo bộ lọc báo cáo: {0}"),
    /**
     * 0: function class
     * 1: method name
     * */
    DATA_FUNCTION_NOT_WELL_DEFINED("Hàm dữ liệu định nghĩa không đúng: {0}.{1}"),
    /**
     * 0: form name
     */
    NO_LAYOUT_BUILDER("Không có bộ làm layout cho form: {0}"),
    /**
     * 0: component
     */
    INVALID_LAYOUT_COMPONENT("Thành phần giao diện không hợp lệ: {0}"), NO_PROPERTIES("Không có cấu hình chương trình"),
    /**0: property name*/
    NO_PROPERTY("Không tìm thấy cấu hình chương trình: {0}"),
    /**0: property name*/
    NO_PROPERTY_VALUE("Không có giá trị của cấu hình chương trình: {0}"),
    NO_INPUT_ARGUMENTS("Không có tham số đầu vào chương trình"), 
    NO_CONFIGURATION("Cần cấu hình chương trình nhưng không có"),
    /**
     * 0: details
     * @version 3.3
     */
    NO_INIT_CONFIGURATION("Cần cấu hình khởi tạo chương trình nhưng không có ({0})"),    
    /**
     * 0: property file
     */
    FAIL_TO_LOAD_PROPERTIES("Lỗi đọc cấu hình chương trình từ file: {0}"), 
    /**
     * 0: command
     */
    FAIL_TO_PERFORM_COMMAND("Lỗi chạy được lệnh chương trình: {0}"), 
    /**
     * 0: file name
     */    
    FAIL_TO_READ_FILE("Lỗi đọc tệp: {0}"),
    /**
     * 0: class name or object spec.
     */    
    FAIL_TO_READ_OBJECT("Lỗi đọc đối tượng lớp: {0}"),
    /**
     * 0: object
     */    
    FAIL_TO_WRITE_OBJECT("Lỗi ghi đối tượng: {0}"), 
    /**
     * 0: user login 
     */
    FAIL_TO_LOAD_USER_CONFIGURATION("Lỗi đọc cấu hình người sử dụng: {0}"),
    /**
     * 0: class
     */    
    INVALID_ARGUMENT_SETUP_CLASS("Lớp cài đặt đầu vào không đúng: {0}"),
    /**
     * 0: index
     * 1: last array index
     */    
    INVALID_ARRAY_INDEX("Chỉ mục mảng không hợp lệ: {0} (kỳ vọng: [0,{1}])"),
    /***
     * 0: domain class
     * 1: attribute name (of domain class)
     * 2: referenced type (of attribute)
     */
    INVALID_REFERENCED_DOMAIN_TYPE("Lớp tham chiếu của thuộc tính không hợp lệ: {0}.{1} -> {2}"), 
    FAIL_TO_CONVERT_TREE_TO_XML("Lỗi chuyển cây dữ liệu thành XML"),
    /**
     * 0: reason
     */
    FAIL_TO_CONVERT_TREE_FROM_XML("Lỗi tạo cây dữ liệu từ XML (lí do: {0})"), 
    /**
     * 0: reason
     */
    FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC("Lỗi tạo cây chứa cấu hình mô-đun (containment tree) (lí do: {0})"),
    /**
     * 0: attribute name 
     */
    NO_FORMAT_STRING("Không có format cho trường dữ liệu: {0}"), 
    /**
     * 0: attribute name
     * 1: format string 
     */
    INVALID_FORMAT_STRING("Format trường dữ liệu ({0}) không hợp lệ: {1}"),
    /**
     * 0: the program component for which the command is to be used 
     * 1: command name
     */
    NO_COMMAND("{0}: Không có lệnh chương trình: {1}"),
    /**
     * 0: class name
     * 1: attribute name
     */
    INVALID_SOURCE_ATTRIBUTE_TYPED("Loại dữ liệu của trường dữ liệu không hợp lệ: {0}.{1}"), 
    /**
     * 0: browser
     * 1: current size
     * 2: min size
     */
    INVALID_BROWSER_STATE_FOR_SORTING("Trạng thái trình duyệt đối tượng (object browser) không phù hợp cho sắp xếp: {0} có {1} đối tượng (y/c >= {2})"),
    /**
     * 0: actual config
     * 1: expected config class
     */
    INVALID_DATA_FIELD_CONFIG("Loại cấu hình giao diện trường dữ liệu không đúng: {0} (y/c: {1})"),
    /**
     * 0: class
     * 1: attribute
     */
    INVALID_BOUND_ATTRIBUTE("Trường dữ liệu liên quan (bound attribute) không phù hợp: {0}.{1}"),
    /**
     * 0: class
     * 1: derived attribute
     * 2: source attribute
     */
    INVALID_DERIVED_SOURCE_ATTRIBUTE("Thuộc tính gốc (source attribute) của thuộc tính phụ thuộc (derived attribute) không phù hợp: {0}.{1} -> {2} (gốc)"),
    /**
     * 0: class
     * 1: derived attribute
     * 2: source attribute(s)
     */
    INVALID_DERIVED_ATTRIBUTE("Thuộc tính phụ thuộc (derived attribute) không được định nghĩa đúng: {0}.{1} -> {2} (gốc)"),
    /**
     * 0: class
     * 1: attribute name
     * 2: attribute type
     */
    INVALID_ATTRIBUTE_TYPE("Kiểu thuộc tính không được định nghĩa đúng: {0}.{1}: {2}"),    
    /**
     * 0: class
     * 1: key attribute
     */
    INVALID_CLASS_NOT_THE_OWNER_OF_ID_ATTRIBUTE("Thuộc tính loại khóa không được chứa trong lớp như yêu cầu: lớp ({0}), thuôc tính ({1})"),
    /**
     * 0: class name 
     * 1: expected comparable class
     */
    CLASS_NOT_COMPARABLE("Lớp dữ liệu không phải dạng so sánh được: {0} (y/c: {1})"), 
//    /**
//     * 0: browser class name 
//     * 1: expected browser class name
//     */
//    BROWSER_NOT_SORTABLE("Loại trình duyệt đối tượng không hỗ trợ sắp xếp: {0} (y/c: {1})"),
    /**
     * 0: data container
     */
    DATA_CONTAINER_NOT_SORTABLE("Form đối tượng không hỗ trợ sắp xếp: {0}"), 
    /**
     * 0: object form (data container)
     */
    NO_TARGET_OBJECT_FORM_SPECIFIED("Không tìm thấy cấu hình form đích của: {0}"), 
    /**
     * 0: a domain object to which associate objects are to be found  (e.g. Student)
     * 1: domain class label of the associate objects (e.g. Enrolment)
     * */
    NO_ASSOCIATED_OBJECTS("Không tìm thấy dữ liệu nào về {1} trong quan hệ với: {0}"), 
    /**
     * 0: data controller (the data controller that owns the data container's GUI)
     */
    FAIL_TO_WAIT_FOR_UPDATE_GUI("Không thể cập nhật giao diện vì tiến trình cập nhật đang chạy: \n {0}"),
    /**
     * 0: filter class
     * */
    FAIL_TO_FILTER_OBJECTS("Không thể lọc dữ liệu (nguồn: {0})"), 
    /**
     * 0: child data controller
     * 1: parent data controller
     */
    NO_PARENT_QUERY_WHEN_REQUIRED("Không có kết nối truy vấn dữ liệu giữa hai form: {0} -> {1}"),
    /**
     * 0: domain attribute
     */
    NO_OBJECT_QUERY_DESCRIPTOR("Không tìm thấy định nghĩa truy vấn dữ liệu nào của thuộc tính: {0}"),
    /**
     * 0: domain attribute
     */
    NO_OBJECT_QUERY_HANDLER_FUNCTION("Không tìm thấy phương thức truy vấn dữ liệu của thuộc tính: {0}"),
    /**
     * 0: data controller
     **/
    DATA_CONTROLLER_NOT_A_CHILD("Cần một điều khiển con nhưng không đúng: {0}"),
    /**
     * 0: application module
     */
    FAIL_TO_EXPORT_DOCUMENT("Không thể xuất dữ liệu cho mô-đun: {0}"),
    /**
     * 0: task name
     */
    FAIL_TO_WAIT_FOR_TASK("Tiến trình chạy quá lâu không có phản hồi: {0}"),
    /***
     * 0: class
     * 1: attribute
     * 2: attribute value
     */
    FAIL_TO_PARSE_ATTRIBUTE_VALUE("Không thể xử lý giá trị thuộc tính: {0}.{1} = {2}"),
    /**
     * 0: class 
     */
    INVALID_DOM_TYPE("Loại quản lý dữ liệu (DOM) không phù hợp: {0}"),
    /**
     * 0: object class
     * 1: details 
     */
    NULL_POINTER_EXCEPTION("Đối tượng {0} chưa được gán ({1})"),
    /**
     * 0: domain class label (data name)
     */
    FAIL_TO_PRINT("Lỗi in dữ liệu: {0}"),
    /**@version 3.2*/
    NO_PRINT_DOCUMENT("Không có văn bản nào để in. Bạn đã xuất chưa?"),
    /***
     * 0: domain class
     */
    NO_DOMAIN_ATTRIBUTES_IN_DOMAIN_CLASS("Không có thuộc tính nào được định nghĩa trong lớp miền: {0}"),
    /**
     * 0: class name 
     */
    FAIL_TO_COMPILE_CLASS("Lỗi khi biên dịch lớp: {0}"),
    /**
     * 0: domain class/view of the module
     * @version 3.2c
     */
    NO_MODULE_DEFINED("Không tìm thấy cấu hình mô-đun: {0}"),
    /**
     * 0: module
     * @version 3.2c
     */
    NO_MODULE_HELP_FILE("Không tìm thấy tệp trợ giúp của mô-đun: {0}"),
    /**0: method name 
     * @version 3.3
     */
    FAIL_TO_GENERATE_METHOD("Lỗi khi tạo phương thức: {0}"),
    /**
     * 0: class name, 
     * 1: method name
     * @version 3.3
     */
    FAIL_TO_ADD_METHOD_TO_CLASS("Lỗi khi thêm phương thức vào lớp: {0}.{1}"),
    /**
     * 0: class name
     */
    FAIL_TO_WRITE_TO_CLASS("Lỗi ghi thay đổi vào file .java của lớp: {0}"),
    /**
     * 0: class name
     */
    FAIL_TO_LOAD_TO_CLASS("Lỗi tải lớp: {0}"),
    /**
     * 0: directory name (path)
     */
    FAIL_TO_CREATE_DIRECTORY("Lỗi tạo thư mục: {0}"),
    /**
     * 0: class name
     * 1: field name
     */
    FAIL_TO_GENERATE_FIELD("Lỗi tạo trường: {0}{1}"),
    /**
     * 0: script content
     * 1: input (if any)
     * */
    FAIL_TO_EXECUTE_EMBEDDED_SCRIPT("Lỗi thực thi mã (script) nhúng trong văn bản: {0} (input: {1})"),
    /**
     * @version 3.4
     * 0: cause
     */
    FAIL_TO_PARSE_SOURCE_CODE("Lỗi phân tích (parse) mã nguồn: {0}"),
    /**
     * 0: node val
     * 1: parent node
     * 
     * @version 5.2
     */
    FAIL_TO_ADD_NODE_TO_TREE("Lỗi thêm nút con ({0}) vào cây tại nút mẹ {1}."),
    /**
     * 0: service module
     * 1: client module
     * @version 5.2
     */
    NO_VIEW_CLIENT_SERVICE_REGION_SPECIFIED("Mô-đun dịch vụ không định nghĩa tên vùng giao diện của mô-đun khách hàng cho hiển thị kết quả dịch vụ : mô-đun dịch vụ: {0}, mô-đun khách hàng {1}"),
    /**
     * 0: kiểu thực
     * 1: kiểu kỳ vọng
     * @version 5.2
     */
    INVALID_REGION_COMPONENT("Thành phần GUI của miền không hợp lệ: kiểu {0}, cần kiểu {1}"),
    /**
     * 0: service module name (from which the client is requested) 
     * 
     * @version 5.2 
     */
    NO_CLIENT_MODULE_SPECIFIED("Mô-đun dịch vụ ({0}): mô-đun tớ cần có nhưng không có"),
    /**
     * 0: GUI region name
     * @version 5.2
     */
    NO_VIEW_BUILDER_SPECIFIED_WHEN_REQUIRED("Cần ViewBuilder cho ({0}) nhưng không có"),
    /**
     * 0: OCL constraint 
     * @version 20190321
     */
    FAIL_TO_CREATE_OCL_CONSTRAINT("Không thể tạo điều kiện ràng buộc OCL: {0}"),
    /**
     * 0: value
     * 1: target type
     */
    FAIL_TO_CONVERT_VALUE("Không thể convert giá trị \"{0}\" sang kiểu: {1} (kiểu không đúng hoặc không được hỗ trợ)"),
    /**
     * 0: reason
     * @version 5.4
     */
    FAIL_TO_SAVE_FILE("Không thể lưu file: {0}"),
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
  /**
   * @version
   * - 3.4.c: changed Code to InfoCode
   */
  public NotPossibleException(InfoCode errCode, Object...args) {
    super(errCode, args);
  }
  
  // v2.7.3
  /**
   * @version
   * - 3.4.c: changed Code to InfoCode
   */
  public NotPossibleException(InfoCode errCode, Throwable t, Object...args) {
    super(errCode,t,args);
  }
  
  /**
   * @version
   * - 3.4.c: changed Code to InfoCode
   */
  public NotPossibleException(InfoCode errCode, String msg, Object...args) {
    super(errCode, msg, args);
  }
  
  /**
   * @version
   * - 3.4.c: changed Code to InfoCode
   */
  public NotPossibleException(InfoCode errCode, Throwable t, String msg, Object...args) {
    super(errCode,msg,t,args);
  }
}
