import _ from 'lodash';
import * as React from 'react';
import {Controller, useFormContext} from 'react-hook-form';
import {IJDAMultiModuleInput} from '.';
import {Modules} from '../../../../data_types/enums/Modules';
import {IJDAModuleConfig} from '../../jda_module_controller/withModuleController';
import {JDARouterContext} from '../../jda_router/JDARouterContext';
import {useModuleInputAPI} from './useModuleInputAPI';
interface IJDAFormMultiInputAPI<T>
  extends Omit<IJDAMultiModuleInput<T>, 'disabled'> {
  onAppend: (v: T) => void;
  onRemove: (v: T) => void;
  onUpdate: (v: T) => void;
}
export interface IJDAModuleMultiInputControllerProps<T>
  extends IJDAFormMultiInputAPI<T> {
  name: keyof any;
  label: string;
  disabled?: boolean;
  module: Modules;
  associateField?: string;
  associateCollection?: string;
}

export function withJDAModuleMultiInputController<
  T,
  Props extends IJDAModuleMultiInputControllerProps<T>,
>(Component: React.ComponentType<Props>, moduleConfig: IJDAModuleConfig<T>) {
  return (props: Omit<Props, keyof IJDAFormMultiInputAPI<T>>) => {
    const {options, search, getTypedObject} = useModuleInputAPI(moduleConfig);
    const {control, setValue, getValues} = useFormContext<any>();

    const {router} = React.useContext(JDARouterContext);
    const getCurrentModuleValue = React.useCallback(() => {
      const value = _.cloneDeep(_.omit(getValues() as any, [props.name]));
      const linkedCollectionValue = props.associateCollection
        ? {
            [props.associateCollection as any]: [value],
          }
        : props.associateField
        ? {
            [props.associateField as any]: value,
          }
        : {};
      return {
        ...linkedCollectionValue,
      };
    }, [
      getValues,
      props.associateCollection,
      props.associateField,
      props.name,
    ]);

    const onCreate = React.useCallback(async () => {
      router.showCreateForm(props.module, {
        value: getCurrentModuleValue(),
      });
    }, [getCurrentModuleValue, props.module, router]);

    const onEdit = React.useCallback(
      async (value: T) => {
        router.showEditForm(value, props.module, {
          value: getCurrentModuleValue(),
        });
      },
      [router, props.module, getCurrentModuleValue],
    );

    const onShowDetail = React.useCallback(
      async (value: T) => {
        router.showDetail(value, props.module);
      },
      [router, props.module],
    );

    React.useEffect(() => {
      //Try to update value if goBackData Change
      const value = router.getGoBackData<T>(props.module);
      console.log('Goback data', value);

      if (value) {
        let current_value: T[] = getValues(props.name as any) ?? [];
        let index = current_value.findIndex(
          (i) => i[moduleConfig.primaryKey] === value[moduleConfig.primaryKey],
        );
        if (index >= 0) {
          current_value = [
            ...current_value.slice(0, index),
            value,
            ...current_value.slice(index + 1),
          ];
        } else {
          current_value = [...current_value, value];
        }
        setValue(props.name as any, current_value);
      }
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [router.getGoBackData]);

    return (
      <Controller
        control={control}
        name={props.name as any}
        render={({field}) => {
          console.log(`Field Value for ${props.name as any} `, field.value);

          return (
            <Component
              {...(props as Props)}
              values={field.value ?? []}
              onShowDetail={onShowDetail}
              onEdit={onEdit}
              onCreate={onCreate}
              onAppend={(value: T) => {
                if (
                  !field.value ||
                  !field.value.find(
                    (e: T) =>
                      e[moduleConfig.primaryKey] ===
                      value[moduleConfig.primaryKey],
                  )
                )
                  getTypedObject(value)
                    .then((r) => {
                      field.onChange([...(field.value ?? []), r]);
                    })
                    .catch((e) => console.log(e));
              }}
              onSearch={search}
              onRemove={(v) => {
                const values = (field.value ?? []).filter(
                  (i: T) =>
                    i[moduleConfig.primaryKey] !== v[moduleConfig.primaryKey],
                );
                field.onChange(values);
              }}
              options={options}
            />
          );
        }}
      />
    );
  };
}

export type JDAControlledFormMultiInputComponent<
  T,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  Props extends IJDAModuleMultiInputControllerProps<T>,
  // eslint-disable-next-line prettier/prettier, no-undef
> = (props: Omit<Props, keyof IJDAFormMultiInputAPI<T>>) => JSX.Element;
