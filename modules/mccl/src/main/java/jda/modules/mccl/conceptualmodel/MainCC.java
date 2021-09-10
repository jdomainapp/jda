package jda.modules.mccl.conceptualmodel;

import java.io.File;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;

/**
 * @overview  A sub-type of {@link MCC} that represents the main module's MCC. 
 *    This MCC is special in that its domain class is not specified. 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2c
 */
public class MainCC extends MCC {

  /** the domain name of the target software (e.g. CourseMan) */
  private String domainName;
  
  /** the package where this MCC is stored (e.g. vn.com.courseman.software)*/
  private String pkgName;

  /**
   * @effects 
   *  initialise this with <tt>name, domaiName, pkgName</tt> (the domain class is <tt>null</tt>)
   */
  public MainCC(String name, String domainName, String pkgName) {
    super(name, null);
    
    setPackageName(pkgName);
    
    this.domainName = domainName;
    this.pkgName = pkgName;
  }
  
  /**
   * @modifies this.{@link #ast}
   * @effects 
   *  creates in {@link #ast} a {@link ModuleDescriptor} for the main module class represented by this.
   *  
   *  <p>The <tt>modelDesc</tt> element is not initialised, because module main does not have a domain class.
   */
  @Override
  public void createModuleDesc() {
    /**
     * e.g.
     *  @ModuleDescriptor(
          name="ModuleMain",
          viewDesc=@ViewDesc(
            formTitle="Course Management App: CourseMan",
            imageIcon="courseman.jpg",
            view=View.class,
            viewType=Type.Main,
            topX=0.5,topY=0.0,widthRatio=0.75f,heightRatio=1f, 
            children={
                RegionName.Desktop,
                RegionName.MenuBar,
                RegionName.ToolBar,
                RegionName.StatusBar
            },
            excludeComponents={
              Add
            },
            props={
              @PropertyDesc(name=PropertyName.view_toolBar_buttonIconDisplay,
                  valueAsString="true",valueType=Boolean.class),
              @PropertyDesc(name=PropertyName.view_toolBar_buttonTextDisplay,
                  valueAsString="false",valueType=Boolean.class),
              @PropertyDesc(name=PropertyName.view_searchToolBar_buttonIconDisplay,
                  valueAsString="true",valueType=Boolean.class),
              @PropertyDesc(name=PropertyName.view_searchToolBar_buttonTextDisplay,
                  valueAsString="false",valueType=Boolean.class),
              @PropertyDesc(name=PropertyName.view_lang_international,
                valueAsString="true",valueType=Boolean.class),
            }
          ),
          controllerDesc=@ControllerDesc(controller=Controller.class),
          type=ModuleType.DomainMain
          ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
        )
     */
    
    // first add all necessary imports
    Class[] libClasses = {
        ViewDesc.class,
        View.class,
        RegionName.class,
        RegionType.class,
        ControllerDesc.class,
        Controller.class,
        PropertyDesc.class,
        PropertyName.class,
        ModuleType.class,
        SetUpDesc.class, 
        CopyResourceFilesCommand.class
    };
    
    final CompilationUnit ast = getAst();
    final ClassOrInterfaceDeclaration mccNode = getMccNode(); 
    
    ParserToolkit.addImport(ast, libClasses);
    
    NormalAnnotationExpr moduleDesc = mccNode.addAndGetAnnotation(ModuleDescriptor.class);
    
    NodeList<MemberValuePair> props = new NodeList<>();
    moduleDesc.setPairs(props);
    
    // prop: name
    props.add(new MemberValuePair("name", new StringLiteralExpr(getName())));
    
    // modelDesc: omitted
    
    // prop: viewDesc
    NodeList<MemberValuePair> viewDescProps = new NodeList<>();
    viewDescProps.add(new MemberValuePair("formTitle", new StringLiteralExpr("Software: " + domainName)));
    viewDescProps.add(new MemberValuePair("imageIcon", new StringLiteralExpr(domainName.toLowerCase()+DEF_IMAGE_ICON_EXT)));
//    viewDescProps.add(new MemberValuePair("domainClassLabel", new StringLiteralExpr(clsName)));
    viewDescProps.add(new MemberValuePair("view", ParserToolkit.createSimpleClassExprFor(View.class)));
    viewDescProps.add(new MemberValuePair("viewType", ParserToolkit.createSimpleFieldAccessExprFor(RegionType.Main)));
//    viewDescProps.add(new MemberValuePair("parent", ParserToolkit.createSimpleFieldAccessExprFor(RegionName.Tools)));
    viewDescProps.add(new MemberValuePair("topX", ParserToolkit.createDoubleExpr(0.5)));
    viewDescProps.add(new MemberValuePair("topY", ParserToolkit.createDoubleExpr(0.0)));
    viewDescProps.add(new MemberValuePair("widthRatio", ParserToolkit.createFloatExpr(0.75f)));
    viewDescProps.add(new MemberValuePair("heightRatio", ParserToolkit.createFloatExpr(1f)));
            
    // viewDesc.children
    RegionName[] childRegs = {
        RegionName.Desktop,
        RegionName.MenuBar,
        RegionName.ToolBar,
        RegionName.StatusBar
    };
    NodeList children = new NodeList();
    for (RegionName child: childRegs) {
      children.add(ParserToolkit.createSimpleFieldAccessExprFor(child));
    }
    viewDescProps.add(new MemberValuePair("children", new ArrayInitializerExpr(children)));
    
    // viewDesc.excludeComponents
    RegionName[] exlRegs = {
        RegionName.Add
    };
    NodeList exlRegChildren = new NodeList();
    for (RegionName exlReg: exlRegs) {
      exlRegChildren.add(ParserToolkit.createSimpleFieldAccessExprFor(exlReg));
    }
    viewDescProps.add(new MemberValuePair("excludeComponents", new ArrayInitializerExpr(exlRegChildren)));
    
    // viewDesc.props
    NodeList viewProps = new NodeList();
    final Name propDescName = parseName(PropertyDesc.class.getSimpleName());
    NodeList viewPropEls = new NodeList();
    viewPropEls.add(new MemberValuePair("name", ParserToolkit.createSimpleFieldAccessExprFor(PropertyName.view_toolBar_buttonIconDisplay)));
    viewPropEls.add(new MemberValuePair("valueAsString", ParserToolkit.createStringExpr(Boolean.TRUE+"")));
    viewPropEls.add(new MemberValuePair("valueType", ParserToolkit.createSimpleClassExprFor(Boolean.class)));
    NormalAnnotationExpr viewProp = new NormalAnnotationExpr(propDescName, viewPropEls);
    viewProps.add(viewProp);

    viewPropEls = new NodeList();
    viewPropEls.add(new MemberValuePair("name", ParserToolkit.createSimpleFieldAccessExprFor(PropertyName.view_toolBar_buttonTextDisplay)));
    viewPropEls.add(new MemberValuePair("valueAsString", ParserToolkit.createStringExpr(Boolean.FALSE+"")));
    viewPropEls.add(new MemberValuePair("valueType", ParserToolkit.createSimpleClassExprFor(Boolean.class)));
    viewProp = new NormalAnnotationExpr(propDescName, viewPropEls);
    viewProps.add(viewProp);

    viewPropEls = new NodeList();
    viewPropEls.add(new MemberValuePair("name", ParserToolkit.createSimpleFieldAccessExprFor(PropertyName.view_searchToolBar_buttonIconDisplay)));
    viewPropEls.add(new MemberValuePair("valueAsString", ParserToolkit.createStringExpr(Boolean.TRUE+"")));
    viewPropEls.add(new MemberValuePair("valueType", ParserToolkit.createSimpleClassExprFor(Boolean.class)));
    viewProp = new NormalAnnotationExpr(propDescName, viewPropEls);
    viewProps.add(viewProp);

    viewPropEls = new NodeList();
    viewPropEls.add(new MemberValuePair("name", ParserToolkit.createSimpleFieldAccessExprFor(PropertyName.view_searchToolBar_buttonTextDisplay)));
    viewPropEls.add(new MemberValuePair("valueAsString", ParserToolkit.createStringExpr(Boolean.FALSE+"")));
    viewPropEls.add(new MemberValuePair("valueType", ParserToolkit.createSimpleClassExprFor(Boolean.class)));
    viewProp = new NormalAnnotationExpr(propDescName, viewPropEls);
    viewProps.add(viewProp);

    viewPropEls = new NodeList();
    viewPropEls.add(new MemberValuePair("name", ParserToolkit.createSimpleFieldAccessExprFor(PropertyName.view_lang_international)));
    viewPropEls.add(new MemberValuePair("valueAsString", ParserToolkit.createStringExpr(Boolean.TRUE+"")));
    viewPropEls.add(new MemberValuePair("valueType", ParserToolkit.createSimpleClassExprFor(Boolean.class)));
    viewProp = new NormalAnnotationExpr(propDescName, viewPropEls);
    viewProps.add(viewProp);
    
    viewDescProps.add(new MemberValuePair("props", new ArrayInitializerExpr(viewProps)));
    
    NormalAnnotationExpr viewDesc = new NormalAnnotationExpr(
        parseName(ViewDesc.class.getSimpleName()), viewDescProps);
    
    MemberValuePair viewDescProp = new MemberValuePair("viewDesc", viewDesc);
    props.add(viewDescProp);
    
    // prop: controllerDesc
    MemberValuePair controllerDescProp = new MemberValuePair("controllerDesc", 
        ParserToolkit.createSimpleAnnotationExpr(ControllerDesc.class, "controller", 
            ParserToolkit.createSimpleClassExprFor(Controller.class)));
    props.add(controllerDescProp);
    
    // type
    MemberValuePair typeProp = new MemberValuePair("type", ParserToolkit.createSimpleFieldAccessExprFor(ModuleType.DomainMain));
    props.add(typeProp);
    
    // setupDesc
    MemberValuePair setupDescProp = new MemberValuePair("setUpDesc", 
        ParserToolkit.createSimpleAnnotationExpr(SetUpDesc.class, "postSetUp", 
            ParserToolkit.createSimpleClassExprFor(CopyResourceFilesCommand.class)));
    props.add(setupDescProp);
  }

  /**
   * @effects 
   *  return pkgName 
   * @version 5.4.1
   */
  @Override
  public String getPackage() {
    return pkgName;
  }
}
