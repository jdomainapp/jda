package jda.modules.common.exceptions;

import java.text.MessageFormat;

public class NotFoundException extends ApplicationRuntimeException {
  
  public static enum Code implements InfoCode {
    /**
     * 0: class 
     * <br>1: input args
     */
    OBJECT_NOT_FOUND("Không tìm thấy đối tượng {0}<{1}>"),  //
    /**
     * 0: (associate) class
     * 1: id attribute(s)
     * 2: id attribue value(s)
     */
    OBJECT_ASSOCIATE_NOT_FOUND("Không tìm thấy đối tượng liên kết (associate): {0}.{1}={2}"), 
    /**
     *  0: child region
     *  1: parent region 
     */
    CHILD_REGION_NOT_FOUND("Không tìm thấy vùng con {0} của vùng {1}"), //
    /**
     * 0: the source region 
     */
    REFERENCE_SETTINGS_NOT_FOUND("Không tìm thấy cấu hình tham khảo của vùng: {0}"), //
    SETTINGS_NOT_FOUND(""), //
    STYLE_SETTINGS_NOT_FOUND(""), //
    /** 0: class name*/
    CLASS_NOT_FOUND("Không tìm thấy lớp: {0}"), //
    /**
     *  0: class, 
     *  1: method 
     */
    METHOD_NOT_FOUND("Không tìm thấy phương thức: {0}.{1}"), //
    METHOD_WITH_RETURN_TYPE_NOT_FOUND(""), //
    /**
     * 0: class; 
     * 1: attribute (to which the method is bound) OR name prefix;
     * 2: method's Opt.type name; 
     * 3: arguments/params;
     * */
    METHOD_ANNOTATED_NOT_FOUND("Không tìm thấy phương thức {0}.{1}@{2}({3})"), //
    /**0 : file name*/
    FILE_NOT_FOUND("Không tìm thấy tệp: {0}"), //
    /**0: folder name*/
    FOLDER_NOT_FOUND("Không tìm thấy thư mục: {0}"), //
    /**
     * 0: class
     * 1: parameter list (type-value list OR type list)
     */
    CONSTRUCTOR_METHOD_NOT_FOUND("Không tìm thấy phương thức khởi tạo: {0}({1})"), //
    /**
     * 0: class name
     */
    ATTRIBUTES_NOT_FOUND("Không tìm thấy thuộc tính dữ liệu nào của lớp: {0}"), // 
    /**
     * 0: attribute name
     * 1: class name
     */
    ATTRIBUTE_NOT_FOUND("Không tìm thấy thuộc tính {0} của lớp {1}"),  
    /**
     * 0: attribute name
     * 1: class name
     */
    ATTRIBUTE_ID_NOT_FOUND("Không tìm thấy thuộc tính ID: {0}, của lớp {1}"), //
    /**
     * 0: class
     * 1: attribute
     */
    ATTRIBUTE_VALUES_NOT_FOUND("Không tìm thấy dữ liệu nguồn nào của trường: {0}.{1}"), 
    LINK_ATTRIBUTE_NOT_FOUND(""), //
    /**
     * 0: class
     */
    CONSTRAINT_NOT_FOUND("Không tìm thấy ràng buộc của lớp: {0}"), //
    /**0: property name*/
    PROPERTY_NOT_FOUND("Không tìm thấy thuộc tính : {0}"), // 
    /**
     * 0: assocName, 
     * 1: assocType, 
     * 2: owner domain class (of the association)
     */
    ASSOCIATION_NOT_FOUND("Không tìm thấy quan hệ {0} ({1}) trong lớp {2}"), //
    /**
     * 0: domain class
     * */
    CONTROLLER_NOT_FOUND("Không tìm thấy điều khiển của: {0}"), 
    /**
     * 0: domain class of the controller
     */
    DATA_CONTROLLER_NOT_FOUND("Không tìm thấy điều khiển dữ liệu của: {0}"), //    
    /***
     * 0: child container's domain class <br>
     * 1: parent container
     */
    CHILD_DATA_CONTAINER_NOT_FOUND("Không tìm thấy hộp giao diện: loại {0}, nằm trong {1}"), //
    LABELS_NOT_FOUND("Không tìm thấy cấu hình nhãn nào của chương trình"),  
    /**
     * 0: label id
     */
    LABEL_NOT_FOUND("Không tìm thấy nhãn: {0}"),  
    /**
     * 0: module name
     */
    MODULE_LABEL_NOT_FOUND("Không tìm thấy nhãn cho mô-đun: {0}"), 
    /**
     * 0: class,
     * 1: id-attribute,
     * 2: attribute spec (e.g. type)
     */
    ID_CONSTRAINT_NOT_FOUND("Không tìm thấy ràng buộc dạng mã: {0}.{1}: {2}"),  
    /**Không tìm thấy mô-đun {0}*/
    MODULE_NOT_FOUND("Không tìm thấy mô-đun: {0}"), 
    /** could not find mnemonic for a menu */
    MNEMONIC_NOT_FOUND(""),  
    /** could not find a specified public static constant defined in a class */
    /**
     * 0: member
     * 1: enclosing class 
     */
    CONSTANT_NOT_FOUND("Không tìm thấy phần tử hằng {0} trong lớp {1}"),  
    UPDATE_METHOD_NOT_FOUND(""),  
    //
    /**Không tìm thấy mã đối tượng {0}<{1}>*/
    OBJECT_ID_NOT_FOUND("Không tìm thấy mã đối tượng {0}<{1}>"),  
    /**
     * 0: (associate) class
     * 1: id attribute(s)
     * 2: id attribue value(s)
     */
    OBJECT_ASSOCIATE_ID_NOT_FOUND("Không tìm thấy mã đối tượng liên kết (associate): {0}.{1}={2}"),
    OBJECT_FIRST_AFTER_NOT_FOUND(""),  OBJECT_FIRST_BEFORE_NOT_FOUND(""),
    /**
     * 0: class
     */
    OBJECT_ID_RANGE_NOT_FOUND("Không tìm thấy khoảng mã dữ liệu cho: {0}"),  
    RECORD_ID_NOT_FOUND(""),  OBJECT_ID_FIRST_BEFORE_NOT_FOUND(""),  
    OBJECT_ID_FIRST_AFTER_NOT_FOUND(""),  OBJECT_ID_PREV_NOT_FOUND(""), OBJECT_ID_NEXT_NOT_FOUND(""), // 
    /** used by composite controller: when an execution node is not found in the execution tree */
    EXECUTION_NODE_NOT_FOUND(""), 
    /** used to indicate some configuration item is not specified in the application's set-up*/
    CONFIGURATION_NOT_FOUND(""),  
    /**
     * 0: annotation type
     * 1: context class
     */
    ANNOTATION_NOT_FOUND("Không tìm thấy định nghĩa phụ chú: loại: {0}, lớp định nghĩa: {1}"),  
    /**Không tìm thấy mẫu (template): {0}*/
    TEMPLATE_NOT_FOUND("Không tìm thấy mẫu (template): {0}"),  
    /**"Không tìm thấy dòng có đầy đủ cột"*/
    FIRST_FULL_ROW_NOT_FOUND("Không tìm thấy dòng có đầy đủ cột"), 
    /**
     * 0: reference class name (e.g. Student)
     * 1: reference class id attribute name (e.g. Student.id)
     * 2: reference class id attribute value (e.g. 1)
     * 3: class name of this object (e.g. City)
     * 4: constructor args used to create this object (e.g. [1,"hn",1])
     * 5: link-attribute name of this object.class to the referenc class (e.g. City.student)
     * */
    REFERENCE_OBJECT_NOT_FOUND ("Không tìm thấy dữ liệu liên quan: {0}.{1}[{2}] (tại dòng dữ liệu: {3}({4}).{5})"), 
    /**
     * 0: reference class name (e.g. Student)
     * 1: reference class id attribute value (e.g. 1)
     * 2: class name of this object (e.g. City)
     * 3: link-attribute name of this object.class to the referenc class (e.g. City.student)
     * */
    REFERENCE_REFLEXIVE_OBJECT_NOT_FOUND("Không tìm thấy dữ liệu liên quan: {0}[{1}] (tại dòng dữ liệu: {3}.{4})"),
    /**
     * 0: module descriptor class
     */
    MODULE_DESCRIPTOR_NOT_FOUND("Không tìm thấy cấu hình mô-đun trong lớp: {0}"), 
    /**
     * 0: system descriptor class
     */
    SYSTEM_DESCRIPTOR_NOT_FOUND("Không tìm thấy cấu hình phần mềm trong lớp: {0}"), 
    /**
     * 0: class name, 
     * 1: attribute name
     */
    ATTRIBUTE_PRINT_CONFIG_NOT_FOUND("Không tìm thấy cấu hình xuất của thuộc tính: {0}.{1}"), 
    /**
     * 0: class name, 
     * 1: attribute name
     */
    ATTRIBUTE_PRINT_TEMPLATE_NOT_FOUND("Không tìm thấy mẫu xuất của thuộc tính: {0}.{1}"),
    /**
     * 0: attribute name (for which the function is associated)
     * */
    DATA_FUNCTION_NOT_FOUND("Không tìm thấy hàm dữ liệu: {0}"),
    /**
     * 0: object form
     */
    TARGET_OBJECT_FORM_NOT_FOUND("Không tìm thấy form dữ liệu đích của form: {0}"), 
    /**
     * 0: domain type (one of the DomainConstraint.Type constants)
     */
    DATA_SOURCE_TYPE_NOT_FOUND("Không tìm thấy kiểu dữ liệu nguồn tương ứng của: {0}"), 
    /**
     * 0: application function (one of the Function constants)
     */
    DATA_SOURCE_FUNCTION_NOT_FOUND("Không tìm thấy hàm dữ liệu nguồn tương ứng của: {0}"),
    /***
     * 0: action
     * 1: resource name
     */
    RESOURCE_PERMISSION_NOT_FOUND("Không tìm thấy quyền truy cập {0} của NSD đối với tài nguyên {1}"),
    /**
     * v5.1: 
     * 0: software config class
     * */
    MODULE_DESCRIPTORS_NOT_FOUND("Không có MCCs nào trong cấu hình phần mềm: {0}"),
    /**
     * 0: tên miền GUI
     * @version 5.2
     */
    REGION_NOT_FOUND("Không tìm thấy miền GUI nào có tên: {0}"),
    /**
     * 0: tên thành phần
     * @version 5.2
     */
    COMPONENT_NOT_FOUND("Không tìm thấy thành phần nào có tên: {0}"),
    STATESPACE_NOT_FOUND("Lớp {0} có không gian trạng thái (state space) rỗng"), 
    /** v5.6: graph node <br>
     * 0: node label*/
    NODE_NOT_FOUND("Node not found: {0}. Is this node label correct?"),
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
  
  /**
   * @version
   * - 3.4.c: changed Code to InfoCode
   */
  public NotFoundException(InfoCode errCode, String msg, Object...args) {
    super(errCode, msg, args);
  }
  
  /**
   * @version
   * - 3.4.c: changed Code to InfoCode
   */
  public NotFoundException(InfoCode errCode, Throwable t, String msg, Object...args) {
    super(errCode,msg,t,args);
  }
  
  // v2.7.3
  /**
   * @version
   * - 3.4.c: changed Code to InfoCode
   */
  public NotFoundException(InfoCode errCode, Object...args) {
    super(errCode, args);
  }
  
  // v2.7.3
  /**
   * @version
   * - 3.4.c: changed Code to InfoCode
   */
  public NotFoundException(InfoCode errCode, Throwable t, Object...args) {
    super(errCode,t,args);
  }
}
