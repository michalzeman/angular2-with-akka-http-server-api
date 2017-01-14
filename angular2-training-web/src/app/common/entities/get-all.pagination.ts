export interface GetAllPagination<E> {
  result: E[];
  page: number;
  sizePerPage: number;
  size: number;
}
