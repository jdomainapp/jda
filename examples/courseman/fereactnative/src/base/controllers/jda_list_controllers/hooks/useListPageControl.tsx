import React, {useCallback, useState} from 'react';

export interface IJDAPageControl {
  setCurrentPage: React.Dispatch<React.SetStateAction<number>>;
  setTotalPage: React.Dispatch<React.SetStateAction<number>>;
  setItemPerPage: (itemPerPage: number) => void;
  nextPage: () => void;
  backPage: () => void;
  goToPage: (page: number) => void;
  goToFirstPage: () => void;
  gotoLastPage: () => void;
}

export interface IJDAPaging {
  currentPage: number;
  pageSize: number;
  totalPage: number;
}

export function usePageControl(props: any): {
  paging: IJDAPaging;
  pageControl: IJDAPageControl;
} {
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [totalPage, setTotalPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(10);

  //api for wrappedComponent
  const nextPage = useCallback(() => {
    props.onChangePage?.(Math.min(totalPage, currentPage + 1));
    setCurrentPage(Math.min(totalPage, currentPage + 1));
  }, [props, totalPage, currentPage]);

  const backPage = useCallback(() => {
    props.onChangePage?.(Math.max(1, currentPage - 1));
    setCurrentPage(Math.max(1, currentPage - 1));
  }, [currentPage, props]);

  const goToPage = useCallback(
    (page: number) => {
      if (page < 1) {
        page = 1;
      }
      if (page > totalPage) {
        page = totalPage;
      }
      props.onChangePage?.(page);
      setCurrentPage(page);
    },
    [totalPage, props],
  );

  const goToFirstPage = useCallback(() => {
    props.onChangePage?.(1);
    setCurrentPage(1);
  }, [props]);

  const gotoLastPage = useCallback(() => {
    props.onChangePage?.(totalPage);
    setCurrentPage(totalPage);
  }, [props, totalPage]);

  const setItemPerPage = useCallback(
    (itemPerPage: number) => {
      props.onChangePageSize?.(Math.max(itemPerPage, 1));
      setPageSize(Math.max(itemPerPage, 1));
    },
    [props],
  );

  return {
    paging: {
      currentPage,
      pageSize,
      totalPage,
    },
    pageControl: {
      setCurrentPage,
      setItemPerPage,
      setTotalPage,
      nextPage,
      backPage,
      goToPage,
      goToFirstPage,
      gotoLastPage,
    },
  };
}
