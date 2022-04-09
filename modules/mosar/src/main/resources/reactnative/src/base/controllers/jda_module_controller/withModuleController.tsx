import * as React from 'react';
import {ComponentType, useCallback, useEffect, useRef, useState} from 'react';
import {IAPIConfig, useAPI} from '../jda_apis/useAPI';
import {
  IJDAFormControlerProps,
  JDAControlledFormComponent,
  JDAFormMode,
} from '../jda_form_controllers/withFormController';
import {
  IJDAListControllerProps,
  IJDAListRef,
  JDAControlledListComponent,
} from '../jda_list_controllers/hocs/withListController';
export interface IJDAModuleAPI<T> {
  currentView: JDAModuleView;
  ListView: React.ReactNode;
  FormView: React.ReactNode;
  moduleConfig: IJDAModuleConfig<T>;
}

export interface IJDAModuleConfig<T> {
  primaryKey: keyof T;
  apiResource: string;
  moduleName: string;
  fieldLabel: Record<keyof T, string>;
  quickRender: (v?: T) => string;
  apiConfig?: IAPIConfig<T>;
}
export enum JDAModuleView {
  LIST,
  FORM,
}

export interface IJDAModuleControllerProps<T> extends IJDAModuleAPI<T> {} // reversed for other logic

export function withModuleController<
  T,
  // ListItemProps extends IJDAListItemControllerProps<T>,
  ListProps extends IJDAListControllerProps<T>,
  FormProps extends IJDAFormControlerProps<T>,
  P extends IJDAModuleControllerProps<T>,
>(
  Component: ComponentType<P>,
  ListView: JDAControlledListComponent<T, ListProps>,
  FormView: JDAControlledFormComponent<T, FormProps>,
  moduleConfig: IJDAModuleConfig<T>,
) {
  return (props: Omit<P, keyof IJDAModuleAPI<T>>) => {
    const [currentView, setCurrentView] = useState<JDAModuleView>(
      JDAModuleView.LIST,
    );
    const [formMode, setFormMode] = useState<JDAFormMode>(JDAFormMode.CREATE);
    const [formValue, setFormValue] = useState<T | undefined>();
    const api = useAPI<T>(moduleConfig.apiResource, moduleConfig.apiConfig);
    const listRef = useRef<IJDAListRef<T>>();

    /////////// Connect List and Form to API
    const handleFormSubmit = useCallback(
      async (submitedItem: T) => {
        console.log('52', formMode, submitedItem);
        switch (formMode) {
          case JDAFormMode.EDIT: {
            const res = await api.updateById(
              submitedItem[moduleConfig.primaryKey],
              submitedItem,
            );
            if (res.success) {
              setCurrentView(JDAModuleView.LIST);
              listRef.current?.itemsControl.updateItem(res.payload);
            }
            break;
          }
          case JDAFormMode.CREATE: {
            console.log('here');
            const res = await api.create(submitedItem);
            console.log('Create result :', res);
            setCurrentView(JDAModuleView.LIST);
            listRef.current?.itemsControl.addItems([res]);
            break;
          }
          case JDAFormMode.READ_ONLY:
            setCurrentView(JDAModuleView.LIST);
            break;
        }
      },
      [api, formMode],
    );
    const handleAddItem = useCallback(() => {
      setCurrentView(JDAModuleView.FORM);
      setFormMode(JDAFormMode.CREATE);
      setFormValue(undefined);
    }, []);

    const handleEditItem = useCallback((itemToEdit: T) => {
      if (itemToEdit) {
        setCurrentView(JDAModuleView.FORM);
        setFormMode(JDAFormMode.EDIT);
        setFormValue(itemToEdit);
      }
    }, []);

    const handleDeleteItems = useCallback(
      async (_items: T[keyof T][]) => {
        const res = await api.deleteById(_items[0]);
        if (res.success) {
          listRef.current?.itemsControl.deleteItems(_items);
        }
      },
      [api],
    );
    const handleChangePage = useCallback(
      async (page: number) => {
        listRef.current?.setLoading(true);
        const res = await api.getByPage(page);
        if (res.success) {
          const {content, pageCount, currentPage} = res.payload;
          if (content) {
            listRef.current?.itemsControl.resetItems(content);
          }
          if (pageCount) {
            listRef.current?.pageControl.setTotalPage(pageCount);
          }
          if (currentPage) {
            listRef.current?.pageControl.setCurrentPage(currentPage);
          }
        }
        listRef.current?.setLoading(false);
      },
      [api],
    );
    const handleRefresh = () => handleChangePage(0);
    const handleChangePageSize = useCallback((_pageSize: number) => {}, []);
    const handleShowDetail = useCallback((item: T) => {
      // console.log('show detail', item);

      // const itemToShow = listRef.current?.itemsControl.getItemByIndex(index);
      // console.log(itemToShow);

      // if (itemToShow) {
      setCurrentView(JDAModuleView.FORM);
      setFormMode(JDAFormMode.READ_ONLY);
      setFormValue(item);
      // }
    }, []);
    const handleFormCancel = useCallback(() => {
      setCurrentView(JDAModuleView.LIST);
    }, []);
    // auto load page 0 when first render
    useEffect(() => {
      handleChangePage(0);
    }, [handleChangePage]);
    ///////// Render
    return (
      <Component
        {...(props as P)}
        moduleConfig={moduleConfig}
        currentView={currentView}
        ListView={
          <ListView
            ref={listRef}
            onAddItem={handleAddItem}
            onShowDetail={handleShowDetail}
            onEditItem={handleEditItem}
            onRefresh={handleRefresh}
            onDeleteItems={handleDeleteItems}
            onChangePage={handleChangePage}
            onChangePageSize={handleChangePageSize}
            {...({} as any)}
          />
        }
        FormView={
          <FormView
            initValue={formValue}
            mode={formMode}
            onCancel={handleFormCancel}
            onSubmit={handleFormSubmit}
            {...({} as any)}
          />
        }
      />
    );
  };
}

//Export componentType
class TypeUltil<
  T,
  // ListItemProps extends IJDAListItemControllerProps<T>,
  ListProps extends IJDAListControllerProps<T>,
  FormProps extends IJDAFormControlerProps<T>,
  P extends IJDAModuleControllerProps<T>,
> {
  //TODO if you change parammeter of withJDAListController function, you must change parameters of controlled function below
  controlled = (
    Component: ComponentType<P>,
    ListView: JDAControlledListComponent<T, ListProps>,
    FormView: JDAControlledFormComponent<T, FormProps>,
    moduleConfig: IJDAModuleConfig<T>,
  ) =>
    withModuleController<T, ListProps, FormProps, P>(
      Component,
      ListView,
      FormView,
      moduleConfig,
    );
}

export type JDAControlledModuleComponent<
  T,
  // ListItemProps extends IJDAListItemControllerProps<T>,
  ListProps extends IJDAListControllerProps<T>,
  FormProps extends IJDAFormControlerProps<T>,
  P extends IJDAModuleControllerProps<T>,
> = ReturnType<TypeUltil<T, ListProps, FormProps, P>['controlled']>;
