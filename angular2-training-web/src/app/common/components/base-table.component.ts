import {BaseEntity} from "../entities/baseEntity";
import {OnInit, OnDestroy} from "@angular/core";
import {EntityService} from "../services/entity/base-entity.service";
import {Router, ActivatedRoute}       from '@angular/router';
import {BaseDomainTemplate} from "../templates/baseDomain.template";
import {DomainMetadata} from "../templates/form-metadata";

const PAGE_QUERY_PARAM = "page";

export abstract class BaseTableComponent<E extends BaseEntity> implements OnInit, OnDestroy {

  public itemsPerPage: number;

  public page: number;

  public total: number;

  public data:E[];

  public urlTable: string;

  public paginationArray: number[];

  public metadataArray: DomainMetadata[];

  protected sub: any;

  constructor(protected crudService:EntityService<E>,
              protected router:Router,
              protected route:ActivatedRoute,
              protected domainTemplate:BaseDomainTemplate<E>) {
  }

  ngOnInit() {
    console.debug('ngOnInit() ->');
    this.metadataArray = this.domainTemplate.metadataArray;
    this.page = 1;
    this.itemsPerPage = 25;
    this.total = 0;
    this.calculatePaginationArray(this.total, this.itemsPerPage);
    this.urlTable = this.domainTemplate.getTableUrl();
    this.sub = this.route.queryParams.subscribe(
      params => {
        if (params[PAGE_QUERY_PARAM]) {
          this.page = +params[PAGE_QUERY_PARAM];
        } else {
          this.page = 1
        }
        this.getAll();
      }
    );
  }

  /**
   * Calculate pagination array for displaying of pagination component
   * @param total - number of all domain entities
   * @param itemPerPage - number per one page
   */
  private calculatePaginationArray(total: number, itemPerPage:number) {
    this.paginationArray = new Array(Math.round(total / itemPerPage));
  }

  getValue(key:string, item:E):any {
    if (key && item) {
      let splitted = key.split(".", 3);
      if (splitted.length > 1) {
        let value = item;
        for (let i = 0; i < splitted.length; i++) {
          value = value[splitted[i]];
          if (!value) {
            break;
          }
        }
        return value;
      } else {
        return item[key];
      }
    }
  }

  getTitle(): string {
    return this.domainTemplate.getTableTitle();
  }

  /**
   * Get list of domain objects
   * TODO: it would be better to implement pagination for real implementation!!!
   */
  getAll():void {
    console.debug('getAll() ->');
    this.crudService.getAllPagination(this.page, this.itemsPerPage).subscribe(data => {
      this.total = data.size;
      this.itemsPerPage = data.sizePerPage;
      this.data = data.result;
      this.calculatePaginationArray(this.total, this.itemsPerPage);
    });
  }

  /**
   * edit detail of entity -> redirect to detail of entity
   * @param entity
   */
  edit(entity:E):void {
    console.debug('edit() ->');
    this.router.navigate([this.domainTemplate.getDetailUrl(), entity.id]);
  }

  /**
   * Navigate to the detail
   */
  addNew():void {
    console.debug('addNew() ->');
    this.router.navigate([this.domainTemplate.getDetailUrl()]);
  }

  /**
   * remove/delete entity from table
   * @param entity
   */
  remove(entity:E):void {
    console.debug('remove() ->');
    this.crudService.delete(entity).subscribe(success => this.getAll())
  }

  ngOnDestroy() {
    console.debug('ngOnDestroy() ->');
  }
}
