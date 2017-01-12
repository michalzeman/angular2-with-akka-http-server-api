export class PaginationModel {

  constructor(public itemsPerPage: number,
              public page: number,
              public total: number,
              public urlTable: string) {
  }
}
