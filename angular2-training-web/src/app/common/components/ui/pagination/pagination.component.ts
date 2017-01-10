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

  @Input() pgnModelOds: Observable<PaginationModel>;

  private pgnModelSbn: Subscription;

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
    this.pgnModelSbn = this.pgnModelOds.subscribe(value => {
      console.debug('PaginationComponent.refresh -> success');
      this.refresh(value);
    }, error => {
      console.error('PaginationComponent.refresh -> error: ', error);
    })
  }

  ngOnDestroy(): void {
    if (!this.pgnModelSbn.closed) {
      this.pgnModelSbn.unsubscribe();
    }
  }

  /**
   * Calculate pagination array for displaying of pagination component
   * @param total - number of all domain entities
   * @param itemPerPage - number per one page
   */
  private calculatePaginationArray(total: number, itemPerPage: number) {
    this.totalPages = Math.round(total / itemPerPage);
    this.pgnStartArray = new Array(this.pgnStartItems);
    for (let i = 0; i < this.pgnStartItems; i++) {
      if (this.page > 1) {
        this.pgnStartArray[i] = (this.page - 1) + i;
      } else {
        this.pgnStartArray[i] = (this.page) + i;
      }
    }

    this.pgnEndArray = new Array(this.pgnEndItems);
    for (let i = 0; i < this.pgnEndItems; i++) {
      this.pgnEndArray[i] = (this.totalPages - i);
    }

    this.pgnEndArray = this.pgnEndArray.reverse()
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
