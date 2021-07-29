--------------------------------------------
--------- CONFIGURATION SETTINGS -----------
--------------------------------------------
delete from vi.regionstyle;
delete from vi.region;
delete from vi.style;


insert into vi.region 
values
  -- root (top-level) region
  (1,'root',DEFAULT,DEFAULT,DEFAULT,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2,'main',DEFAULT,DEFAULT,1,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3,'data',DEFAULT,DEFAULT,1,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4,'report',DEFAULT,DEFAULT,1,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  -- gui settings
  -- name: has the form: <domain class>|<application name> + "GUI"
  -- type: matches the top-level region name
  (201,'CourseManGUI','Chương trình quản lí khóa học - CourseMan',DEFAULT,1,800,600,'main',DEFAULT),
  (301,'StudentManagerGUI','Quản lí sinh viên',DEFAULT,1,500,350,'data',DEFAULT),
  (302,'CompulsoryModuleManagerGUI','Quản lí môn bắt buộc',DEFAULT,1,500,350,'data',DEFAULT),
  (303,'ElectiveModuleManagerGUI','Quản lí môn tự chọn',DEFAULT,1,500,350,'data',DEFAULT),
  (304,'EnrolmentManagerGUI','Quản lí đăng kí môn học/sổ điểm',DEFAULT,1,500,350,'data',DEFAULT),
  (401,'StudentReportByAddressGUI','Sinh viên theo vùng',DEFAULT,1,500,350,'report',DEFAULT),
  (402,'StudentModulesReportGUI','Môn học theo sinh viên',DEFAULT,1,600,350,'report',DEFAULT),
  (403,'StudentProfileReportGUI','Hồ sơ sinh viên',DEFAULT,1,600,350,'report',DEFAULT),
  (404,'StudentModuleCountByNameReportGUI','Số môn học (theo tên)',DEFAULT,1,600,350,'report',DEFAULT),
  (405,'StudentModuleCountByIDReportGUI','Số môn học (theo mã sv)',DEFAULT,1,600,350,'report',DEFAULT),
  -- level-1 main child regions
  (21,'menubar',DEFAULT,DEFAULT,2,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (22,'toolbar','Tools',DEFAULT,2,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (23,'statusbar',DEFAULT,DEFAULT,2,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  -- level-1 data child regions
  (31,'components',DEFAULT,DEFAULT,3,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (32,'actions',DEFAULT,DEFAULT,3,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (33,'statusbar','Trạng thái',DEFAULT,3,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (34,'searchtoolbar','Tìm kiếm',DEFAULT,3,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  -- level-1 report child regions
  (41,'components',DEFAULT,DEFAULT,4,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (42,'actions',DEFAULT,DEFAULT,4,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (43,'statusbar','Trạng thái',DEFAULT,4,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  -- level-2 regions
  --- menus
  (211,'File','Tệp',DEFAULT,21,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (212,'Tools','Công cụ',DEFAULT,21,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (213,'Options','Tùy chọn',DEFAULT,21,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (214,'Help','Trợ giúp',DEFAULT,21,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- File menu items
  (2111,'New','Tạo mới...','new.gif',211,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2112,'Close','Đóng cửa sổ','close.gif',211,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2113,'Exit','Kết thúc','exit.gif',211,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- Tools menu items
  (2121,'StudentManager','Quản lí sinh viên','sman.gif',212,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2122,'CompulsoryModuleManager','Quản lí môn học bắt buộc','mman.gif',212,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2123,'ElectiveModuleManager','Quản lí môn học tự chọn','eman.gif',212,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2124,'EnrolmentManager','Quản lí đăng kí/điểm TK môn','eman.gif',212,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2125,'Report','Báo cáo','report.gif',212,DEFAULT,DEFAULT,'menu',DEFAULT),
  (2126,'FindToolBar','Tìm kiếm...','find.gif',212,100,20,'check','false'),
  ----- Report submenus
  (21251,'StudentReportByAddress','Sinh viên theo vùng','studentreport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (21252,'StudentModulesReport','Môn học theo sinh viên','studenmodulestreport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (21253,'StudentProfileReport','Hồ sơ sinh viên','studentprofilereport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (21254,'StudentModuleCountByNameReport','Số môn học của sinh viên (theo tên)','studentmodulecountreport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (21255,'StudentModuleCountByIDReport','Số môn học của sinh viên (theo mã sv)','studentmodulecountreport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- Options menu items
  (2131,'Language','Ngôn ngữ','language.gif',213,DEFAULT,DEFAULT,'choicemenu',DEFAULT),
  ----- Language submenu items
  (21311,'vi','Tiếng Việt','lang_vi.gif',2131,DEFAULT,DEFAULT,'check','true'),
  (21312,'en','Tiếng Anh','lang_vi.gif',2131,DEFAULT,DEFAULT,'check','false'),
  --- toolbar
  (221,'Open','Mở','open.gif',22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (222,'New','Tạo mới','new.gif',22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  --(222,'Save','Lưu','save.gif',22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (223,'Update','Cập nhật','edit.gif', 22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (224,'Previous','Trước','left.gif',22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (225,'Next','Sau','right.gif',22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  --- searchtoolbar
  (341,'SearchQueryField',DEFAULT,DEFAULT,34,DEFAULT,DEFAULT,'text','<mã>'),  
  (342,'Find',DEFAULT,'find.gif',34,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  --- components
  ---- student
  (311,'Student',DEFAULT,DEFAULT, 31,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3111,'title','Thông tin sinh viên',DEFAULT, 311,DEFAULT,DEFAULT,'label',DEFAULT),
  (3112,'id','Mã: ',DEFAULT, 311,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3113,'name','Tên: ',DEFAULT, 311,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3114,'dob','Ngày sinh: ',DEFAULT, 311,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3115,'address','Địa chỉ: ',DEFAULT, 311,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3116,'email','Thư điện tử: ',DEFAULT, 311,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- compulsory module
  (312,'CompulsoryModule',DEFAULT,DEFAULT, 31,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3121,'title','Thông tin môn học bắt buộc',DEFAULT, 312,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3122,'code','Mã môn: ',DEFAULT, 312,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3123,'name','Tên môn: ',DEFAULT, 312,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3124,'semester','Học kì: ',DEFAULT, 312,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3125,'credits','Số đvht: ',DEFAULT, 312,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- elective module
  (313,'ElectiveModule',DEFAULT,DEFAULT, 31,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3131,'title','Thông tin môn học tự chọn',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3132,'deptName','Tên khoa: ',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3133,'code','Mã môn: ',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3134,'name','Tên môn: ',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3135,'semester','Học kì: ',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3136,'credits','Số đvht: ',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- enrolment
  (314,'Enrolment',DEFAULT,DEFAULT, 31,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3141,'title','Thông tin đăng kí/điểm TK',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3142,'id','Mã đăng kí: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3143,'student','Mã sinh viên: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3144,'module','Mã môn học: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3145,'internalMark','Điểm thành phần (TK): ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3146,'examMark','Điểm thi: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3147,'finalGrade','Xếp loại: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- report: student by address
  (411,'StudentReportByAddress',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4111,'title','Báo cáo sinh viên theo vùng',DEFAULT, 411,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4112,'address','Tên thành phố (địa chỉ): ',DEFAULT, 411,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4113,'id','Mã sinh viên',DEFAULT, 411,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4114,'name','Tên sinh viên',DEFAULT, 411,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- report: enrolled modules
  (412,'StudentModulesReport',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4121,'title','Báo cáo đăng kí môn học/điểm TK',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4122,'id','Mã sinh viên',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4123,'code','Mã môn học',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4124,'name','Tên môn',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4125,'semester','Học kì',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4126,'credits','Số đvht',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- report: student profile
  (413,'StudentProfileReport',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4131,'title','Hồ sơ sinh viên',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4132,'id','Mã sinh viên',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4133,'name','Tên sinh viên',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4134,'dob','Ngày sinh',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4135,'address','Tên thành phố (địa chỉ)',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4136,'email','Thư điện tử',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- report: student module count (by name)
  (414,'StudentModuleCountByNameReport',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4141,'title','Số môn học của sinh viên (theo tên)',DEFAULT, 414,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4142,'id','Mã sinh viên',DEFAULT, 414,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4143,'name','Tên sinh viên',DEFAULT, 414,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4144,'numEnrolledModules','Số môn đăng ký',DEFAULT, 414,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- report: student module count (by id)
  (415,'StudentModuleCountByIDReport',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4151,'title','Số môn học của sinh viên (theo mã sv)',DEFAULT, 415,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4152,'id','Mã sinh viên',DEFAULT, 415,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4153,'name','Tên sinh viên',DEFAULT, 415,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4154,'numEnrolledModules','Số môn đăng ký',DEFAULT, 415,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  --- data-entry actions
  (321,'Create','Tạo','create.gif', 32,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (322,'Cancel','Hủy bỏ','cancel.gif', 32,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  --- report actions
  (421,'ExecuteReport','Chạy báo cáo...','execute.gif', 42,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (422,'Cancel','Hủy bỏ','cancel.gif', 42,DEFAULT,DEFAULT,DEFAULT,DEFAULT)
  ;

insert into vi.style
values
  -- font style: plain=0,bold=1,italic=2
  -- default style
  (1,'Arial,12,0',DEFAULT,DEFAULT),
  -- white background
  (2, DEFAULT,'255,255,255',DEFAULT),
  -- big, bold font with light-blue color
  (3, 'Arial,20,1','255,255,255','0,100,255')
  ; 
    
insert into vi.regionstyle
values
  (1,1),
  (2,2),
  (3,2),
  (3111,3),
  (3121,3),
  (3131,3),
  (3141,3),
  (4111,3),
  (4121,3),
  (4131,3),
  (4141,3),
  (4151,3)
  ;