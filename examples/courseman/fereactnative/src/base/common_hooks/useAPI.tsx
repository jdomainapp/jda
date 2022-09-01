import axios, {AxiosRequestConfig, AxiosResponse} from 'axios';
import {useCallback} from 'react';
import {BASE_API_URL} from '../../AppConfig';

const axiosConfigs: AxiosRequestConfig = {
  baseURL: BASE_API_URL,
  withCredentials: true,
};
// axios.interceptors.request.use((request) => {
//   console.log('Starting Request', JSON.stringify(request, null, 2));
//   return request;
// });

// axios.interceptors.response.use((response) => {
//   console.log('Response:', JSON.stringify(response.data, null, 2));
//   return response;
// });

export interface IAPIReturn<P> {
  success: boolean;
  payload: P;
  error: IAPIError;
}

export interface IAPIError {
  code: number;
  message?: string;
}

export interface IAPIGetListReturn<T> {
  currentPage?: number;
  pageCount: number;
  content?: T[];
}

export interface IAPIConfig<
  GET_T,
  POST_T = GET_T,
  PUT_T = POST_T,
  PATCH_T = POST_T,
> {
  toPOST?: (object: GET_T) => POST_T;
  toPUT?: (object: GET_T) => PUT_T;
  toPATCH?: (object: GET_T) => PATCH_T;
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

export function useAPI<GET_T, POST_T = GET_T, PUT_T = POST_T, PATCH_T = POST_T>(
  routeName: string,
  apiConfig?: IAPIConfig<GET_T, POST_T, PUT_T, PATCH_T>,
) {
  const create = useCallback(
    async (data: GET_T) => {
      console.log('send create request to server', data);
      const res = await axios.post<POST_T, AxiosResponse<GET_T>>(
        `/${routeName}`,
        apiConfig?.toPOST ? apiConfig.toPOST(data) : data,
        axiosConfigs,
      );
      return res.data;
    },
    [apiConfig, routeName],
  );
  const getById = useCallback(
    async (id: GET_T[keyof GET_T]): Promise<IAPIReturn<GET_T>> => {
      try {
        const result = await axios.get<GET_T, AxiosResponse<GET_T>>(
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
    async (
      _pageNumber: number,
    ): Promise<IAPIReturn<IAPIGetListReturn<GET_T>>> => {
      try {
        const res = await axios.get<
          GET_T,
          AxiosResponse<IAPIGetListReturn<GET_T>>
        >(`/${routeName}`, axiosConfigs);
        return successWithData(res.data);
      } catch (error) {
        console.log(`${routeName} get by page error`, error);
        return failWithError({
          code: 100,
        });
      }
    },
    [routeName],
  );
  const updateById = useCallback(
    async (id: GET_T[keyof GET_T], data: GET_T) => {
      const res = await axios.patch<PUT_T, AxiosResponse<GET_T>>(
        `/${routeName}/${id}`,
        apiConfig?.toPATCH ? apiConfig.toPATCH(data) : data,
        axiosConfigs,
      );
      return successWithData(res.data);
    },
    [apiConfig, routeName],
  );
  const deleteById = useCallback(
    async (id: GET_T[keyof GET_T]) => {
      try {
        await axios.delete<GET_T, AxiosResponse<GET_T>>(
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
