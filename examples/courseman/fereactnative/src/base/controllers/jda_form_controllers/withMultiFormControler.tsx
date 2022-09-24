import * as React from 'react';
import {useCallback, useMemo, useState} from 'react';
import {
  IJDAFormAPI,
  IJDAFormControlerProps,
  JDAControlledFormComponent,
} from './withFormController';

interface IJDAGenericFormAPI<T> extends IJDAFormAPI {
  formType: string;
  onChangeFormType: (t?: string) => void;
  formTypes: string[];
  FormView: JDAControlledFormComponent<T, any>;
}

export interface IJDAMultiFormControllerProps
  extends IJDAGenericFormAPI<any>,
    IJDAFormControlerProps<any> {}

export function withJDAMultiFormController<
  P extends IJDAMultiFormControllerProps,
>(
  Component: React.ComponentType<P>,
  forms: {
    type: string;
    formComponent: JDAControlledFormComponent<any, any>;
  }[],
) {
  return (props: Omit<P, keyof IJDAGenericFormAPI<any>>) => {
    const [formType, setFormType] = useState<string>(forms[0].type);
    const beforeSubmit = useCallback(
      (formValue: any) => {
        props.onSubmit({type: formType, ...formValue});
      },
      [formType, props],
    );
    const FormView = useMemo(() => {
      const FView = forms.find((f) => f.type === formType)?.formComponent;
      return FView ? (
        <FView {...(props as any)} onSubmit={beforeSubmit} />
      ) : (
        <></>
      );
    }, [beforeSubmit, formType, props]);
    return (
      <Component
        {...(props as P)}
        formTypes={forms.map((f) => f.type)}
        formType={formType}
        FormView={FormView}
        onChangeFormType={setFormType}
      />
    );
  };
}
