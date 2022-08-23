import {RouteProp} from '@react-navigation/core';
import {NativeStackScreenProps} from '@react-navigation/native-stack';
import {useCallback, useRef} from 'react';
import {Modules} from '../../../data_types/enums/Modules';
import {
  IJDAModuleParams,
  JDAModuleMode,
} from '../jda_module_controller/withModuleController';

export interface IJDARouteParams<T> {
  prevScreen?: Modules;
  prevScreenParams?: IJDARouteParams<any>;
  moduleParams: IJDAModuleParams<T>;
  goBackData?: any;
}

export function useRouter<T>(
  props: NativeStackScreenProps<any>,
  module?: Modules,
) {
  const focusListener = useRef<(() => void) | null>(null);
  const onFocus = useCallback(
    (callback: () => void) => {
      if (focusListener.current != null)
        props.navigation.removeListener('focus', focusListener.current);
      props.navigation.addListener('focus', callback);
      focusListener.current = callback;
    },
    [props.navigation],
  );
  const ModuleParams = props.route.params as IJDARouteParams<T> | undefined;

  const updateParamOrNavigate = useCallback(
    (moduleParams: Partial<IJDAModuleParams<T>>, moduleName?: Modules) => {
      if (moduleName) {
        props.navigation.push(moduleName, {
          prevScreenParams: {...ModuleParams},
          prevScreen: props.route.name,
          moduleParams: {...moduleParams, caller: module},
        });
      } else {
        props.navigation.setParams({
          prevScreenParams: undefined,
          prevScreen: undefined,
          goBackData: undefined,
          moduleParams: moduleParams,
        });
      }
    },
    [ModuleParams, module, props.navigation, props.route.name],
  );
  const goHome = useCallback(() => {
    props.navigation.popToTop();
  }, [props.navigation]);

  const goToModule = useCallback(
    (moduleName: Modules) => {
      props.navigation.navigate(moduleName);
    },
    [props.navigation],
  );

  const showList = useCallback(
    (moduleName?: Modules) => {
      updateParamOrNavigate(
        {
          mode: JDAModuleMode.VIEW_LIST_ITEM,
        },
        moduleName,
      );
    },
    [updateParamOrNavigate],
  );

  const showCreateForm = useCallback(
    (moduleName?: Modules, options?: Partial<IJDAModuleParams<T>>) => {
      console.log('Show create form, value: ', options?.value);

      updateParamOrNavigate(
        {
          mode: JDAModuleMode.CREATE_ITEM,
          ...options,
        },
        moduleName,
      );
    },
    [updateParamOrNavigate],
  );

  const showEditForm = useCallback(
    (item: T, moduleName?: Modules, options?: Partial<IJDAModuleParams<T>>) => {
      updateParamOrNavigate(
        {
          mode: JDAModuleMode.EDIT_ITEM,
          ...options,
          value: {...item},
        },
        moduleName,
      );
    },
    [updateParamOrNavigate],
  );
  const showDetail = useCallback(
    (item: T, moduleName?: Modules, options?: Partial<IJDAModuleParams<T>>) => {
      updateParamOrNavigate(
        {
          mode: JDAModuleMode.VIEW_ITEM,
          ...options,
          value: {...item},
        },
        moduleName,
      );
    },
    [updateParamOrNavigate],
  );

  const goBack = useCallback(
    (data?: any) => {
      if (ModuleParams?.prevScreen) {
        // console.log('1. go back to ', ModuleParams.prevScreen);
        const params: IJDARouteParams<any> = {
          ...(ModuleParams.prevScreenParams as any),
          goBackData: {
            ...ModuleParams.prevScreenParams?.goBackData,
            [props.route.name]: data,
          },
        };
        props.navigation.navigate(ModuleParams.prevScreen, params);
      } else if (props.navigation.canGoBack()) {
        props.navigation.goBack();
      }
    },
    [
      ModuleParams?.prevScreen,
      ModuleParams?.prevScreenParams,
      props.navigation,
      props.route.name,
    ],
  );

  const getGoBackData = useCallback(
    <D extends unknown>(moduleName: Modules) => {
      const data: D | undefined = ModuleParams?.goBackData?.[moduleName] as any;
      return data;
    },
    [ModuleParams?.goBackData],
  );
  return {
    ModuleParams,
    RouteState: props.route,
    router: {
      goHome,
      goToModule,
      showCreateForm,
      showEditForm,
      showDetail,
      showList,
      goBack,
      onFocus,
      getGoBackData,
    },
  };
}

export type ReturnTypeUseRouter<T> = {
  ModuleParams: IJDARouteParams<T> | undefined;
  RouteState: RouteProp<any, string>;
  router: {
    goHome: () => void;
    goToModule: (moduleName: Modules) => void;
    showCreateForm: (
      moduleName?: Modules,
      options?: Partial<IJDAModuleParams<T>>,
    ) => void;
    showEditForm: (
      item: T,
      moduleName?: Modules,
      options?: Partial<IJDAModuleParams<T>>,
    ) => void;
    showDetail: (
      item: T,
      moduleName?: Modules,
      options?: Partial<IJDAModuleParams<T>>,
    ) => void;
    showList: (moduleName?: Modules) => void;
    goBack: (data?: any) => void;
    onFocus: (callback: () => void) => void;
    getGoBackData: <D extends unknown>(moduleName: Modules) => D | undefined;
  };
};
