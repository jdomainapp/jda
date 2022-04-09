import React, {useCallback, useState} from 'react';

export interface IJDAPageControl {
  setCurrentPage: React.Dispatch<React.SetStateAction<number>>;
  setItemPerPage: (_itemPerPage: number) => void;
  setTotalPage: React.Dispatch<React.SetStateAction<number>>;
}

export interface IJDAPaging {
  currentPage: number;
  pageSize: number;
  totalPage: number;
  setItemPerPage: (itemPerPage: number) => void;
  nextPage: () => void;
  backPage: () => void;
  goToPage: (page: number) => void;
  goToFirstPage: () => void;
  gotoLastPage: () => void;
}

export function usePageControl(): {
  paging: IJDAPaging;
  pageControl: IJDAPageControl;
} {
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [totalPage, setTotalPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(10);

  //api for wrappedComponent
  const nextPage = useCallback(() => {
    setCurrentPage(Math.min(totalPage, currentPage + 1));
  }, [totalPage, currentPage]);

  const backPage = useCallback(() => {
    setCurrentPage(Math.max(1, currentPage - 1));
  }, [currentPage]);

  const goToPage = useCallback(
    (page: number) => {
      if (page < 1) {
        page = 1;
      }
      if (page > totalPage) {
        page = totalPage;
      }
      setCurrentPage(page);
    },
    [totalPage],
  );

  const goToFirstPage = useCallback(() => {
    setCurrentPage(1);
  }, []);

  const gotoLastPage = useCallback(() => {
    setCurrentPage(totalPage);
  }, [totalPage]);

  const setItemPerPage = useCallback((itemPerPage: number) => {
    setPageSize(Math.max(itemPerPage, 1));
  }, []);

  return {
    paging: {
      currentPage,
      pageSize,
      totalPage,
      setItemPerPage,
      nextPage,
      backPage,
      goToPage,
      goToFirstPage,
      gotoLastPage,
    },
    pageControl: {
      setCurrentPage,
      setItemPerPage,
      setTotalPage,
    },
  };
}
