import {BaseEntity} from "../entities/baseEntity";
import {OnInit, OnDestroy} from "@angular/core";
import {EntityService} from "../services/entity/base-entity.service";
import {Router, ActivatedRoute}       from '@angular/router';
import {BaseDomainTemplate} from "../templates/baseDomain.template";
import {DomainMetadata} from "../templates/form-metadata";
import {Observable, Subject} from "rxjs";
import {PaginationModel} from "./ui/pagination/pagination-model";

const PAGE_QUERY_PARAM = "page";

export abstract class BaseTableComponent<E extends BaseEntity> implements OnInit, OnDestroy {

  public itemsPerPage = 15;

  public page: number;

  public total: number;

  public data:E[];

  public urlTable: string;

  public pgnStartItems = 5;

  public pgnEndItems = 5;

  public metadataArray: DomainMetadata[];

  public pgnModelSubject: Subject<PaginationModel>;

  protected sub: any;

  constructor(protected crudService:EntityService<E>,
              protected router:Router,
              protected route:ActivatedRoute,
              protected domainTemplate:BaseDomainTemplate<E>) {
    this.pgnModelSubject = new Subject<PaginationModel>();
  }

  ngOnInit() {
    console.debug('ngOnInit() ->');
    this.metadataArray = this.domainTemplate.metadataArray;
    this.page = 1;
    this.total = 0;
    this.urlTable = this.domainTemplate.getTableUrl();
    this.sub = this.route.queryParams.subscribe(
      params => {
        // if (params[PAGE_QUERY_PARAM]) {
        //   let pageQueryParam = +params[PAGE_QUERY_PARAM];
        //   if (isNaN(pageQueryParam)) {
        //     this.page = 1;
        //   } else {
        //     if (pageQueryParam < 0) {
        //       pageQueryParam = 1;
        //     }
        //     this.page = pageQueryParam;
        //   }
        // } else {
        //   this.page = 1;
        // }
        this.page = this.getQueryParams(params);
        this.getAll();
      }
    );
  }

  /**
   * Get Query param from the url
   * @param params - url params
   * @return {number}
   */
  protected getQueryParams(params:any): number {
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

  getPgnModelObs(): Observable<PaginationModel> {
    return this.pgnModelSubject.asObservable();
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
      this.pgnModelSubject.next(new PaginationModel(this.itemsPerPage, this.page, this. total, this.urlTable));
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
    this.pgnModelSubject.complete();
  }
}
