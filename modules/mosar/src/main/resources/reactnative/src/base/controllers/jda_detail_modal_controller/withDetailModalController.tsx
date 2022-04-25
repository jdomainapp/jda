import * as React from 'react';
import {useCallback, useState} from 'react';
import {useAPI} from '../jda_apis/useAPI';
import {IJDAModuleConfig} from '../jda_module_controller/withModuleController';

export interface IDetailModalAPI<T> {
  data?: T;
  loading: boolean;
}

export interface IDetailModalControllerProps<T> extends IDetailModalAPI<T> {
  initData?: T;
  id: T[keyof T];
}

export function withDetailModalController<
  T,
  P extends IDetailModalControllerProps<T>,
>(Component: React.ComponentType<P>, moduleConfig: IJDAModuleConfig<T>) {
  return (props: Omit<P, keyof IDetailModalAPI<T>>) => {
    const [loading, setloading] = useState(false);
    const api = useAPI<T>(moduleConfig.apiResource);
    const [data, setdata] = React.useState<T>();

    const loadData = useCallback(async () => {
      if (props.initData) return;
      setloading(true);
      const res = await api.getById(props.id as any);
      if (res.success) setdata(res.payload);
      setloading(false);
    }, [api, props.id, props.initData]);

    React.useEffect(() => {
      loadData();
    }, [loadData]);
    return <Component {...(props as P)} data={data} loading={loading} />;
  };
}
