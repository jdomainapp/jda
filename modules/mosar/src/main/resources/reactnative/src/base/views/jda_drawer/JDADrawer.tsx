import {
  DrawerContentScrollView,
  DrawerItemList,
} from '@react-navigation/drawer';
import * as React from 'react';
import {
  IJDADrawerControllerProps,
  withDrawerController,
} from '../../controllers/jda_drawer_controllers/withDrawerController';

export interface IJDADrawerProps extends IJDADrawerControllerProps {}

function JDADrawer(props: IJDADrawerProps) {
  return (
    <DrawerContentScrollView {...props.drawerProps}>
      <DrawerItemList {...props.drawerProps} />
    </DrawerContentScrollView>
  );
}

export default withDrawerController(JDADrawer);
