import * as React from 'react';
import {useAPI} from '../../../common_hooks/useAPI';
import {IJDAModuleConfig} from '../../jda_module_controller/withModuleController';

export function useModuleInputAPI<T>(ModuleConfig: IJDAModuleConfig<T>) {
  const api = useAPI<T>(ModuleConfig.apiResource);
  const [options, setOptions] = React.useState<T[]>([]);

  const search = React.useCallback(async () => {
    const res = await api.getByPage(0);
    if (res.success && res.payload.content) {
      setOptions(res.payload.content);
    } else setOptions([]);
  }, [api]);

  const getTypedObject = React.useCallback(
    async (obj: T) => {
      console.log('OBJ', obj);

      console.log('ID', ModuleConfig.primaryKey);

      const res = await api.getById(obj[ModuleConfig.primaryKey]);
      if (res.success && res.payload) {
        return res.payload;
      }
    },
    [ModuleConfig.primaryKey, api],
  );

  React.useEffect(() => {
    search();
  }, []);
  return {options, search, getTypedObject};
}
