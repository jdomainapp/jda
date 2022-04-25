import {Text} from '@ui-kitten/components';
import * as React from 'react';
import {Modal, SafeAreaView, StyleSheet} from 'react-native';
import {
  IJDAModuleControllerProps,
  JDAModuleView,
} from '../../controllers/jda_module_controller/withModuleController';

export interface IJDABasicModuleProps<T> extends IJDAModuleControllerProps<T> {}

export function JDABasicModule<T>(props: IJDABasicModuleProps<T>) {
  return (
    <SafeAreaView>
      {props.ListView}
      <Modal
        visible={props.currentView === JDAModuleView.FORM}
        collapsable={true}
        onRequestClose={() => {}}
        // transparent={true}
        animationType={'slide'}>
        <Text style={styles.formTitle}>
          Form: {props.moduleConfig.moduleName}
        </Text>
        {props.FormView}
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  formTitle: {
    textAlign: 'center',
    fontSize: 20,
  },
});
