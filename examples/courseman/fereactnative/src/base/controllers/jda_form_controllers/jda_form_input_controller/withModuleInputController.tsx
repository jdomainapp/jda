import _ from 'lodash';
import * as React from 'react';
import {useCallback, useEffect, useMemo} from 'react';
import {Controller, useFormContext, useWatch} from 'react-hook-form';
import {IJDAModuleInput} from '.';
import {Modules} from '../../../../data_types/enums/Modules';
import {IJDAModuleConfig} from '../../jda_module_controller/withModuleController';
import {JDARouterContext} from '../../jda_router/JDARouterContext';
import {useModuleInputAPI} from './useModuleInputAPI';
import {
  IJDAFormInputAPI,
  IJDAFormInputControllerProps,
} from './withFormInputController';

export interface IJDAModuleInputAPI<T>
  extends IJDAFormInputAPI<T>,
    IJDAModuleInput<T> {}

export interface IJDAModuleInputControllerProps<T>
  extends IJDAFormInputControllerProps<T> {
  module: Modules;
  associateField?: string;
  associateCollection?: string;
}

export function withModuleInputController<
  T,
  P extends IJDAModuleInputControllerProps<T>,
>(Component: React.ComponentType<P>, moduleConfig: IJDAModuleConfig<T>) {
  return (props: Omit<P, keyof IJDAFormInputAPI<T>>) => {
    const {options, search, getTypedObject} = useModuleInputAPI(moduleConfig);
    const {router} = React.useContext(JDARouterContext);

    const {control, setValue, watch} = useFormContext<T>();
    const formValue = watch();
    const _currentValue = useWatch({
      control: control,
      name: props.name,
    });
    const moduleValue = useMemo(() => {
      const value = _.cloneDeep(_.omit(formValue as any, [props.name]));
      const linkedFieldValue = props.associateField
        ? {[props.associateField as keyof T]: value}
        : {};
      return {
        ..._currentValue,
        ...linkedFieldValue,
      };
    }, [_currentValue, formValue, props.associateField, props.name]);
    useEffect(() => {
      console.log(`Currrent value, ${props.name}  :   `, _currentValue);
    }, [_currentValue, props.name]);
    const onUnlink = useCallback(() => {
      setValue(props.name, undefined as any);
    }, [props.name, setValue]);
    const onCreate = useCallback(async () => {
      router.showCreateForm(props.module, {
        value: moduleValue,
      });
    }, [moduleValue, props.module, router]);

    const onEdit = useCallback(async () => {
      router.showEditForm(moduleValue, props.module);
    }, [router, moduleValue, props.module]);

    const onShowDetail = useCallback(async () => {
      console.log('Show detail for ', moduleValue);
      router.showDetail(moduleValue, props.module);
    }, [router, moduleValue, props.module]);

    useEffect(() => {
      //Try to update value if goBackData Change
      const value = router.getGoBackData<T>(props.module);
      if (value) setValue(props.name, value as any);
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [router.getGoBackData]);

    return (
      <Controller
        name={props.name}
        control={control}
        rules={props.rules}
        render={(item) => (
          <Component
            {...(props as P)}
            {...item}
            value={item.field.value}
            onChange={(value: T) => {
              getTypedObject(value)
                .then((r) => item.field.onChange(r))
                .catch((e) => console.log(e));
            }}
            onCreate={onCreate}
            onEdit={onEdit}
            onShowDetail={onShowDetail}
            onSearch={search}
            onUnlink={onUnlink}
            options={options}
          />
        )}
      />
    );
  };
}

export type JDAControlledModuleInputComponent<
  T,
  P extends IJDAModuleInputControllerProps<T>,
  // eslint-disable-next-line no-undef
> = (props: Omit<P, keyof IJDAFormInputAPI<T>>) => JSX.Element;
