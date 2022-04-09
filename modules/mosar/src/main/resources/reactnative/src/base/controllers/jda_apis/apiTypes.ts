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
