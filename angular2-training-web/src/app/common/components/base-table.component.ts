import {BaseEntity} from "../entities/baseEntity";
import {OnInit, OnDestroy} from "@angular/core";
import {EntityService, BaseEntityServiceImpl} from "../services/entity/base-entity.service";
import {Router, ActivatedRoute} from "@angular/router";
import {BaseDomainTemplate} from "../templates/baseDomain.template";
import {DomainMetadata} from "../templates/form-metadata";
import {Subject} from "rxjs";
import {PaginationModel} from "../ui/pagination/pagination-model";

const PAGE_QUERY_PARAM = "page";

export abstract class BaseTableComponent<E extends BaseEntity, S extends BaseEntityServiceImpl<E>, T extends BaseDomainTemplate<E>>
  implements OnInit, OnDestroy {

  public itemsPerPage = 15;

  public page: number;

  public total: number;

  public data: E[];

  public urlTable: string;

  public pgnStartItems = 5;

  public pgnEndItems = 5;

  public metadataArray: DomainMetadata[];

  // public paginationModelObs: Observable<PaginationModel>;

  public paginationModelObs: Subject<PaginationModel>;

  protected sub: any;

  constructor(protected crudService: S,
              protected router: Router,
              protected route: ActivatedRoute,
              protected domainTemplate: T) {
    // this.page = 1;
    // this.total = 0;
    this.urlTable = this.domainTemplate.getTableUrl();
    // defer operator use just for postpone of data load until some subscriber is registered
    // this.paginationModelObs = Observable.defer(() => this.getAll());
    this.paginationModelObs = new Subject<PaginationModel>();
  }

  ngOnInit() {
    console.debug('ngOnInit() ->');
    this.metadataArray = this.domainTemplate.metadataArray;
    this.getAll();
  }

  /**
   * Get Query param from the url
   * @param params - url params
   * @return {number}
   */
  protected getQueryParams(params: any): number {
    if (params[PAGE_QUERY_PARAM]) {
      let pageQueryParam = +params[PAGE_QUERY_PARAM];
      if (isNaN(pageQueryParam)) {
        pageQueryParam = 1;
      } else {
        if (pageQueryParam < 0) {
          pageQueryParam = 1;
        }
      }
      return pageQueryParam;
    } else {
      return 1;
    }
  }

  getValue(key: string, item: E): any {
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
  getAll(): void {
    console.debug('getAll() ->');
    this.route.queryParams.map(params => {
      let page = this.getQueryParams(params);
      this.page = page;
      console.debug('BaseTableComponent -> params: ', this.page);
      return page;
    }).flatMap(page => {
      return this.crudService.getAllPagination(page, this.itemsPerPage)
    }).map(data => {
      this.total = data.size;
      this.itemsPerPage = data.sizePerPage;
      this.data = data.result;
      return new PaginationModel(this.itemsPerPage, this.page, this.total, this.urlTable);
    }).subscribe(
      model => this.paginationModelObs.next(model),
      error => console.error('getAll() -> error', error));
  }

  /**
   * edit detail of entity -> redirect to detail of entity
   * @param entity
   */
  edit(entity: E): void {
    console.debug('edit() ->');
    this.router.navigate([this.domainTemplate.getDetailUrl(), entity.id]);
  }

  /**
   * Navigate to the detail
   */
  addNew(): void {
    console.debug('addNew() ->');
    this.router.navigate([this.domainTemplate.getDetailUrl()]);
  }

  /**
   * remove/delete entity from table
   * @param entity
   */
  remove(entity: E): void {
    console.debug('remove() ->');
    this.crudService.delete(entity).subscribe(success => {
      console.debug("remove() -> success");
      this.getAll();
    });
  }

  ngOnDestroy() {
    console.debug('ngOnDestroy() ->');
    this.paginationModelObs.complete();
  }
}
