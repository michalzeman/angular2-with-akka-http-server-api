import {Component, Input, OnInit, OnDestroy} from "@angular/core";
import {Observable, Subscription} from "rxjs";
import {PaginationModel} from "./pagination-model";


@Component({
  selector: 'pagination',
  templateUrl: './pagination.html',
})
export class PaginationComponent implements OnInit, OnDestroy {

  @Input() public pgnStartItems;

  @Input() public pgnEndItems;

  @Input() public pgnModelOds: Observable<PaginationModel>;

  public totalPages: number;

  public pgnStartArray: number[];

  public pgnEndArray: number[];

  public itemsPerPage: number;

  public page: number;

  public total: number;

  public urlTable: string;


  constructor() {
  }

  ngOnInit(): void {
    this.pgnModelOds.subscribe(value => {
      console.debug('PaginationComponent.refresh -> success');
      this.refresh(value);
    }, error => {
      console.error('PaginationComponent.refresh -> error: ', error);
    })
  }

  ngOnDestroy(): void {

  }

  /**
   * Calculate pagination array for displaying of pagination component
   * @param total - number of all domain entities
   * @param itemPerPage - number per one page
   */
  private calculatePaginationArray(total: number, itemPerPage: number) {
    this.totalPages = Math.round(total / itemPerPage);
    this.pgnStartArray = this.initPgnStartArray(this.pgnStartItems, this.page);

    this.pgnEndArray = this.initPgnEndArray(this.pgnEndItems, this.totalPages);
  }

  /**
   * init start items for pagination
   * @return {number[]}
   */
  private initPgnStartArray(pgnStartItems: number, page: number): number[] {
    let pgnStartArray = new Array(pgnStartItems);
    for (let i = 0; i < pgnStartItems; i++) {
      if (page > 1) {
        pgnStartArray[i] = (page - 1) + i;
      } else {
        pgnStartArray[i] = (page) + i;
      }
    }
    return pgnStartArray.filter(item => item > 0);
  }

  /**
   * Init end items for pagination
   * @return {number[]}
   */
  private initPgnEndArray(pgnEndItems: number, totalPages: number): number[] {
    let pgnEndArray = new Array(pgnEndItems);
    for (let i = 0; i < pgnEndItems; i++) {
      pgnEndArray[i] = (totalPages - i);
    }
    return pgnEndArray.filter(item => item > 0).reverse();
  }

  protected refresh(pgnModel: PaginationModel): void {
    this.page = pgnModel.page;
    this.itemsPerPage = pgnModel.itemsPerPage;
    this.total = pgnModel.total;
    this.urlTable = pgnModel.urlTable;
    this.calculatePaginationArray(this.total, this.itemsPerPage);
  }

  public isOnEnd(): boolean {
    // let result = (this.page >= this.pgnEndArray[0]) && (this.pgnStartArray[this.pgnStartItems - 1] < this.pgnEndArray[0]);
    let result = (this.page >= this.pgnEndArray[0]);
    return result;
  }

  public isActive(item:number): string {
    return (this.page == item)? 'active':'';
  }

}
