package jda.modules.security.accountmanager;


import static jda.modules.mccl.conceptmodel.view.RegionName.*;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.accountmanager.controller.command.OpenCurrentUserCommand;
import jda.modules.security.def.DomainUser;
import jda.mosa.controller.Controller;
import jda.mosa.controller.assets.helper.objectbrowser.SingularIdPooledObjectBrowser;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;

/**
 * @overview
 *  A module bound to {@link DomainUser} that is used for a user to manage his/her account details 
 *  (incl. full name and password). The user name cannot be changed.
 * 
 *  <p>Note: this module only displays the currently logged-in user and 
 *  enables the user to change the above details. It does not display other user's accounts.
 *  
 * @author dmle
 * @version 3.2
 */
@ModuleDescriptor(
name="ModuleAccountManager",
modelDesc=@ModelDesc(
    model=DomainUser.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Tài khoản đăng nhập",
    formTitle="Quản lý tài khoản đăng nhập", 
    imageIcon="frmDomainUser.png",
    viewType=RegionType.Data,
    parent=RegionName.Tools,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    topX=0.5,topY=0.0
    ,excludeComponents={
      Open, New, Delete, Refresh, Reload, CopyObject, First, Previous, Next, Last, Actions
    }
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.O_C
    ,objectBrowser=SingularIdPooledObjectBrowser.class
    ,defaultCommand=LAName.Open
    ,props={
      // custom Open command: to open and show only the logged in user
      @PropertyDesc(name=PropertyName.controller_dataController_open,
          valueIsClass=OpenCurrentUserCommand.class, valueAsString=CommonConstants.NullValue,
          valueType=Class.class)
    }
    //,isDataFieldStateListener=true
),
type=ModuleType.System,
isPrimary=true
)
public class ModuleAccountManager {
  @AttributeDesc(label="Tài khoản đăng nhập")
  private String title;

  @AttributeDesc(label="Họ và tên",alignX=AlignmentX.Center)
  private String name;

  @AttributeDesc(label="Tên đăng nhập",alignX=AlignmentX.Center)
  private String login;
  
  @AttributeDesc(label="Mật khẩu",alignX=AlignmentX.Center)
  private String password;
}