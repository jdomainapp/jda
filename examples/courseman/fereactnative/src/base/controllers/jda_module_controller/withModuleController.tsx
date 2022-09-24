import {NativeStackScreenProps} from '@react-navigation/native-stack';
import * as React from 'react';
import {ComponentType} from 'react';
import {Modules} from '../../../data_types/enums/Modules';
import {IAPIConfig} from '../../common_hooks/useAPI';
import {
  IJDAFormControlerProps,
  IJDAFormRef,
  JDAControlledFormComponent,
} from '../jda_form_controllers/withFormController';
import {
  IJDAListControllerProps,
  IJDAListRef,
  JDAControlledListComponent,
} from '../jda_list_controllers/hocs/withJDAListController';
import {JDARouterContext} from '../jda_router/JDARouterContext';
import {useRouter} from '../jda_router/useRouter';
import {useFormHandler} from './hooks/useFormHandler';
import {useListHandler} from './hooks/useListHandler';

export enum JDAModuleMode {
  CREATE_ITEM,
  EDIT_ITEM,
  VIEW_ITEM,
  VIEW_LIST_ITEM,
}

/**
 * @param valueKey : In case other module request create new Object for specific key,
 * after create object done, current module will go back and return new object with this key and object[keyValue] = created value
 * @param hiddenData : hide some info of Object in both List $ Form
 */
export interface IJDAModuleParams<T> {
  mode: JDAModuleMode;
  hiddenFields?: (keyof T)[];
  caller?: Modules;
  valueKey?: string;
  value?: T;
}
export interface IJDAModuleAPI<T> extends ReturnType<typeof useRouter> {
  ListView: React.ReactNode;
  FormView: React.ReactNode;
  moduleConfig: IJDAModuleConfig<T>;
  mode: JDAModuleMode;
}

export interface IJDAModuleConfig<T, SubT = T> {
  primaryKey: keyof T;
  route: Modules;
  apiResource: string;
  moduleName: string;
  fieldLabel: Record<keyof T, string>;
  quickRender: (v?: SubT) => string;
  apiConfig?: IAPIConfig<T, any, any, any>;
}

export interface IJDAModuleControllerProps<T>
  extends IJDAModuleAPI<T>,
    NativeStackScreenProps<any> {} // reversed for other logic

export function withModuleController<
  T,
  ListProps extends IJDAListControllerProps<T>,
  FormProps extends IJDAFormControlerProps<T>,
  P extends IJDAModuleControllerProps<T>,
  SubT = T,
>(
  Component: ComponentType<P>,
  ListView: JDAControlledListComponent<T, ListProps>,
  FormView: JDAControlledFormComponent<T, FormProps>,
  moduleConfig: IJDAModuleConfig<T, SubT>,
) {
  return (props: Omit<P, keyof IJDAModuleAPI<T>>) => {
    const listRef = React.useRef<IJDAListRef<T>>();
    const formRef = React.useRef<IJDAFormRef<T>>();
    const {ModuleParams} = React.useContext(JDARouterContext);
    // const moduleStateHandler = useModuleStateReducer<T>(listRef, formRef);
    const listHandler = useListHandler(moduleConfig, listRef);
    const formHandler = useFormHandler(moduleConfig, listRef, formRef);
    ///////// Render

    return (
      <Component
        {...(props as P)}
        mode={ModuleParams?.moduleParams?.mode ?? JDAModuleMode.VIEW_LIST_ITEM}
        moduleConfig={moduleConfig}
        ListView={<ListView {...(listHandler as any)} ref={listRef} />}
        FormView={
          <FormView
            {...(formHandler as any)}
            hideModuleInputs={[ModuleParams?.moduleParams.caller]}
            ref={formRef}
          />
        }
      />
    );
  };
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars, prettier/prettier
export type JDAControlledModuleComponent<
  T,
  P extends IJDAModuleControllerProps<T>,
  // eslint-disable-next-line no-undef
> = (props: Omit<P, keyof IJDAModuleAPI<T>>) => JSX.Element;
