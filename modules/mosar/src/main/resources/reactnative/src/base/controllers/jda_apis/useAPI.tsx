import axios, {AxiosRequestConfig, AxiosResponse} from 'axios';
import {useCallback} from 'react';
import {IAPIError, IAPIGetListReturn, IAPIReturn} from './apiTypes';
const axiosConfigs: AxiosRequestConfig = {
  baseURL: 'http://localhost:8080',
  withCredentials: true,
};

export interface IAPIConfig<T> {
  toPOST?: (object: T) => any;
  toPUT?: (object: T) => any;
  toPATCH?: (object: T) => any;
}

function successWithData<T>(data: T): IAPIReturn<T> {
  return {
    success: true,
    payload: data,
    error: {
      code: 0,
      message: '',
    },
  };
}
function failWithError<T>(err: IAPIError): IAPIReturn<T> {
  return {
    success: false,
    payload: {} as T,
    error: err,
  };
}

export function useAPI<T>(routeName: string, apiConfig?: IAPIConfig<T>) {
  const create = useCallback(
    async (data: T) => {
      console.log('send create request to server', data);
      const res = await axios.post<any, AxiosResponse<T>>(
        `/${routeName}`,
        apiConfig?.toPOST ? apiConfig.toPOST(data) : data,
        axiosConfigs,
      );
      return res.data;
    },
    [apiConfig, routeName],
  );
  const getById = useCallback(
    async (id: T[keyof T]): Promise<IAPIReturn<T>> => {
      try {
        const result = await axios.get<T, AxiosResponse<T>>(
          `/${routeName}/${id}`,
          axiosConfigs,
        );
        return successWithData(result.data);
      } catch (error) {
        return failWithError({
          code: 0,
        });
      }
    },
    [routeName],
  );
  const getByPage = useCallback(
    async (_pageNumber: number): Promise<IAPIReturn<IAPIGetListReturn<T>>> => {
      try {
        const res = await axios.get<T, AxiosResponse<IAPIGetListReturn<T>>>(
          `/${routeName}`,
          axiosConfigs,
        );
        return successWithData(res.data);
      } catch (error) {
        console.log('get by page error', error);
        return failWithError({
          code: 100,
        });
      }
    },
    [routeName],
  );
  const updateById = useCallback(
    async (id: T[keyof T], data: T) => {
      const res = await axios.patch<any, AxiosResponse<T>>(
        `/${routeName}/${id}`,
        apiConfig?.toPATCH ? apiConfig.toPATCH(data) : data,
        axiosConfigs,
      );
      return successWithData(res.data);
    },
    [apiConfig, routeName],
  );
  const deleteById = useCallback(
    async (id: T[keyof T]) => {
      try {
        await axios.delete<T, AxiosResponse<T>>(
          `/${routeName}/${id}`,
          axiosConfigs,
        );
        return successWithData('Deleted');
      } catch (error) {
        console.log('delete error', error);
        return failWithError({
          code: 100,
        });
      }
    },
    [routeName],
  );
  return {
    getById,
    getByPage,
    deleteById,
    updateById,
    create,
  };
}
