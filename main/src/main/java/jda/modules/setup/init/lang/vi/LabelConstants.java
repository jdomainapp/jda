package jda.modules.setup.init.lang.vi;


/**
 * Represents Vietnamese labels.
 * 
 * @author dmle
 * 
 */
public class LabelConstants {
  
//  static {
//    System.out.println("Initialising Vietnamese labels...");
//  }

  public static final Label Desktop = new Label("Cửa sổ chính");
  public static final Label MenuBar = new Label("Thanh Menu");
  public static final Label ToolBar = new Label("Thanh công cụ");
  public static final Label StatusBar = new Label("Thanh trạng thái");

  // / menu bar regions
  public static final Label File = new Label("Tệp");
  public static final Label Tools = new Label("Công cụ");
  public static final Label Options = new Label("Tùy chọn");
  public static final Label Help = new Label("Trợ giúp");

  // / File menu
//  public static final Label FileOpen = new Label("Mở");
  public static final Label FileExit = new Label("Thoát");
//  public static final Label Login = new Label("Đăng nhập");
//  public static final Label Logout = new Label("Đăng xuất");

  // Tools menu
  public static final Label SearchToolBar = new Label("Tìm...");
  public static final Label ToolReport = new Label("Báo cáo");
  public static final Label ToolSecurity = new Label("Bảo mật");
  // / other specific tool menu items are to be added by domain applications

  // Options menu
//  public static final Label OptionLanguage = new Label("Ngôn ngữ");
//
//  public static final Label LanguageVi = new Label("Tiếng Việt");
//
//  public static final Label LanguageEn = new Label("Tiếng Anh ");
  
  // v2.6.4.a: Windows menu
  public static final Label Window = new Label("Cửa sổ");
  public static final Label WindowTile = new Label("Đồng thời");
  public static final Label WindowCascade = new Label("Gối nhau");
  public static final Label WindowSubMenuAuto = new Label("Tự động xếp");
  public static final Label WindowAutoTile = new Label("Đồng thời");
  public static final Label WindowAutoCascade = new Label("Gối nhau");
  public static final Label WindowClose = new Label("Đóng cửa sổ");

  // Tool menu
  public static final Label ToolOpen = new Label("Mở");
  public static final Label ToolCopyObject = new Label("Chép");
  public static final Label ToolRefresh = new Label("Xem mới");
  public static final Label ToolReload = new Label("Tải mới");
  public static final Label ToolExport = new Label("Xuất");
  public static final Label ToolPrint = new Label("In");
  public static final Label ToolChart = new Label("Biểu đồ");
  public static final Label ToolNew = new Label("Tạo mới");
  public static final Label ToolAdd = new Label("Thêm");
  public static final Label ToolUpdate = new Label("Cập nhật");
  public static final Label ToolDelete = new Label("Xóa");
  public static final Label ToolFirst = new Label("Đầu");
  public static final Label ToolPrevious = new Label("Trước");
  public static final Label ToolNext = new Label("Sau");
  public static final Label ToolLast = new Label("Cuối");

  public static final Label ToolViewCompact = new Label("Thu gọn");

  // v3.2c
  public static final Label ToolHelpButton = new Label("Trợ giúp");

  // search tool bar regions
  public static final Label ToolSearchText = new Label("Truy vấn");
  public static final Label ToolSearchOption = new Label("Tất cả");
  public static final Label ToolSearch = new Label("Tìm");
  public static final Label ToolClearSearch = new Label("Xóa");
  public static final Label ToolCloseSearchToolBar = new Label("Đóng lại");
  
  // data actions
  public static final Label ActionCreate = new Label("Tạo");
  public static final Label ActionReset = new Label("Nhập lại");
  public static final Label ActionCancel = new Label("Hủy bỏ");

  // report actions
  public static final Label ActionReportCreate = new Label("Chạy báo cáo");

  // login action
  public static final Label ActionLogin = new Label("Đăng nhập");
  
  // status bar labels
  public static final Label StatusUserInfo = new Label("Người dùng");
    
  //////////////////////////////// Labels for special classes ////////////////////

//v2.7.4: removed to use labels defined in the module descriptors
//  // Module Logout
//  public static final Label Logout = new Label("Đăng xuất");
//
//  /**LoginUser*/
//  public static final Label Login = new Label("Đăng nhập");
//  public static final Label ModuleLogin_title = new Label("Thông tin tài khoản");
//  public static final Label ModuleLogin_login = new Label("Tên truy cập");
//  public static final Label ModuleLogin_password = new Label("Mật khẩu");
//  public static final Label ModuleLogin_role = new Label("Vai trò");
//
//  public static final Label LoginUser_title = new Label("Thông tin tài khoản");
//  public static final Label LoginUser_login = new Label("Tên truy cập");
//  public static final Label LoginUser_password = new Label("Mật khẩu");
//  public static final Label LoginUser_role = new Label("Vai trò");

  /**Configuration*/
//  public static final Label ModuleConfiguration = new Label("Cấu hình chương trình");
//  public static final Label ModuleConfiguration_title = new Label("Cấu hình chương trình");
//  public static final Label ModuleConfiguration_appName = new Label("Tên chương trình");
//  public static final Label ModuleConfiguration_version = new Label("Phiên bản");  
//  public static final Label ModuleConfiguration_appFolder = new Label("Thư mục <br>chương trình");  
//  public static final Label ModuleConfiguration_language = new Label("Ngôn ngữ");
//  public static final Label ModuleConfiguration_organisation = new Label("Về công ty");
//  public static final Label ModuleConfiguration_modules = new Label("Các mô-đun");
//  public static final Label ModuleConfiguration_defaultModule = new Label("Mô-đun mặc định");
//  public static final Label ModuleConfiguration_setUpFolder = new Label("Thư mục cài đặt");
//  public static final Label ModuleConfiguration_dbName = new Label("CSDL");  
//  public static final Label ModuleConfiguration_userName = new Label("Tên NSD mặc định");
//  public static final Label ModuleConfiguration_password = new Label("Mật khẩu");  
//  public static final Label ModuleConfiguration_mainGUISizeRatio = new Label("Tỷ lệ kích thước <br>cửa sổ chính");
//  public static final Label ModuleConfiguration_childGUISizeRatio = new Label("Tỷ lệ kích thước <br>cửa sổ con");
//  public static final Label ModuleConfiguration_useSecurity = new Label("Bảo mật?");    
//  public static final Label ModuleConfiguration_listSelectionTimeOut = new Label("Độ trễ spinner");
//  public static final Label ModuleConfiguration_fontLocation = new Label("Thư mục phông");
//  public static final Label ModuleConfiguration_imageLocation = new Label("Thư mục ảnh");
//  public static final Label ModuleConfiguration_labelConstantClass = new Label("Lớp dữ liệu nhãn");
//
//  /**ControllerConfig*/
//  public static final Label ModuleControllerConfig = new Label("Cấu hình điều khiển");
//  public static final Label ModuleControllerConfig_id = new Label("Mã");
//  public static final Label ModuleControllerConfig_controller = new Label("Loại điều khiển");
//  public static final Label ModuleControllerConfig_dataController = new Label("Điều khiển <br>dữ liệu");
//  public static final Label ModuleControllerConfig_openPolicy = new Label("Chính sách mở <br>dữ liệu");
//  public static final Label ModuleControllerConfig_defaultCommand = new Label("Chức năng <br>mặc định");
//  public static final Label ModuleControllerConfig_isStateListener = new Label("Xử lí sự kiện <br>trạng thái?");
//  public static final Label ModuleControllerConfig_applicationModule = new Label("Thuộc mô-đun");
//
//  /**UserApplicationModule*/
//  public static final Label ModuleDomainApplicationModule = new Label("Các chức năng chương trình");
//  public static final Label ModuleDomainApplicationModule_domainModule = new Label("");
//
//  /**Module*/
//  public static final Label ModuleApplicationModule = new Label("Mô-đun chương trình");
//  public static final Label ModuleApplicationModule_name = new Label("Tên");
//  public static final Label ModuleApplicationModule_config = new Label("Cấu hình");
//  public static final Label ModuleApplicationModule_controllerCfg = new Label("Điều khiển");
//  public static final Label ModuleApplicationModule_domainClass = new Label("Lớp dữ liệu");
//  public static final Label ModuleApplicationModule_isViewer = new Label("Mô-đun xem <br>dữ liệu?");
//  public static final Label ModuleApplicationModule_isPrimary = new Label("Mô-đun chính?");
//
//  /**Organisation*/
//  public static final Label ModuleOrganisation = new Label("Công ty");    
//  public static final Label ModuleOrganisation_title = new Label("Công ty");
//  public static final Label ModuleOrganisation_name = new Label("Tên");
//  public static final Label ModuleOrganisation_logo = new Label("Lô-gô");
//  public static final Label ModuleOrganisation_contactDetails = new Label("Liên hệ");
//  public static final Label ModuleOrganisation_url = new Label("Trang web");
//
//  /**Chart*/
//  public static final Label ModuleChart = new Label("Biểu đồ");
//  public static final Label ModuleChart_chartTitle = new Label("Tiêu đề");
//  public static final Label ModuleChart_chartType = new Label("Loại");
//  public static final Label ModuleChart_categoryByColumn = new Label("Nhóm dữ liệu <br>theo cột");
//  public static final Label ModuleChart_chart = new Label("");
//  
//  /**Export document*/
//  public static final Label ModuleExportDocument = new Label("Xuất & in dữ liệu");
//  public static final Label ModuleExportDocument_name = new Label("Tên văn bản");
//  public static final Label ModuleExportDocument_docTitle = new Label("Tiêu đề");
//  public static final Label ModuleExportDocument_pages = new Label(MetaConstants.SYMBOL_ContainerHandle);
//
//  public static final Label ModulePage = new Label("-");
//  public static final Label ModulePage_outputFile = new Label("");
//
//  public static final Label ModuleHtmlPage = new Label("-");
//  public static final Label ModuleHtmlPage_outputFile = new Label("");
//
//  /**ImportData*/
//  public static final Label ModuleImportData = new Label("Nhập dữ liệu");
//  public static final Label ModuleImportData_osmType = new Label("Chọn loại <br>lưu trữ");
//  public static final Label ModuleImportData_domainClass = new Label("Chọn loại <br>dữ liệu");
//
//  /**Help*/
//  public static final Label ModuleHelp = new Label("Trợ giúp chương trình");
//  public static final Label ModuleHelp_title = new Label("Trợ giúp");
//  public static final Label ModuleHelp_config = new Label("Chương trình");
//  public static final Label ModuleHelp_helpContents = new Label("Các mô-đun <br>chương trình");
//
//  public static final Label ModuleHelpContent = new Label("Trợ giúp mô-đun");
//  public static final Label ModuleHelpContent_module = new Label("Mô-đun");
//  
//  public static final Label ModuleHelpContent_overview = new Label("Mô tả chung");
//  public static final Label ModuleHelpContent_titleDesc = new Label("Tiêu đề");
//  public static final Label ModuleHelpContent_helpItems = new Label("Nội dung");
//  public static final Label ModuleHelpContent_appHelp = new Label("Chương trình");
//  
//  public static final Label ModuleHelpItem = new Label("Thành phần");
//  public static final Label ModuleHelpItem_region = new Label("Thành phần <br>giao diện");
//  public static final Label ModuleHelpItem_description = new Label("Mô tả");
//  public static final Label ModuleHelpItem_helpContent = new Label("Mô-đun");
}
