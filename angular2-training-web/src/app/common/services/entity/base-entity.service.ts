import {Http, URLSearchParams, Response, RequestOptions} from "@angular/http";
import "../../../rxjs-operators";
import {BaseEntity} from "../../entities/baseEntity";
import {DelegateService} from "../delegate.service";
import {Observable} from "rxjs/Rx";
import {AlertMessage, ALERT_TYPE_DANGER} from "../../../domains/alert/alert-message";
import {GetAllPagination} from "../../entities/get-all.pagination";

export interface ErrorResponse {
  data: string,
  status: number,
  config: Object,
  statusText: string
}

export interface EntityService<E extends BaseEntity> {
  /**
   * Find Entity by id
   * @param id
   */
  get(id: number): Observable<E>;

  /**
   * delete entity by id
   * @param id
   */
  delete(entity: E): Observable<{}>;

  /**
   * update entity
   * @param entity
   */
  update(entity: E): Observable<E>;

  /**
   * get all
   */
  getAll(): Observable<E[]>;

  /**
   * Get all by paging
   * @param page - current page
   * @param items - items per page
   */
  getAllPagination(page: number, items: number): Observable<GetAllPagination<E>>;

  /**
   * save entity
   * @param entity
   */
  save(entity: E): Observable<E>;
}

export abstract class BaseEntityServiceImpl<E extends BaseEntity> implements EntityService<E> {

  protected url: string;

  constructor(protected delegateService: DelegateService,
              protected http: Http,
              url: string) {
    this.url = '/api' + url;
  }

  protected extractData(res: Response) {
    // console.log('Crud service response',res);
    let body = res.json();
    // console.log('Crud service response.body',body);
    // return body.data | {};
    if (body) {
      return body;
    }
    else {
      return {};
    }
  }

  protected handleError(error: Response) {
    // In a real world app, we might use a remote logging infrastructure
    // We'd also dig deeper into the error to get a better message
    // let errMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';

    console.error(error); // log to console instead
    let status = error.status;
    switch (error.status) {
      case 500:
        let errMsg = error.text();
        this.delegateService.emitAlert(new AlertMessage(errMsg.toString(), ALERT_TYPE_DANGER));
        break;
      default:
        this.delegateService.emitAlert(new AlertMessage('System error', ALERT_TYPE_DANGER));
        break;
    }
    return Observable.throw(error);
  }

  get(id: number): Observable<E> {
    console.debug('get ->');
    //noinspection TypeScriptValidateTypes
    return this.http.get(this.url + '/' + id)
      .map(response => this.extractData(response))
      .catch(error => this.handleError(error));
  }

  delete(entity: E): Observable<{}> {
    console.debug('delete -> ', entity);
    return this.http.delete(this.url + '/' + entity.id.toString())
      .map(response => this.extractData(response))
      .catch(error => this.handleError(error));
  }

  update(entity: E): Observable<E> {
    console.debug('update -> ', entity);
    //noinspection TypeScriptValidateTypes
    return this.http.put(this.url.concat('/').concat(entity.id.toString()), entity)
      .map(response => this.extractData(response))
      .catch(error => this.handleError(error));
  }

  getAll(): Observable<E[]> {
    console.debug('getAll ->');
    return this.http.get(this.url)
      .map(this.extractData)
      .catch(error => this.handleError(error));
  }

  getAllPagination(page: number, items: number): Observable<GetAllPagination<E>> {
    console.debug('getAllPagination ->');
    let params = new URLSearchParams();
    params.set('page', page.toString());
    params.set('items', items.toString());
    let options = new RequestOptions({
      search: params
    });
    return this.http.get(this.url, options)
      .map(this.extractData)
      .catch(error => this.handleError(error));
  }

  save(entity: E): Observable<E> {
    console.debug('save -> ', entity);
    //noinspection TypeScriptValidateTypes
    entity.id = -1; // workaround for backed !!! there must be defined id!
    return this.http.post(this.url, entity)
      .map(response => this.extractData(response))
      .catch(error => this.handleError(error));
  }

}
