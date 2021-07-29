package jda.modules.exportdoc.util.table;

import static jda.modules.exportdoc.util.table.Table.Prop.*;

import javax.swing.ImageIcon;

import jda.mosa.view.assets.GUIToolkit.ImageType;

public class ImageCell extends Cell<ImageIcon> {
  private String name;
  private ImageType imageType;

  public ImageCell(ImageIcon val, String name, ImageType imageType) {
    super(val);
    this.name=name;
    this.imageType=imageType;
  }

  public String getName() {
    return name;
  }

  public ImageType getImageType() {
    return imageType;
  } 
  
  @Override
  public void finalise() {
    int w, h;
    
    ImageIcon img = getVal();
    int cellSpacing = getProperty().getIntegerValue(MarginCell, 0);
    
    w = img.getIconWidth() + 2*cellSpacing;
    h = img.getIconHeight() + 2*cellSpacing;
    
    setProperty(
        PreferredWidth, w,
        PreferredHeight, h
        );
  }

  @Override
  public ImageCell clone() {
    ImageIcon val = getVal();
    return new ImageCell(new ImageIcon(val.getImage()), name, imageType);
  }
}
