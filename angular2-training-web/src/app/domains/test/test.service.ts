import {BaseEntityServiceImpl} from "../../common/services/entity/base-entity.service";
import {Test} from "./test";
import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {DelegateService} from "../../common/services/delegate.service";
import {Observable} from "rxjs/Rx";
import {GetAllPagination} from "../../common/entities/get-all.pagination";

@Injectable()
export class TestService extends BaseEntityServiceImpl<Test> {

  resultList: Array<Test> = [
    {
      id: 1,
      name: "Test"
    },
    {
      id: 2,
      name: "Test"
    },
    {
      id: 3,
      name: "Test"
    }];

  constructor(delegateService: DelegateService, http: Http) {
    super(delegateService, http, '/tests');
  }


  get(id: number): Observable<Test> {
    console.debug('TestCrudServer.get() -> ', id);
    return Observable.create(observer => {
      let test: Test = {id: id, name: "Test"};
      observer.next(test);
      observer.complete();
    });
  }

  getAll(): Observable<Test[]> {
    console.debug('get');
    return Observable.create(observer => {
      observer.next(this.resultList);
    })
  }


  getAllPagination(page: number, items: number): Observable<GetAllPagination<Test>> {
    let paginationResult: GetAllPagination<Test> = {
      result: this.resultList,
      page: page,
      sizePerPage: 10,
      size: 3
    };
    return Observable.of(paginationResult);
  }
}
