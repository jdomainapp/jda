import * as React from 'react';
import {useCallback, useEffect} from 'react';
import {useAPI} from '../../../common_hooks/useAPI';
import {useTypedContext} from '../../../common_hooks/useTypedContext';
import {
  IJDAFormRef,
  JDAFormMode,
} from '../../jda_form_controllers/withFormController';
import {IJDAListRef} from '../../jda_list_controllers/hocs/withJDAListController';
import {
  IJDARouterContext,
  JDARouterContext,
} from '../../jda_router/JDARouterContext';
import {IJDAModuleConfig, JDAModuleMode} from '../withModuleController';

const FormModeMaping: Record<JDAModuleMode, JDAFormMode> = {
  [JDAModuleMode.CREATE_ITEM]: JDAFormMode.CREATE,
  [JDAModuleMode.VIEW_ITEM]: JDAFormMode.READ_ONLY,
  [JDAModuleMode.EDIT_ITEM]: JDAFormMode.EDIT,
  [JDAModuleMode.VIEW_LIST_ITEM]: JDAFormMode.CREATE,
};

export function useFormHandler<T, SubT>(
  moduleConfig: IJDAModuleConfig<T, SubT>,
  listRef: React.MutableRefObject<IJDAListRef<T> | undefined>,
  formRef: React.MutableRefObject<IJDAFormRef<T> | undefined>,
) {
  const {ModuleParams, router} =
    useTypedContext<IJDARouterContext<T>>(JDARouterContext);
  const api = useAPI<T>(moduleConfig.apiResource);
  useEffect(() => {
    console.log(
      'Try to set form value from Module controller:',
      ModuleParams?.moduleParams.value,
    );

    if (true) {
      const data = ModuleParams?.moduleParams?.value;
      if (data?.[moduleConfig.primaryKey]) {
        //try to update newest object from server
        formRef?.current?.setLoading(true);
        api
          .getById(data[moduleConfig.primaryKey])
          .then((r) => {
            formRef.current?.setFormValue({...r.payload, ...data});
          })
          .catch((_e) => {
            formRef.current?.setFormValue(data);
          })
          .finally(() => {
            formRef.current?.setLoading(false);
          });
      } else {
        console.log('Set raw data', data);

        formRef.current?.setFormValue(data);
      }
    }
  }, [
    ModuleParams?.moduleParams.mode,
    ModuleParams?.moduleParams.value,
    api,
    formRef,
    moduleConfig.primaryKey,
  ]);

  const showListOrGoBack = useCallback(
    (item?: T) => {
      if (ModuleParams?.prevScreen) {
        router.goBack(item);
      } else {
        router.showList();
      }
    },
    [ModuleParams?.prevScreen, router],
  );
  const handleFormCancel = React.useCallback(() => {
    if (ModuleParams?.prevScreen) {
      router.goBack();
    } else {
      router.showList();
    }
  }, [ModuleParams?.prevScreen, router]);
  const handleFormSubmit = React.useCallback(
    async (submitedItem: T) => {
      switch (ModuleParams?.moduleParams.mode) {
        case JDAModuleMode.EDIT_ITEM: {
          const res = await api.updateById(
            submitedItem[moduleConfig.primaryKey],
            submitedItem,
          );
          if (res.success) {
            listRef.current?.itemsControl.updateItem(res.payload);
            showListOrGoBack(res.payload);
          }
          break;
        }
        case JDAModuleMode.CREATE_ITEM: {
          const res = await api.create(submitedItem);
          listRef.current?.itemsControl.addItems([res]);
          showListOrGoBack(res);
          break;
        }
        case JDAModuleMode.VIEW_ITEM:
          showListOrGoBack(submitedItem);
          break;
      }
    },
    [
      ModuleParams?.moduleParams.mode,
      api,
      listRef,
      moduleConfig.primaryKey,
      showListOrGoBack,
    ],
  );

  return {
    mode: FormModeMaping[
      ModuleParams?.moduleParams?.mode || JDAModuleMode.VIEW_LIST_ITEM
    ],
    onCancel: handleFormCancel,
    onSubmit: handleFormSubmit,
  };
}
