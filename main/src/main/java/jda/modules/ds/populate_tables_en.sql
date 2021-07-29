--------------------------------------------
--------- CONFIGURATION SETTINGS -----------
--------------------------------------------
delete from en.regionstyle;
delete from en.region;
delete from en.style;


insert into en.region 
values
  -- root (top-level) region
  (1,'root',DEFAULT,DEFAULT,DEFAULT,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2,'main',DEFAULT,DEFAULT,1,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3,'data',DEFAULT,DEFAULT,1,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4,'report',DEFAULT,DEFAULT,1,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  -- gui settings
  -- name: has the form: <domain class> + "GUI"
  -- type: matches the top-level region name
  (201,'CourseManGUI','CourseMan Application',DEFAULT,1,800,600,'main',DEFAULT),
  (301,'StudentManagerGUI','Manage students',DEFAULT,1,500,350,'data',DEFAULT),
  (302,'CompulsoryModuleManagerGUI','Manage compulsory modules',DEFAULT,1,500,350,'data',DEFAULT),
  (303,'ElectiveModuleManagerGUI','Manage elective modules',DEFAULT,1,500,350,'data',DEFAULT),
  (304,'EnrolmentManagerGUI','Manage enrolments',DEFAULT,1,500,350,'data',DEFAULT),
  (401,'StudentReportByAddressGUI','Students by address',DEFAULT,1,500,350,'report',DEFAULT),
  (402,'StudentModulesReportGUI','Student enrolled modules',DEFAULT,1,600,350,'report',DEFAULT),
  (403,'StudentProfileReportGUI','Student profile',DEFAULT,1,600,350,'report',DEFAULT),
  (404,'StudentModuleCountByNameReportGUI','Module count (by name)',DEFAULT,1,600,350,'report',DEFAULT),
  (405,'StudentModuleCountByIDReportGUI','Module count (by id)',DEFAULT,1,600,350,'report',DEFAULT),
  -- level-1 main child regions
  (21,'menubar',DEFAULT,DEFAULT,2,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (22,'toolbar','Tools',DEFAULT,2,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (23,'statusbar',DEFAULT,DEFAULT,2,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  -- level-1 data child regions
  (31,'components',DEFAULT,DEFAULT,3,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (32,'actions',DEFAULT,DEFAULT,3,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (33,'statusbar','Status',DEFAULT,3,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (34,'searchtoolbar','Search toolbar',DEFAULT,3,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  -- level-1 report child regions
  (41,'components',DEFAULT,DEFAULT,4,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (42,'actions',DEFAULT,DEFAULT,4,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (43,'statusbar','Status',DEFAULT,4,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  -- level-2 regions
  --- menus
  (211,'File','File',DEFAULT,21,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (212,'Tools','Tools',DEFAULT,21,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (213,'Options','Options',DEFAULT,21,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (214,'Help','Help',DEFAULT,21,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- File menu items
  (2111,'New','New','new.gif',211,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2112,'Close','Close','close.gif',211,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2113,'Exit','Exit','exit.gif',211,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- Tools menu items
  (2121,'StudentManager','Manage students','sman.gif',212,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2122,'CompulsoryModuleManager','Manage compulsory modules','mman.gif',212,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2123,'ElectiveModuleManager','Manage elective modules','eman.gif',212,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2124,'EnrolmentManager','Manage enrolments','eman.gif',212,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (2125,'Report','View reports','report.gif',212,DEFAULT,DEFAULT,'menu',DEFAULT),
  (2126,'FindToolBar','Find...','find.gif',212,100,20,'check','true'),
  ----- Report submenus
  (21251,'StudentReportByAddress','Students by address','studentreport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (21252,'StudentModulesReport','Student enrolled modules','studenmodulestreport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (21253,'StudentProfileReport','Student profile','studentprofilereport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (21254,'StudentModuleCountByNameReport','Module count by name','studentmodulecountreport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (21255,'StudentModuleCountByIDReport','Module count by id','studentmodulecountreport.gif',2125,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- Options menu items
  (2131,'Language','Language','language.gif',213,DEFAULT,DEFAULT,'choicemenu',DEFAULT),
  ----- Language submenu items
  (21311,'vi','Tiếng Việt','lang_vi.gif',2131,DEFAULT,DEFAULT,'check','true'),
  (21312,'en','English (US)','lang_en.gif',2131,DEFAULT,DEFAULT,'check','false'),
  --- toolbar
  (221,'New','New','new.gif',22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  --(222,'Save','Save','save.gif',22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (222,'Update','Update','edit.gif', 22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (223,'Previous','Previous','left.gif',22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (224,'Next','Next','right.gif',22,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  --- searchtoolbar
  (341,'SearchQueryField',DEFAULT,DEFAULT,34,DEFAULT,DEFAULT,'text','<id>'),  
  (342,'Find',DEFAULT,'find.gif',34,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  --- components
  ---- student
  (311,'Student',DEFAULT,DEFAULT, 31,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3111,'title','Enter student details',DEFAULT, 311,DEFAULT,DEFAULT,'label',DEFAULT),
  (3112,'id','ID: ',DEFAULT, 311,DEFAULT,DEFAULT,'label',DEFAULT),
  (3113,'name','Name: ',DEFAULT, 311,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3114,'dob','Date of birth: ',DEFAULT, 311,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3115,'address','Home address: ',DEFAULT, 311,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3116,'email','Email: ',DEFAULT, 311,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- compulsory module
  (312,'CompulsoryModule',DEFAULT,DEFAULT, 31,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3121,'title','Enter compulsory module details',DEFAULT, 312,DEFAULT,DEFAULT,'label',DEFAULT),
  (3122,'code','Module code: ',DEFAULT, 312,DEFAULT,DEFAULT,'label',DEFAULT),
  (3123,'name','Name: ',DEFAULT, 312,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3124,'semester','Semester: ',DEFAULT, 312,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3125,'credits','No. of credits: ',DEFAULT, 312,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- elective module
  (313,'ElectiveModule',DEFAULT,DEFAULT, 31,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3131,'title','Enter elective module details',DEFAULT, 313,DEFAULT,DEFAULT,'label',DEFAULT),
  (3132,'deptName','Department name: ',DEFAULT, 313,DEFAULT,DEFAULT,'label',DEFAULT),
  (3133,'code','Module code: ',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3134,'name','Name: ',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3135,'semester','Semester: ',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3136,'credits','No. of credits: ',DEFAULT, 313,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- enrolment
  (314,'Enrolment',DEFAULT,DEFAULT, 31,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3141,'title','Enter enrolment details',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3142,'id','Enrolment ID: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3143,'student','Student id: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3144,'module','Module code: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3145,'internalMark','Internal mark: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3146,'examMark','Examination mark: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (3147,'finalGrade','Final grade: ',DEFAULT, 314,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  ---- report: student by address
  (411,'StudentReportByAddress',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4111,'title','Student report by address',DEFAULT, 411,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4112,'address','Address: ',DEFAULT, 411,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4113,'id','Student ID',DEFAULT, 411,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4114,'name','Student name',DEFAULT, 411,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- report: enrolled modules
  (412,'StudentModulesReport',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4121,'title','Student enrolled modules',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4122,'id','Student ID',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4123,'code','Module code',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4124,'name','Module name',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4125,'semester','Semester',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4126,'credits','Credits',DEFAULT, 412,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- report: student profile
  (413,'StudentProfileReport',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4131,'title','Student profile report',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4132,'id','Student ID',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4133,'name','Student name',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4134,'dob','Date of birth',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4135,'address','Address',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4136,'email','Email',DEFAULT, 413,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- report: student module count (by name)
  (414,'StudentModuleCountByNameReport',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4141,'title','Modules count by name',DEFAULT, 414,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4142,'id','Student ID',DEFAULT, 414,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4143,'name','Student name',DEFAULT, 414,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4144,'numEnrolledModules','Num. enrolled modules',DEFAULT, 414,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  ---- report: student module count (by id)
  (415,'StudentModuleCountByIDReport',DEFAULT,DEFAULT, 41,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (4151,'title','Modules count by ID',DEFAULT, 415,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4152,'id','Student ID',DEFAULT, 415,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4153,'name','Student name',DEFAULT, 415,DEFAULT,DEFAULT,DEFAULT,DEFAULT),  
  (4154,'numEnrolledModules','Num. enrolled modules',DEFAULT, 415,DEFAULT,DEFAULT,DEFAULT,DEFAULT), 
  --- data-entry actions
  (321,'Create','Create','create.gif', 32,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (322,'Cancel','Cancel','cancel.gif', 32,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  --- report actions
  (421,'ExecuteReport','Execute...','execute.gif', 42,DEFAULT,DEFAULT,DEFAULT,DEFAULT),
  (422,'Cancel','Cancel','cancel.gif', 42,DEFAULT,DEFAULT,DEFAULT,DEFAULT)
  ;

insert into en.style
values
  -- font style: plain=0,bold=1,italic=2
  -- default style
  (1,'Arial,12,0',DEFAULT,DEFAULT),
  -- white background
  (2, DEFAULT,'255,255,255',DEFAULT),
  -- big, bold font with light-blue color
  (3, 'Arial,20,1','255,255,255','0,100,255')
  ; 
    
insert into en.regionstyle
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