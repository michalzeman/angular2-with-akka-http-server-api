import {BaseEntityServiceImpl} from "../../common/services/entity/base-entity.service";
import {Test} from "./test";
import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {DelegateService} from "../../common/services/delegate.service";
import {Observable}     from 'rxjs/Rx';

@Injectable()
export class TestService extends BaseEntityServiceImpl<Test> {

  constructor(delegateService: DelegateService, http: Http) {
    super(delegateService, http, '/tests');
  }


  get(id: string): Observable<Test> {
    console.debug('TestCrudServer.get() -> ', id)
    return Observable.create(observer => {
      let test = new Test();
      test.id = id;
      observer.next(test);
      observer.complete();
    });
  }

  getAll(): Observable<Test[]> {
    console.debug('get');
    return Observable.create(observer => {
      observer.next([
        {id: '1'},
        {id: '2'},
        {id: '3'}
      ]);
    })
  }
}
