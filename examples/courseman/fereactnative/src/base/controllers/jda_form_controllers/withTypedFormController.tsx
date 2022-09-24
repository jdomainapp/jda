import * as React from 'react';
import {useCallback, useMemo, useState} from 'react';
import {
  IJDAFormAPI,
  IJDAFormControlerProps,
  IJDAFormRef,
  JDAControlledFormComponent,
} from './withFormController';

interface IJDATypedFormAPI<T> extends IJDAFormAPI {
  formType: string;
  onChangeFormType: (t?: string) => void;
  formTypes: string[];
  FormView: JDAControlledFormComponent<T, any>;
}
export interface ITypedFormItem {
  type: string;
  formComponent: JDAControlledFormComponent<any, any>;
}
export interface IJDAMultiFormControllerProps
  extends IJDATypedFormAPI<any>,
    IJDAFormControlerProps<any> {}

export function withJDATypedFormController<
  P extends IJDAMultiFormControllerProps,
>(Component: React.ComponentType<P>, forms: ITypedFormItem[]) {
  return React.forwardRef<
    IJDAFormRef<any>,
    Omit<P, keyof IJDATypedFormAPI<any>>
  >((props, ref) => {
    const fwRef = React.useRef<IJDAFormRef<any>>();
    const [formType, setFormType] = useState<string>(forms[0].type);
    const [formValue, setFormValue] = useState();
    const setFormValueWithType = useCallback((value?: any) => {
      if (value?.type) {
        setFormType(value.type);
      }
      setFormValue(value);
    }, []);
    React.useImperativeHandle(ref, () => ({
      setMode: (mode) => fwRef.current?.setMode?.(mode),
      setLoading: (value: boolean) => fwRef.current?.setLoading?.(value),
      setFormValue: (v) => setFormValueWithType(v),
    }));
    const beforeSubmit = useCallback(
      (value: any) => {
        console.log('Go to before submit in typed form ');

        props.onSubmit({type: formType, ...value});
      },
      [formType, props],
    );
    const FormView = useMemo(() => {
      const FView = forms.find((f) => f.type === formType)?.formComponent;
      return FView ? (
        <FView
          {...(props as any)}
          ref={fwRef}
          onSubmit={beforeSubmit}
          initValue={formValue}
        />
      ) : (
        <></>
      );
    }, [beforeSubmit, formType, formValue, props]);
    return (
      <Component
        {...(props as P)}
        formTypes={forms.map((f) => f.type)}
        formType={formType}
        FormView={FormView}
        onChangeFormType={setFormType}
      />
    );
  });
}
